# AGENTS.md — Jenkins Email Extension Template Plugin (`emailext-template`)

## Overview
- **Plugin ID**: `emailext-template`
- **Repository**: `jenkinsci/emailext-template-plugin`
- **Java Package**: `org.jenkinsci.plugins.emailext_template`
- **Jenkins Baseline**: `2.479.3` (`org.jenkins-ci.plugins:plugin:6.x`)

Allows administrators to configure reusable global templates for the Email Extension Plugin (`email-ext`) via **Manage Jenkins -> Editable Email Notification Templates** (`/emailexttemplates`) and attach one or more templates to jobs via the **Editable Email Notification Templates** Post-build Action (`ExtendedEmailTemplatePublisher`). Also provides an optional `Configuration Slicing` extension (`ExtEmailTemplateSlicer`).

---

## Build, Test & Verification Commands

### Prerequisites
- **JDK**: Java 17, 21, or 25
- **Maven**: Apache Maven `3.9+`

### Core Maven Commands
```bash
# Full build, JUnit 5 tests, InjectedTest, SpotBugs (Max effort), and Enforcer checks
mvn clean verify

# Run unit tests quickly (skipping InjectedTest during development)
mvn test -Dtest=!InjectedTest

# Run specific test classes
mvn test -Dtest=ExtendedEmailTemplatePublisherTest
mvn test -Dtest=ExtEmailTemplateSlicerTest

# Run SpotBugs and Maven Enforcer checks directly
mvn spotbugs:check enforcer:enforce
```

---

## Architecture & Guardrails

1. **Null-Safe `templateIds` Deserialization (`JENKINS-71094`)**:
   - When a job is saved with an empty template list, Stapler or XStream may pass `null` or blank `TemplateId` entries to `ExtendedEmailTemplatePublisher`.
   - `ExtendedEmailTemplatePublisher` normalizes `templateIds` in its constructor, `setTemplateIds()`, `readResolve()`, and `getTemplateIds()` so `getTemplateIds()` never returns `null` or throws a `NullPointerException` when rendering `/job/<name>/configure` or running builds.
2. **Groovy Stapler View Escaping (`JENKINS-68868`)**:
   - Groovy Stapler views (`index.groovy`, `edit.groovy`, `edit-template.groovy`, `config.groovy`) automatically HTML-escape text nodes (`b(t.name)`, `td(t.description)`) and tag attributes (`confirmationMessage`). Do not wrap values in `Util.escape()` before passing them to Groovy builder tags (which causes double-escaping like `&amp;amp;`).
   - Always URL-encode query parameters such as template IDs in `href` attributes using `java.net.URLEncoder.encode(t.id, "UTF-8")`.
3. **JUnit 5 Exclusively**:
   - `<ban-junit4-imports.skip>false</ban-junit4-imports.skip>` is enforced. Write all tests using JUnit 5 (`org.junit.jupiter.api.*`) and `@WithJenkins`.
