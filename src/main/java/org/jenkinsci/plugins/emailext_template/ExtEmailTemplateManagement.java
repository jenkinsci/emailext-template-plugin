package org.jenkinsci.plugins.emailext_template;

import hudson.Extension;
import hudson.model.ManagementLink;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.security.Permission;
import java.io.IOException;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.HttpRedirect;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;


@Extension
public class ExtEmailTemplateManagement extends ManagementLink {

    @Override
    public String getIconFileName() {
        return "/plugin/emailext-template/images/mail-mark-read.png";
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
     * @throws IOException
     * @throws ServletException
     */
    public void doAddTemplate(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
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
     * @throws IOException
     * @throws ServletException
     */
    public void doEditTemplate(StaplerRequest req, StaplerResponse rsp, @QueryParameter("id") String templateId) throws IOException, ServletException {
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
     * @throws IOException
     */
    public HttpResponse doRemoveTemplate(StaplerRequest res, StaplerResponse rsp, @QueryParameter("id") String templateId) throws IOException {
        checkPermission(Jenkins.ADMINISTER);
        ExtendedEmailTemplatePublisher.DescriptorImpl descriptor = getPublisherDescriptor();
        descriptor.removeTemplateById(templateId);
        return new HttpRedirect("index");
    }
    
    public HttpResponse doSaveTemplate(StaplerRequest req) {
        checkPermission(Jenkins.ADMINISTER);
        try {
            ExtendedEmailTemplatePublisher.DescriptorImpl descriptor = getPublisherDescriptor();
            
            JSONObject json = req.getSubmittedForm().getJSONObject("template");
            String id = json.getString("id");
            
            ExtendedEmailPublisherTemplate template = descriptor.getTemplateById(id);
            if(template != null) {
                req.bindJSON(template, json);
                template.setPublisher(req.bindJSON(ExtendedEmailPublisher.class, json));
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
        return Jenkins.getInstance().getDescriptorByType(ExtendedEmailTemplatePublisher.DescriptorImpl.class);
    }
    
    private void checkPermission(Permission permission) {
        Jenkins.getInstance().checkPermission(permission);
    }
    
}

