package org.jenkinsci.plugins.emailext_template;


import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.emailext.MatrixTriggerMode;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.emailext.plugins.trigger.AlwaysTrigger;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by acearl on 4/16/2014.
 */
public class ExtendedEmailTemplatePublisherTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("JENKINS-22610")
    public void testRemovedTemplate() throws Exception {
        ExtendedEmailTemplatePublisher.DescriptorImpl descriptor = (ExtendedEmailTemplatePublisher.DescriptorImpl)j.jenkins.getDescriptor(ExtendedEmailTemplatePublisher.class);
        List<EmailTrigger> triggers = new ArrayList<>();
        triggers.add(new AlwaysTrigger(true, false, false, false, "mickeymouse@gmail.com", "", "Test Email", "Howdy!", "", 0, "project"));

        ExtendedEmailPublisher publisher = new ExtendedEmailPublisher("$DEFAULT_RECIPIENTS", "html", "$DEFAULT_SUBJECT", "$DEFAULT_CONTENT", "", "", 0, "$DEFAULT_REPLYTO", false, triggers, MatrixTriggerMode.ONLY_PARENT);

        ExtendedEmailPublisherTemplate template = new ExtendedEmailPublisherTemplate("template1", "Test Template", "Simple test template", publisher);
        descriptor.addTemplate(template);
        ExtendedEmailTemplatePublisher templatePublisher = new ExtendedEmailTemplatePublisher(Collections.singletonList(new TemplateId(template.getId())));

        FreeStyleProject p = j.createFreeStyleProject();
        p.getPublishersList().add(templatePublisher);

        FreeStyleBuild b = j.buildAndAssertSuccess(p);
        // Template should exist, so we shouldn't get the message
        j.assertLogNotContains(Messages.TemplateIdRemoved(template.getId()), b);

        descriptor.removeTemplateById(template.getId());

        b = j.buildAndAssertSuccess(p);
        // Template was removed, so we should get the message
        j.assertLogContains(Messages.TemplateIdRemoved(template.getId()), b);
    }
}
