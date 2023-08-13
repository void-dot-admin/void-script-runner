import groovy.xml.MarkupBuilder

def writer = new StringWriter()
def html = new MarkupBuilder (writer)


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
