/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jenkinsci.plugins.emailext_template;

import hudson.plugins.emailext.ExtendedEmailPublisher;
import java.io.Serializable;
import org.codehaus.plexus.util.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author acearl
 */
public class ExtendedEmailPublisherTemplate implements Serializable {
    
    private String id;
    
    private String name;
    
    private String description;
    
    private ExtendedEmailPublisher publisher;
    
    @DataBoundConstructor
    public ExtendedEmailPublisherTemplate(String id, String name, String description, ExtendedEmailPublisher publisher) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.publisher = publisher;
    }
    
    public ExtendedEmailPublisherTemplate() {

    }
    
    public String getId() {
        if(StringUtils.isBlank(id)) {
            id = generateId();
        }
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ExtendedEmailPublisher getPublisher() {
        return publisher;
    }
    
    public void setPublisher(ExtendedEmailPublisher publisher) {
        this.publisher = publisher;
    }
    
    private String generateId() {
        return "emailext-template-" + System.currentTimeMillis();
    }
    
}
