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

def iconFileName = it?.iconFileName

l.layout(permission: app.ADMINISTER, norefresh: true, title: _("Editable Email Template Management")) {
  st.include(page: "sidepanel")
  l.main_panel {
    h1 {
      if (iconFileName != null) {
        l.icon(src: iconFileName, class: "icon-xlg")
        text(" ")
      }
      text(_("Editable Email Template Management"))
    }

    p(_("intro"))
    def descriptor = app.getDescriptorByType(org.jenkinsci.plugins.emailext_template.ExtendedEmailTemplatePublisher.DescriptorImpl.class)
    if (!descriptor.templates.isEmpty()) {
      table(class: "jenkins-table") {
        thead {
          tr {
            th(_("Name"))
            th(_("Description"))
            th(class: "jenkins-table__cell--tight", _("Actions"))
          }
        }
        tbody {
          descriptor.templates.each { t ->
            def encodedId = java.net.URLEncoder.encode(t.id, "UTF-8")
            tr {
              td {
                b(t.name)
              }
              td(t.description)
              td(class: "jenkins-table__cell--tight") {
                div(class: "jenkins-table__cell__button-wrapper") {
                  l.task(
                    icon: "symbol-create-outline plugin-ionicons-api",
                    href: "editTemplate?id=${encodedId}",
                    title: _("Edit template")
                  )
                  l.task(
                    icon: "symbol-trash-outline plugin-ionicons-api",
                    href: "removeTemplate?id=${encodedId}",
                    post: true,
                    requiresConfirmation: true,
                    title: _("Remove template"),
                    confirmationMessage: "Are you sure you want to delete [${t.name}]?"
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
