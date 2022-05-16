import com.atlassian.jira.component.ComponentAccessor
import static com.atlassian.jira.issue.IssueFieldConstants.ISSUE_TYPE
import com.atlassian.jira.security.roles.ProjectRoleManager

/*
Behaviour (Initializer script) for all project and issue types:
- Restrict available issue types based on project role
*/

// Get current user
def currentUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser

/*** Restrict available issue types based on project role ***/
// Issue types that are allowed for everyone (eg: Task) or whatever you want available for everyone
// don't need to be included in the permission mapping.
def ISSUE_TYPES_PERMISSION = [
    "Developer": ["Sub-task"],
    "Tech Lead": ["Story", "Sub-task", "Bug"],
    "Tester": ["Bug", "Sub-task"],
    "Scrum Master": ["Story", "Epic", "Sub-task"],
    "Project Owner": ["Requirement", "Story", "Sub-task", "Epic", "Bug"],
    "Project Manager": ["Requirement", "Story", "Sub-task", "Epic", "Release", "Bug"]
]

// If current user is a Jira administrator, show all issue types
def isUserJiraAdministrator = ComponentAccessor.groupManager.isUserInGroup(currentUser, "jira-administrators")
if ( isUserJiraAdministrator ) {
    return // do nothing
}

// Get roles of the current user in the project
def projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager)
def userRoleNames = projectRoleManager.getProjectRoles(currentUser, issueContext.projectObject)*.name

// Get issue type objects
def allRestrictedIssueTypes = ISSUE_TYPES_PERMISSION.values().flatten().unique()
def allIssueTypes = ComponentAccessor.constantsManager.allIssueTypeObjects
def issueTypeField = getFieldById(ISSUE_TYPE)

def availableIssueTypes = []
availableIssueTypes.addAll(allIssueTypes.findAll{!(it.name in allRestrictedIssueTypes) })
userRoleNames.each { role ->
    availableIssueTypes.addAll(allIssueTypes.findAll{ it.name in ISSUE_TYPES_PERMISSION[(role)] })
}

issueTypeField.setFieldOptions(availableIssueTypes)
