package org.jenkinsci.plugins.emailext_template.configurationslicing;

import static org.junit.Assert.assertTrue;

import hudson.model.FreeStyleProject;
import hudson.plugins.emailext.ExtendedEmailPublisher;

import org.jenkinsci.plugins.emailext_template.ExtendedEmailPublisherTemplate;
import org.jenkinsci.plugins.emailext_template.ExtendedEmailTemplatePublisher;
import org.jenkinsci.plugins.emailext_template.TemplateId;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by mgaunin on 19/05/16.
 */
public class ExtEmailTemplateSlicerTest {
    private static final String ID_TEMP1 = "Id1";
    private static final String ID_TEMP2 = "Id2";
    private static final String ID_TEMP3 = "Id3";
    private static final String NAME_TEMP1 = "Temp1";
    private static final String NAME_TEMP2 = "Temp2";
    private static final String NAME_TEMP3 = "Temp3";
    private static final String DELIM = ",";

    @Rule
    final public JenkinsRule j = new JenkinsRule();

    private ExtEmailTemplateSlicer.ExtEmailTemplateSlicerSpec spec;
    private ExtendedEmailTemplatePublisher.DescriptorImpl descriptor;

    @Test
    public void testAddTemplate() throws IOException {
        final FreeStyleProject project = createProject("Job", null);
        // Let's check that there is no template yet
        List<String> values = spec.getValues(project);
        assertTrue(values.get(0).equalsIgnoreCase(spec.getDefaultValueString()));
        // Set one template
        spec.setValues(project, Collections.singletonList(NAME_TEMP1));
        values = spec.getValues(project);
        assertTrue(values.get(0).equalsIgnoreCase(NAME_TEMP1));
        ExtendedEmailTemplatePublisher publisher = (ExtendedEmailTemplatePublisher)project.getPublishersList().get(0);
        Collection<TemplateId> ids = publisher.getTemplateIds();
        for (final TemplateId id : ids) {
            assertTrue(ID_TEMP1.equalsIgnoreCase(id.getTemplateId()));
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(NAME_TEMP1).append(DELIM).append(NAME_TEMP2);
        // Set a second template
        spec.setValues(project, Collections.singletonList(sb.toString()));
        values = spec.getValues(project);
        assertTrue(values.get(0).equalsIgnoreCase(sb.toString()));
        ids = publisher.getTemplateIds();
        boolean firstId = true;
        for (final TemplateId id : ids) {
            if (firstId) {
                assertTrue(ID_TEMP1.equalsIgnoreCase(id.getTemplateId()));
                firstId = false;
            }
            else
                assertTrue(ID_TEMP2.equalsIgnoreCase(id.getTemplateId()));
        }
    }

    @Test
    public void testRemoveTemplate() throws IOException {
        final FreeStyleProject project = createProject("Job", null);
        StringBuilder sb = new StringBuilder();
        sb.append(NAME_TEMP1).append(DELIM).append(NAME_TEMP2).append(DELIM).append(NAME_TEMP3);
        // Set 3 templates
        spec.setValues(project, Collections.singletonList(sb.toString()));
        List<String> values = spec.getValues(project);
        assertTrue(values.get(0).equalsIgnoreCase(sb.toString()));
        sb = new StringBuilder();
        sb.append(NAME_TEMP1).append(DELIM).append(DELIM).append(NAME_TEMP3);
        // Remove the second one with typo in the value (2 delimiters)
        spec.setValues(project, Collections.singletonList(sb.toString()));
        values = spec.getValues(project);
        sb = new StringBuilder();
        sb.append(NAME_TEMP1).append(DELIM).append(NAME_TEMP3);
        assertTrue(values.get(0).equalsIgnoreCase(sb.toString()));
        ExtendedEmailTemplatePublisher publisher = (ExtendedEmailTemplatePublisher)project.getPublishersList().get(0);
        Collection<TemplateId> ids = publisher.getTemplateIds();
        boolean firstId = true;
        for (final TemplateId id : ids) {
            if (firstId) {
                assertTrue(id.getTemplateId().equalsIgnoreCase(ID_TEMP1));
                firstId = false;
            }
            else
                assertTrue(id.getTemplateId().equalsIgnoreCase(ID_TEMP3));
        }
        // Remove all templates
        spec.setValues(project, Collections.singletonList(spec.getDefaultValueString()));
        values = spec.getValues(project);
        assertTrue(values.get(0).equalsIgnoreCase(spec.getDefaultValueString()));
        assertTrue(project.getPublishersList().isEmpty());
    }

    @Before
    public void setUp() {
        descriptor = (ExtendedEmailTemplatePublisher.DescriptorImpl)j.jenkins.getDescriptor(ExtendedEmailTemplatePublisher.class);
        descriptor.addTemplate(new ExtendedEmailPublisherTemplate(ID_TEMP1, NAME_TEMP1, "Desc", new ExtendedEmailPublisher()));
        descriptor.addTemplate(new ExtendedEmailPublisherTemplate(ID_TEMP2, NAME_TEMP2, "Desc", new ExtendedEmailPublisher()));
        descriptor.addTemplate(new ExtendedEmailPublisherTemplate(ID_TEMP3, NAME_TEMP3, "Desc", new ExtendedEmailPublisher()));
        spec = new ExtEmailTemplateSlicer.ExtEmailTemplateSlicerSpec();
    }

    @After
    public void tearDown() {
        spec = null;
        descriptor = null;
    }

    private FreeStyleProject createProject(final String name, final List<TemplateId> templateIds)
            throws IOException {
        FreeStyleProject project = j.createFreeStyleProject(name);
        if (templateIds != null && !templateIds.isEmpty()) {
            project.getPublishersList().add(new ExtendedEmailTemplatePublisher(templateIds));
        }
        return project;
    }
}
