import groovy.xml.MarkupBuilder

/*
    * Make sure to use "Test Field (multi-line)"
    * as Template in the scripted field
*/

def writer = new StringWriter()
def html = new MarkupBuilder (writer)

// show the message based on IssueType & Status
if ( issue.issueType.name == "Epic" && issue .status.name in ["To Do"] ) {

    // Build HTML for script field output
    html.div(class: "aui-message aui-message-warning") {
        p {
            b ( "Warning:" )
        }
        p {
            b ( "Please make sure that the Code is approved!" )
            br()
        mkp.yield ( "Any changes without approval will be reverted and ticket cancelled." )
            br()
        } 
    }
}

return writer.toString() ?: null
