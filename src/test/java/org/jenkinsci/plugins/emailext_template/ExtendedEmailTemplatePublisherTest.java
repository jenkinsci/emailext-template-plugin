package org.jenkinsci.plugins.emailext_template;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.emailext.MatrixTriggerMode;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.emailext.plugins.trigger.AlwaysTrigger;
import org.jenkinsci.plugins.emailext_template.ExtendedEmailPublisherTemplate;
import org.jenkinsci.plugins.emailext_template.ExtendedEmailTemplatePublisher;
import org.jenkinsci.plugins.emailext_template.Messages;
import org.jenkinsci.plugins.emailext_template.TemplateId;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import org.jvnet.hudson.test.Issue;

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
        List<EmailTrigger> triggers = new ArrayList<EmailTrigger>();
        triggers.add(new AlwaysTrigger(true, false, false, false, "mickeymouse@gmail.com", "", "Test Email", "Howdy!", "", 0, "project"));

        ExtendedEmailPublisher publisher = new ExtendedEmailPublisher("$DEFAULT_RECIPIENTS", "html", "$DEFAULT_SUBJECT", "$DEFAULT_CONTENT", "", "", 0, "$DEFAULT_REPLYTO", false, triggers, MatrixTriggerMode.ONLY_PARENT);

        ExtendedEmailPublisherTemplate template = new ExtendedEmailPublisherTemplate("template1", "Test Template", "Simple test template", publisher);
        descriptor.addTemplate(template);
        ExtendedEmailTemplatePublisher templatePublisher = new ExtendedEmailTemplatePublisher(Collections.singletonList(new TemplateId(template.getId())));

        AbstractProject p = j.createFreeStyleProject("Test");
        p.getPublishersList().add(templatePublisher);

        AbstractBuild b = (AbstractBuild)p.scheduleBuild2(0, new Cause.UserCause()).get();
        assertTrue("Template should exist, so we shouldn't get the message",
                !b.getLog(100).contains(Messages.TemplateIdRemoved(template.getId())));

        descriptor.removeTemplateById(template.getId());

        b = (AbstractBuild)p.scheduleBuild2(0, new Cause.UserCause()).get();
        assertTrue("Template was removed, so we should get the message",
                b.getLog(100).contains(Messages.TemplateIdRemoved(template.getId())));
    }
}
