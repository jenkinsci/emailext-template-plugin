package org.jenkinsci.plugins.emailext_template;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.emailext.MatrixTriggerMode;
import java.util.Collections;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class EmailTemplateManagementXssTest {

    @Test
    @Issue("JENKINS-68868")
    void testTemplateManagementEscapingAndIdEncoding(JenkinsRule j) throws Exception {
        ExtendedEmailTemplatePublisher.DescriptorImpl descriptor =
                j.jenkins.getDescriptorByType(ExtendedEmailTemplatePublisher.DescriptorImpl.class);

        ExtendedEmailPublisher publisher = new ExtendedEmailPublisher(
                "$DEFAULT_RECIPIENTS",
                "html",
                "$DEFAULT_SUBJECT",
                "$DEFAULT_CONTENT",
                "",
                "",
                0,
                "$DEFAULT_REPLYTO",
                "$DEFAULT_FROM",
                false,
                Collections.emptyList(),
                MatrixTriggerMode.ONLY_PARENT,
                false,
                Collections.emptyList());

        String dangerousName = "Build & Test \"'<script>alert(1)</script>";
        String description = "Template description with <script>alert(2)</script>";
        ExtendedEmailPublisherTemplate template =
                new ExtendedEmailPublisherTemplate("custom id&1", dangerousName, description, publisher);
        descriptor.addTemplate(template);

        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage indexPage = wc.goTo("emailexttemplates");
            String rawHtml = indexPage.getWebResponse().getContentAsString();

            // Script payloads must be HTML-escaped in the raw response
            assertFalse(rawHtml.contains("<script>alert(1)</script>"), "Template name script tags must be escaped");
            assertFalse(
                    rawHtml.contains("<script>alert(2)</script>"), "Template description script tags must be escaped");

            // Visible text must not be double-escaped (e.g. '&' remains '&' in DOM text, not '&amp;')
            String visibleText = indexPage.asNormalizedText();
            assertTrue(visibleText.contains(dangerousName), "Template name should render without double-escaping");
            assertFalse(visibleText.contains("&amp;"), "Visible text must not contain double-escaped HTML entities");

            // Query parameter template IDs must be URL-encoded
            assertTrue(
                    rawHtml.contains("editTemplate?id=custom+id%261"),
                    "Edit link should URL-encode the template ID");
            assertTrue(
                    rawHtml.contains("removeTemplate?id=custom+id%261"),
                    "Remove link should URL-encode the template ID");
        } finally {
            descriptor.removeTemplateById("custom id&1");
        }
    }
}
