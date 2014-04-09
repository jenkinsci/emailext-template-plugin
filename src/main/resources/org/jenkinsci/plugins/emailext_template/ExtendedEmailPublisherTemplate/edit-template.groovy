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

package org.jenkinsci.plugins.emailext_template

def f = namespace("/lib/form")
def st = namespace("jelly:stapler")

f.entry(title:_("ID")) {
  f.textbox(readonly:"readonly", name:"template.id", value:template.id)
}
f.entry(title:_("Name")) {
  f.textbox(name:"template.name", value:template.name)
}
f.entry(title:_("Description")) {
    f.textbox(name:"template.description", value:template.description)
}

def descriptor = app.getDescriptorByType(hudson.plugins.emailext.ExtendedEmailPublisherDescriptor.class)

set("instance", template.publisher)
set("descriptor", descriptor)
st.include(class:hudson.plugins.emailext.ExtendedEmailPublisher.class, it:template.publisher, page:descriptor.configPage)