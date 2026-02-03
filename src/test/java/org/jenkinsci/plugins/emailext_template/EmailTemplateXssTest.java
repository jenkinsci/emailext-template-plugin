package org.jenkinsci.plugins.emailext_template;

import hudson.Util;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.emailext.MatrixTriggerMode;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WithJenkins
class EmailTemplateXssTest {

    @Test
    void testTemplateNameEscaping(JenkinsRule j) throws Exception {
        ExtendedEmailTemplatePublisher.DescriptorImpl descriptor =
            j.jenkins.getDescriptorByType(ExtendedEmailTemplatePublisher.DescriptorImpl.class);

        String dangerousName = "xss.::\"'<script>alert(1)</script>";
        String description = "Test description";
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

        ExtendedEmailPublisherTemplate template = new ExtendedEmailPublisherTemplate(
                "test-xss-template",
                dangerousName,
                description,
                publisher);
        descriptor.addTemplate(template);
        HtmlPage page = j.createWebClient().goTo("emailexttemplates");

        String pageHtml = page.asXml();
        assertFalse(pageHtml.contains("<script>alert(1)</script>"),
            "Script tags should be escaped and not executable");
        assertTrue(pageHtml.contains("Test description"),
            "Template description should be present on the page");
    }
}
