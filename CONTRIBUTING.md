# Contributing to the Email Extension Template Plugin

**Never report security issues on GitHub or other public channels (Gitter/Twitter/etc.), follow the instruction from [Jenkins Security](https://jenkins.io/security/) to report it on [Jenkins Jira](https://www.jenkins.io/participate/report-issue/redirect/#18764)**

## Why should you contribute

You can contribute in many ways, and whatever you choose we're grateful!
Source code contribution is the obvious one but we also need your feedback and if you don't really want to participate in the implementation directly you may still have great ideas about features we need (or should get rid of).

## Contributing source code

Plugin source code is hosted on [GitHub](https://github.com/jenkinsci/emailext-template-plugin).
New feature proposals and bug fix proposals should be submitted as
[GitHub pull requests](https://help.github.com/articles/creating-a-pull-request).
Your pull request will be evaluated by the [Jenkins job](https://ci.jenkins.io/job/Plugins/job/emailext-template-plugin/).

Before submitting your change, please assure that you've added tests
which verify your change.

## Code coverage

[JaCoCo code coverage](https://www.jacoco.org/jacoco/) reporting is available as a maven target and can be displayed by the [Jenkins warnings next generation plugin](https://plugins.jenkins.io/warnings-ng/).
Please try to improve code coverage with tests when you submit.
* `mvn -P enable-jacoco clean install jacoco:report` to report code coverage with JaCoCo.

[OpenClover code coverage](https://openclover.org/) reporting is available as a maven target and can be displayed by the [Jenkins clover plugin](https://plugins.jenkins.io/clover/).
* `mvn clover:setup clover:instrument test clover:clover` to report code coverage with OpenClover.

Please don't introduce new spotbugs output.
* `mvn spotbugs:check` to analyze project using [Spotbugs](https://spotbugs.github.io)
* `mvn spotbugs:gui` to review report using GUI

## Maintaining automated tests

Automated tests are run as part of the `verify` phase.
Run automated tests with the command:

```
$ mvn clean verify
```
