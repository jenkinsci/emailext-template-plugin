package org.jenkinsci.plugins.emailext_template.configurationslicing;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.Publisher;

import org.jenkinsci.plugins.emailext_template.ExtendedEmailPublisherTemplate;
import org.jenkinsci.plugins.emailext_template.ExtendedEmailTemplatePublisher;
import org.jenkinsci.plugins.emailext_template.Messages;
import org.jenkinsci.plugins.emailext_template.TemplateId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Created by mgaunin on 18/05/16.
 */
@Extension(optional = true)
public class ExtEmailTemplateSlicer extends UnorderedStringSlicer<AbstractProject<?, ?>> {
    public ExtEmailTemplateSlicer() {
        super(new ExtEmailTemplateSlicerSpec());
    }

    public static class ExtEmailTemplateSlicerSpec
            extends UnorderedStringSlicer.UnorderedStringSlicerSpec<AbstractProject<?, ?>> {
        private static final Logger LOGGER = Logger.getLogger(ExtEmailTemplateSlicerSpec.class.getName());
        private static final List<String> SUPPORTED_DELIM = Arrays.asList("\n", ",", ";");

        @Override
        public String getName() {
            return "Editable Email Notification Templates";
        }

        @Override
        public String getUrl() {
            return "emailexttemplate";
        }

        protected final ExtendedEmailTemplatePublisher getExtendedEmailTemplatePublisher(final AbstractProject<?, ?> project) {
            for (final Publisher publisher : project.getPublishersList()) {
                if (publisher instanceof ExtendedEmailTemplatePublisher) {
                    return (ExtendedEmailTemplatePublisher) publisher;
                }
            }
            return null;
        }

        @Override
        public List<AbstractProject<?, ?>> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
        }

        @Override
        public List<String> getValues(AbstractProject<?, ?> project) {
            final List<String> values = new ArrayList<>();
            final StringBuilder value = new StringBuilder();
            final ExtendedEmailTemplatePublisher publisher = getExtendedEmailTemplatePublisher(project);
            if (publisher != null) {
                final Collection<TemplateId> ids = publisher.getTemplateIds();
                if (ids.size() > 0) {
                    final ExtendedEmailTemplatePublisher.DescriptorImpl descriptor = publisher.getDescriptor();
                    int count = 0;
                    for (final TemplateId id : ids) {
                        final ExtendedEmailPublisherTemplate template = descriptor.getTemplateById(id.getTemplateId());
                        if (template != null) {
                            if (count++ > 0)
                                value.append(',');
                            value.append(template.getName());
                        } else {
                            LOGGER.warning(Messages.TemplateNameRemoved(id));
                        }
                    }
                } else {
                    value.append(getDefaultValueString());
                }
            } else {
                value.append(getDefaultValueString());
            }
            values.add(value.toString());
            return values;
        }

        @Override
        public String getName(AbstractProject<?, ?> project) {
            return project.getFullName();
        }

        @Override
        public boolean setValues(AbstractProject<?, ?> project, List<String> list) {
            if (list.isEmpty()) {
                return false;
            }
            final List<TemplateId> templateIds = new ArrayList<>();
            ExtendedEmailTemplatePublisher publisher = getExtendedEmailTemplatePublisher(project);
            boolean defaultValue = (list.size() == 1)
                    && (getDefaultValueString().equalsIgnoreCase(list.get(0)) || list.get(0).trim().isEmpty());
            if (defaultValue) {
                if (publisher != null) {
                    project.getPublishersList().remove(publisher);
                    return true;
                } else {
                    return false;
                }
            } else if (publisher == null) {
                publisher = new ExtendedEmailTemplatePublisher(templateIds);
                project.getPublishersList().add(publisher);
            }
            ExtendedEmailTemplatePublisher.DescriptorImpl descriptor = publisher.getDescriptor();
            for (final String value : list) {
                // Take into account multiple values
                String delim = findDelimiter(value);
                if (delim != null) {
                    final StringTokenizer st = new StringTokenizer(value, delim);
                    while (st.hasMoreElements()) {
                        final String token = st.nextToken().trim();
                        if (token.length() > 0)
                            retrieveTemplate(templateIds, token, descriptor);
                    }
                } else {
                    retrieveTemplate(templateIds, value, descriptor);
                }
            }
            publisher.setTemplateIds(templateIds);
            try {
                project.save();
            } catch (IOException e) {
                LOGGER.throwing(this.getClass().getName(), "setValues", e);
                return false;
            }
            return true;
        }

        private String findDelimiter(final String value) {
            String delim = null;
            for (final String supDelim : SUPPORTED_DELIM) {
                if (value.contains(supDelim)) {
                    delim = supDelim;
                    break;
                }
            }
            return delim;
        }

        private void retrieveTemplate(final List<TemplateId> templateIds, final String name,
                                      final ExtendedEmailTemplatePublisher.DescriptorImpl descriptor) {
            final ExtendedEmailPublisherTemplate template = descriptor.getTemplateByName(name);
            if (template != null) {
                templateIds.add(new TemplateId(template.getId()));
            } else {
                LOGGER.warning(Messages.TemplateNameRemoved(name));
            }
        }

        @Override
        public String getDefaultValueString() {
            return "(Disabled)";
        }
    }
}
