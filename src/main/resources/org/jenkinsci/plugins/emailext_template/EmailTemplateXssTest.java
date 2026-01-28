package org.jenkinsci.plugins.emailext_template;

import static org.junit.Assert.assertTrue;

import hudson.Util;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlButton;
import org.jvnet.hudson.test.JenkinsRule;
import org.junit.Rule;
import org.junit.Test;

public class EmailTemplateXssTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testTemplateNameEscaping() throws Exception {
        ExtendedEmailTemplatePublisher.DescriptorImpl descriptor =
            jenkinsRule.jenkins.getDescriptorByType(ExtendedEmailTemplatePublisher.DescriptorImpl.class);

        String dangerousName = "xss.::\"'<script>alert(1)</script>";
        String description = "Test description";

        descriptor.templates.add(new ExtendedEmailTemplateTemplate(dangerousName, description));
        HtmlPage page = jenkinsRule.createWebClient().goTo("manage/email-templates");

        String pageHtml = page.asXml();
        String escapedName = Util.escape(dangerousName);
        assertTrue("Template name should be escaped in page HTML",
                pageHtml.contains(escapedName));
        assertTrue("Script tags should be escaped and not executable",
                !pageHtml.contains("<script>alert(1)</script>"));
        assertTrue("Confirmation message should include escaped name",
                pageHtml.contains("Are you sure you want to delete [" + escapedName + "]"));
    }
}
