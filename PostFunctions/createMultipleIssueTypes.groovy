import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLinkTypeManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.context.IssueContextImpl

/*
    Workflow Post-Function that creates a chain IssueTypes (you can add multiple issue types)
    - Custom ScriptRunner post-function
    - add this script as a post-function on a transition that goes "Open" -> "Open" (just an example)
    - the post-function should be placed after "Update change history for an issue and store the issue in the database."
    - the script will run when you press the button transition
    - just change issue types names, links or mandatory customfields
*/

// function for Epic creation
Issue createEpicIssue(String summary) {
    Long projectId = issue.getProjectId()
    def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    def issueTypeId = ComponentAccessor.constantsManager.allIssueTypeObjects.findByName("Epic").id

    def cfRelease = ComponentAccessor.customFieldManager.getCustomFieldObjectsByName("Release related")?.getAt(0)
    def cfEpicName = ComponentAccessor.customFieldManager.getCustomFieldObjectsByName("Epic Name")?.getAt(0)
    def issueContext = new IssueContextImpl(projectId, issueTypeId)
    def cfConfig = cfRelease.getRelevantConfig(issueContext)
    def cfReleaseOption = ComponentAccessor.optionsManager.getOptions(cfConfig)?.find {
        it.toString() == "Yes"
    }?.optionId?.toString()

    def issueInputParameters = ComponentAccessor.issueService.newIssueInputParameters()
            .setProjectId(projectId)
            .setIssueTypeId(issueTypeId)
            .setSummary(summary)
            .addCustomFieldValue(cfRelease.id, cfReleaseOption)
            .addCustomFieldValue(cfEpicName.id, summary)
            .setReporterId(user.name)
            .setAssigneeId(user.name)

    def validationResult = ComponentAccessor.issueService.validateCreate(user, issueInputParameters)
    if ( ! validationResult.isValid() ){
        throw new Exception("Failed to create Epic: ${validationResult.errorCollection}")
    }
    def result = ComponentAccessor.issueService.create(user, validationResult)
    if ( ! result.isValid() ) {
        throw new Exception("Failed to create Epic: ${result.errorCollection}")
    }
    return result.issue
}

// function to create link between Epic & issue (Release in my case - issue from where we execute the script)
void linkEpicWithRelease(Issue rIssue, Issue epicIssue) {
    def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    def issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager)
    def linkType = issueLinkTypeManager.issueLinkTypes.findByName("Parent")

    ComponentAccessor.issueLinkManager.createIssueLink(epicIssue.id,rIssue.id,linkType.id,1L,user)
}

// function for Task creation
Issue createTaskIssue(String summary) {
    Long projectId = issue.getProjectId()
    def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    def issueTypeId = ComponentAccessor.constantsManager.allIssueTypeObjects.findByName("Task").id

    def issueInputParameters = ComponentAccessor.issueService.newIssueInputParameters()
            .setProjectId(projectId)
            .setIssueTypeId(issueTypeId)
            .setSummary(summary)
            .setReporterId(user.name)
            .setAssigneeId(user.name)

    def validationResult = ComponentAccessor.issueService.validateCreate(user, issueInputParameters)
    if (!validationResult.isValid()) {
        throw new Exception("Failed to create Task: ${validationResult.errorCollection}")
    }
    def result = ComponentAccessor.issueService.create(user, validationResult)
    if (!result.isValid()) {
        throw new Exception("Failed to create Task: ${result.errorCollection}")
    }
    return result.issue
}

// function to create link between Epic & task
void linkEpicwithTask(Issue taskIssue, Issue epicIssue) {
    def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    def issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager)
    def linkType = issueLinkTypeManager.issueLinkTypes.findByName("Link")

    ComponentAccessor.issueLinkManager.createIssueLink(taskIssue.id, epicIssue.id, linkType.id, 1L, user)
}


// Create Epic
def issueSummary = issue.getSummary()
def epicIssue = createEpicIssue("Epic for: ${issueSummary}")

// Create link between Epic & issue (Release in my case - issue from where we execute the script)
linkEpicWithRelease(epicIssue, issue)

// Create Task
def taskIssue = createTaskIssue("Task to created requirements for: ${issueSummary}")

// Create link between Task & Epic
linkEpicwithTask(taskIssue, epicIssue)
