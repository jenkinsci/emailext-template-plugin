package org.jenkinsci.plugins.emailext_template;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class ExtEmailTemplateManagementUiTest {

    @Test
    @Issue("JENKINS-69810")
    void testTemplateManagementTableAndEditPagesRenderCleanly(JenkinsRule j) throws Exception {
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

        ExtendedEmailPublisherTemplate template =
                new ExtendedEmailPublisherTemplate("ui-test-id", "UI Test Template", "UI test description", publisher);
        descriptor.addTemplate(template);

        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage indexPage = wc.goTo("emailexttemplates");
            assertEquals(200, indexPage.getWebResponse().getStatusCode());
            String rawHtml = indexPage.getWebResponse().getContentAsString();
            assertTrue(rawHtml.contains("jenkins-table"), "Management page should use standard jenkins-table layout");

            HtmlPage addPage = wc.goTo("emailexttemplates/addTemplate");
            assertEquals(200, addPage.getWebResponse().getStatusCode());

            HtmlPage editPage = wc.goTo("emailexttemplates/editTemplate?id=ui-test-id");
            assertEquals(200, editPage.getWebResponse().getStatusCode());
            assertTrue(editPage.asNormalizedText().contains("Edit Email Template"));
        } finally {
            descriptor.removeTemplateById("ui-test-id");
        }
    }
}
