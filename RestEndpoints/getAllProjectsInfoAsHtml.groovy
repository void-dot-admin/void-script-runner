import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.transform.BaseScript
import groovy.xml.MarkupBuilder
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@BaseScript CustomEndpointDelegate delegate

/*
    - Rest EndPoint script that gets all the Projects from Jira
    - creates a link where you can visualize pKey, pName etc.
*/

// create & customize the table
def createTable(){
    def writer = new StringWriter()
    def projectManager = ComponentAccessor.getProjectManager()
    def projects = projectManager.getProjectObjects()
    def titleHTML = [style: 'background-color: #282915; color: white; font-weight: normal; font-size: 20; text-align: left']
    def tableBodyHTML = [style: 'background-color: #70716A;']
    new MarkupBuilder(writer).table(style: 'width: 100%;') {
        tr {
            th('Key', titleHTML)
            th('Name', titleHTML)
            th('Lead', titleHTML)
            th('Category', titleHTML)
            th('Components', titleHTML)
            th('Issue Types', titleHTML)
            th('Project Url', titleHTML)
        }
        projects.each { project ->
            tr {
                td(project.key, tableBodyHTML)
                td(project.name, tableBodyHTML)
                td(project.leadUserName, tableBodyHTML)
                td(project.projectCategory?.name, tableBodyHTML)
                td(project.components?.name, tableBodyHTML)
                td(project.getIssueTypes().name, tableBodyHTML)
                td(project.getUrl(), tableBodyHTML)
            }
        }
    }
    writer.toString()
}

// create "allProjects" link to access the HTML table
// the link can only be read by "jira-software-users" or "jira-administrators" groups
allProjectsInfo (httpMethod: "GET", groups: ["jira-software-users", "jira-administrators"]) {
    Response.ok().type(MediaType.TEXT_HTML).entity(createTable()).build()
}
