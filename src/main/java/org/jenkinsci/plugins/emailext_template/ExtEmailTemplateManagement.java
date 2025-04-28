package org.jenkinsci.plugins.emailext_template;

import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.Extension;
import hudson.model.ManagementLink;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.security.Permission;

import jenkins.model.Jenkins;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.HttpRedirect;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.io.IOException;

import jakarta.servlet.ServletException;

@Extension
public class ExtEmailTemplateManagement extends ManagementLink {

    @Override
    public String getIconFileName() {
        return "symbol-mail-open-outline plugin-ionicons-api";
    }

    @Override
    public String getUrlName() {
        return "emailexttemplates";
    }

    @Override
    public String getDisplayName() {
        return Messages.ExtEmailTemplateManagement_DisplayName();
    }
    
    @Override 
    public String getDescription() {
        return Messages.ExtEmailTemplateManagement_Description();
    }
    
     /**
     * Creates a new ExtendedEmailPublisher and forwards the request to "edit.groovy".
     * 
     * @param req
     *            request
     * @param rsp
     *            response
     */
    public void doAddTemplate(StaplerRequest2 req, StaplerResponse2 rsp) throws IOException, ServletException {
        checkPermission(Jenkins.ADMINISTER);   
        
        ExtendedEmailPublisherTemplate template = new ExtendedEmailPublisherTemplate();        
        req.setAttribute("template", template);
        req.getView(this, "edit").forward(req, rsp);
    }
    
    /**
     * Loads the template by its id and forwards the request to "edit.jelly".
     * 
     * @param req
     *            request
     * @param rsp
     *            response
     * @param templateId
     *            the id of the template to be loaded in to the edit view.
     */
    public void doEditTemplate(StaplerRequest2 req, StaplerResponse2 rsp, @QueryParameter("id") String templateId) throws IOException, ServletException {
        checkPermission(Jenkins.ADMINISTER);

        ExtendedEmailTemplatePublisher.DescriptorImpl descriptor = getPublisherDescriptor();        
        ExtendedEmailPublisherTemplate template = descriptor.getTemplateById(templateId);
        if (template != null) {
            req.setAttribute("template", template);
            req.getView(this, "edit.jelly").forward(req, rsp);
        } else {
            req.getView(this, "index").forward(req, rsp);
        }
    }
    
    /**
     * Removes a template 
     * 
     * @param res
     *            response
     * @param rsp
     *            request
     * @param templateId
     *            the id of the template to be removed
     * @return forward to 'index'
     */
    @RequirePOST
    public HttpResponse doRemoveTemplate(StaplerRequest2 res, StaplerResponse2 rsp, @QueryParameter("id") String templateId) {
        checkPermission(Jenkins.ADMINISTER);
        ExtendedEmailTemplatePublisher.DescriptorImpl descriptor = getPublisherDescriptor();
        descriptor.removeTemplateById(templateId);
        return new HttpRedirect("index");
    }

    @RequirePOST
    public HttpResponse doSaveTemplate(StaplerRequest2 req) {
        checkPermission(Jenkins.ADMINISTER);
        try {
            ExtendedEmailTemplatePublisher.DescriptorImpl descriptor = getPublisherDescriptor();
            
            JSONObject json = req.getSubmittedForm().getJSONObject("template");
            String id = json.getString("id");
            
            ExtendedEmailPublisherTemplate template = descriptor.getTemplateById(id);
            if(template != null) {
                req.bindJSON(template, json);
                template.setPublisher(req.bindJSON(ExtendedEmailPublisher.class, json));
                descriptor.save();
            } else {
                template = req.bindJSON(ExtendedEmailPublisherTemplate.class, json);
                template.setPublisher(req.bindJSON(ExtendedEmailPublisher.class, json));
                descriptor.addTemplate(template);
            }
        } catch (ServletException e) {
            e.printStackTrace();
        }
        
        return new HttpRedirect("index");
    }
    
    private ExtendedEmailTemplatePublisher.DescriptorImpl getPublisherDescriptor() {
        return Jenkins.get().getDescriptorByType(ExtendedEmailTemplatePublisher.DescriptorImpl.class);
    }
    
    private void checkPermission(Permission permission) {
        Jenkins.get().checkPermission(permission);
    }
    
    @NonNull
    @Override
    public Category getCategory() {
        return Category.CONFIGURATION;
     }
}

