import com.atlassian.jira.issue.AttachmentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.attachment.Attachment

/**
    *Post-function script that creates a Sub-task issue type with name "Sub-task"
    *copy values & attachments from parent issue type
    *This version is updated to use the HAPI feature from Scriptrunner
*/

// the issue type name for the new issue* must be Sub-task to work
final issueTypeName = "Sub-task"

// get the logged in user
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser

// get the field values from parent
def priorityName = issue.getPriority().name
def parentComponents = issue.getComponents().toListString()
def parentAssignee = issue.getAssignee()
def parentTaskChoiceValue = issue.getCustomFieldValue("Task choice")
def parentIssue = ComponentAccessor.issueManager.getIssueByCurrentKey(issue.key)
def parentSummary = issue.getSummary()
def parentDescription = issue.getDescription()

Long[] componentIds = issue.getComponents()*.id

// create the sub-task with HAPI
def subtask = parentIssue.createSubTask(issueTypeName) {
    setReporter(loggedInUser)
    setAssignee(parentAssignee)
    setSummary("Sub-task for: ${parentSummary}")
    setDescription(parentDescription)
    setPriority(priorityName)
    setCustomFieldValue("Task choice", parentTaskChoiceValue)
    setComponents { set(componentIds) }
}
        
// copy parent attachments to sub-task
AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager()
Collection<Attachment> parentIssueAttachments = parentIssue.getAttachments()
    parentIssueAttachments.each{
        attachmentManager.copyAttachment(it, loggedInUser, subtask.key)
}
