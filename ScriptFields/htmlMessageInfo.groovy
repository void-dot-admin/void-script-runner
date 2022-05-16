import groovy.xml.MarkupBuilder

/*
    - Scripted field that adds an HTML message as INFO
    - add this script field to VIEW screen of choosed issueType
*/

def writer = new StringWriter()
def html = new MarkupBuilder (writer)

//build HTML for the field output, in user profile language
if ( issue.issueType.name == "Release" &&
        issue.status.name in ["Open", "In Progress"]) {

    html.div ( class: "aui-message aui-message-info" ) {
        p {
            b( "Info:" )
        }
        p {
            b ( //bold
                    "Type your text here..."  )   
            br() //break
            a ( //a = hyperlink
                    "Type your text here..."  )
            br()
            mkp.yield ( 
                    "Type your text here..."  )
        }
    }
}
return writer.toString () ?: null
