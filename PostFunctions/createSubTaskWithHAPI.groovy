import com.atlassian.jira.component.ComponentAccessor

def parentIssue = issue

if (parentIssue.isSubTask()) {
    return
}

def subTask = parentIssue.createSubTask('Sub-task') {
    setSummary("Sub-task for: ${parentIssue.summary}")
    setDescription(parentIssue.description)
    setPriority(parentIssue.priority)

    def taskChoice = parentIssue.getCustomFieldValue('Task choice')

    if (taskChoice) {
        setCustomFieldValue('Task choice', taskChoice)
    }

    setAssignee {
        if (parentIssue.assignee) {
            set(parentIssue.assignee)
        } else {
            automatic()
        }
    }

    setComponents {
        parentIssue.components.each { add(it) }
    }

}

def attachmentManager = ComponentAccessor.getAttachmentManager()
attachmentManager.copyAttachments(parentIssue, Users.loggedInUser, subTask.key)
