/*
The MIT License

Copyright (c) 2014, Alex Earl

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

def l = namespace("/lib/layout")
def st = namespace("jelly:stapler")
def j = namespace("jelly:core")
//<j:jelly xmlns:j="jelly:core" xmlns:d="jelly:define"
//	xmlns:t="/lib/hudson" xmlns:f="/lib/form">

def iconFileName = it?.iconFileName
        
l.layout(permission:app.ADMINISTER, norefresh: true) {
  st.include(page:"sidepanel")
  l.main_panel {
    h1(_("Editable Email Template Management")) {
      img(src:"${rootURL}/${iconFileName}", alt:"")      
    }
    
    p(_("intro"))
    def descriptor = app.getDescriptorByType(org.jenkinsci.plugins.emailext_template.ExtendedEmailTemplatePublisher.DescriptorImpl.class)
    table(class:"pane", style:"border-top: thin inset darkgray") {
      descriptor.templates.each { t ->      
        tr(valign:"center", style:"border-top: thin inset darkgray") {
          td(width:"32") {
            l.task(icon:"icon-notepad icon-sm", href:"editTemplate?id=${t.id}", title:_("Edit template")+" "+t.name)
            l.task(icon:"icon-edit-delete icon-sm", href:"removeTemplate?id=${t.id}", onclick:"return emailexttemplate_confirmDelete('${t.name}', '${t.id}')", title:_("Remove template")+" "+t.name)
            form(method: "post", action: "removeTemplate", id:"removeForm") {
              input(type: "hidden", "name": "id", id:"removeId")
            }
          }
          td {
            b(t.name)
          }
        }
        tr {
          td()
          td(t.description)
        }	              
      }
    }
    
    script("function emailexttemplate_confirmDelete(name, id) {\n" +
        "if (confirm(\"Are you sure you want to delete [\"+name+\"]?\")) {\n" +
            "document.getElementById('removeId').value = id;\n" +
            "document.getElementById('removeForm').submit();\n" +
        "}\n" +
        "return false;\n" +
        "}\n")
  }
}
