package org.jenkinsci.plugins.emailext_template;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by slide on 4/11/2014.
 */
public class TemplateId {

    private String templateId;

    @DataBoundConstructor
    public TemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateId() {
        return templateId;
    }
}
