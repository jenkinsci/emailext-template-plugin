package org.jenkinsci.plugins.emailext_template;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author acearl
 */
public class ExtendedEmailTemplatePublisher extends Notifier /*implements MatrixAggregatable*/ {

    private List<TemplateId> templateIds;

    @DataBoundConstructor
    public ExtendedEmailTemplatePublisher(List<TemplateId> templateIds) {
        this.templateIds = normalizeTemplateIds(templateIds);
    }

    private static List<TemplateId> normalizeTemplateIds(List<TemplateId> list) {
        List<TemplateId> normalized = new ArrayList<>();
        if (list != null) {
            for (TemplateId item : list) {
                if (item != null
                        && item.getTemplateId() != null
                        && !item.getTemplateId().isBlank()) {
                    normalized.add(item);
                }
            }
        }
        return normalized;
    }

    protected Object readResolve() {
        this.templateIds = normalizeTemplateIds(this.templateIds);
        return this;
    }

    public Collection<TemplateId> getTemplateIds() {
        if (templateIds == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(templateIds);
    }

    public void setTemplateIds(List<TemplateId> list) {
        templateIds = normalizeTemplateIds(list);
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        boolean result = true;
        for (TemplateId template : getTemplateIds()) {
            ExtendedEmailPublisherTemplate t = getDescriptor().getTemplateById(template.getTemplateId());
            if (t != null) {
                result &= t.getPublisher().prebuild(build, listener);
            } else {
                listener.getLogger().println(Messages.TemplateIdRemoved(template.getTemplateId()));
            }
        }

        return result;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        boolean result = true;
        for (TemplateId template : getTemplateIds()) {
            ExtendedEmailPublisherTemplate t = getDescriptor().getTemplateById(template.getTemplateId());
            if (t != null) {
                result &= t.getPublisher().perform(build, launcher, listener);
            } else {
                listener.getLogger().println(Messages.TemplateIdRemoved(template.getTemplateId()));
            }
        }
        return result;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) Jenkins.get().getDescriptor(getClass());
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private List<ExtendedEmailPublisherTemplate> templates = new ArrayList<>();

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> type) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Editable Email Notification Templates";
        }

        public List<ExtendedEmailPublisherTemplate> getTemplates() {
            return templates;
        }

        public void addTemplate(ExtendedEmailPublisherTemplate template) {
            if (getTemplateById(template.getId()) == null) {
                templates.add(template);
                save();
            }
        }

        public void removeTemplateById(String id) {
            ExtendedEmailPublisherTemplate t = getTemplateById(id);
            if (t != null) {
                templates.remove(t);
                save();
            }
        }

        public ExtendedEmailPublisherTemplate getTemplateById(String id) {
            if (id == null) {
                return null;
            }
            ExtendedEmailPublisherTemplate template = null;
            for (ExtendedEmailPublisherTemplate t : templates) {
                if (id.equals(t.getId())) {
                    template = t;
                    break;
                }
            }
            return template;
        }

        public ExtendedEmailPublisherTemplate getTemplateByName(String name) {
            if (name == null) {
                return null;
            }
            ExtendedEmailPublisherTemplate template = null;
            for (ExtendedEmailPublisherTemplate t : templates) {
                if (t.getName() != null && t.getName().equalsIgnoreCase(name)) {
                    template = t;
                    break;
                }
            }
            return template;
        }
    }
}
