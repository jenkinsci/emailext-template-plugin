
// Namespaces
l = namespace("/lib/layout")
st = namespace("jelly:stapler")
j = namespace("jelly:core")
t = namespace("/lib/hudson")
f = namespace("/lib/form")
d = namespace("jelly:define")


//def templates = hudson.plugins.emailext.plugins.EmailTrigger.all()
def configured = instance != null


f.entry(title: _("Templates"), description: _("The templates to use for sending emails")) {
    f.repeatable(var: "template", field="templates", noAddButton: true, minimum:1) {
        table(width: "100%") {
            f.entry {
                select(name:"templateId") {
                    descriptor.templates.each { aTemplate ->
                        if(template != null && template.id==aTemplate.id) {
                            option(value: aTemplate.id, selected: "selected", "${aTemplate.name} - ${aTemplate.description}")
                        } else {
                            option(value: aTemplate.id, "${aTemplate.name} - ${aTemplate.description}")
                        }                        
                    }
                }
            }
            
            f.entry {
                div(align:"right") {
                    input(type:"button", value:_("Add Template"), class:"repeatable-add show-if-last")
                    input(type:"button", value:_("Delete"), class:"repeatable-delete show-if-not-only", style:"margin-left: 1em;")
                }
            }
        }        
    }
}