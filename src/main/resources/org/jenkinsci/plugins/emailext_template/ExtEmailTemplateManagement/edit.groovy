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
def f = namespace("/lib/form")
def st = namespace("jelly:stapler")

//<j:jelly xmlns:j="jelly:core" xmlns:st="jelly:stapler" xmlns:d="jelly:define" xmlns:l="/lib/layout" xmlns:t="/lib/hudson" xmlns:f="/lib/form">
l.layout(norefresh:"true") {
  st.include(page:"sidepanel")
  l.main_panel {
    h1 {
      img(width:"48", height:"48", src:"${imagesURL}/16x16/document_edit.gif")
      img(width:"16", height:"16", src:"${imagesURL}/16x16/empty.gif")
      _("Edit Email Template")
    }
    f.form(method:"post", action:"saveTemplate") {
      f.section(title:_("the template"), name:"template") {
        include(template, "edit-template")
	f.block {
          f.submit(value:_("Submit"))
        }
      }
    }
  }
}
