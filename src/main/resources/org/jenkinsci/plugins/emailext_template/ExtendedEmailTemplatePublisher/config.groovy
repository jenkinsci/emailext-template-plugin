
// Namespaces
l = namespace("/lib/layout")
st = namespace("jelly:stapler")
j = namespace("jelly:core")
t = namespace("/lib/hudson")
f = namespace("/lib/form")
d = namespace("jelly:define")

def configured = instance != null

f.entry(title: _("Templates"), description: _("The templates to use for sending emails")) {
    f.repeatable(items: instance?.templateIds, var: "template", noAddButton: true, minimum:1, name:"templateIds") {
        table(width: "100%") {
            f.entry {
                select(name:"templateId") {
                    descriptor.templates.each { aTemplate ->
                        f.option(value: aTemplate.id, selected: (template != null && template.templateId==aTemplate.id), "${aTemplate.name} - ${aTemplate.description}")
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
