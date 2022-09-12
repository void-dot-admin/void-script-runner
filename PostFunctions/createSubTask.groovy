import com.atlassian.jira.issue.AttachmentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.config.manager.PrioritySchemeManager
import com.atlassian.jira.issue.context.IssueContextImpl
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.attachment.Attachment

/**
 *Post-function script that creates a Sub-task issue type with name "Sub-task"
 *copy values & attachments from parent issue type
 */

Issue createSubTaskIssue(String summary, String description) {
    Long projectId = issue.getProjectId()
    def parentIssueKey = issue.key

    // the issue type name for the new issue - *manadatory of type sub-task
    final issueTypeName = "Sub-task"

    // the priority of the new issue (copy from parent)
    final priorityName = issue.getPriority().name

    def issueService = ComponentAccessor.issueService
    def constantsManager = ComponentAccessor.constantsManager
    def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    def prioritySchemeManager = ComponentAccessor.getComponent(PrioritySchemeManager)
    def assignee = issue.getAssignee() // get assignee from parent

    def parentIssue = ComponentAccessor.issueManager.getIssueByCurrentKey(parentIssueKey)
    if (!parentIssue) {
        log.error("Could not find parent issue with key $parentIssueKey")
        return
    }

    def subtaskIssueTypes = constantsManager.allIssueTypeObjects.findAll{ it.subTask }
    def subTaskIssueType = subtaskIssueTypes.findByName(issueTypeName)
    if (!subTaskIssueType) {
        log.error("Could not find subtask issue type with name $issueTypeName. Avaliable subtask issue types are ${subtaskIssueTypes*.name.join(", ")}")
        return
    }

    // get custom field value from parent (single select type)
    def taskChoice = ComponentAccessor.customFieldManager.getCustomFieldObjectsByName("Task choice")?.getAt(0)
    def parentTaskChoice = issue.getCustomFieldValue(taskChoice)?.toString()
    def issueContext = new IssueContextImpl (projectId, subTaskIssueType.id)
    def fieldConfig = taskChoice.getRelevantConfig(issueContext)
    def taskChoiceOption = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find{
        it.toString() == "$parentTaskChoice"
    }?.optionId?.toString()

    // if the priority doesn't exists or if is null, then set the default priority
    def priorityId = constantsManager.priorities.findByName(priorityName)?.id ?: prioritySchemeManager.getDefaultOption(parentIssue)

    // get components as Long from parent
    Long[] componentIds = issue.getComponents()*.id

    // set sub-task values from parent
    def issueInputParameters = issueService.newIssueInputParameters().with {
        setProjectId(projectId)
        setIssueTypeId(subTaskIssueType.id)
        setReporterId(loggedInUser?.username)
        setAssigneeId(assignee?.username)
        setSummary(summary)
        setDescription(description)
        setPriorityId(priorityId)
        addCustomFieldValue(taskChoice.id, taskChoiceOption)
        setComponentIds(componentIds)
    }

    def validationResult = issueService.validateSubTaskCreate(loggedInUser, parentIssue.id, issueInputParameters)
    assert validationResult.valid : validationResult.errorCollection
    def issueResult = issueService.create(loggedInUser, validationResult)
    assert issueResult.valid : issueResult.errorCollection
    def subtask = issueResult.issue

    // copy parent attachments to sub-task
    AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager()
    Collection<Attachment> parentIssueAttachments = parentIssue.getAttachments()
    parentIssueAttachments.each{
        attachmentManager.copyAttachment(it, loggedInUser, subtask.key)
    }
    ComponentAccessor.subTaskManager.createSubTaskIssueLink(parentIssue,subtask,loggedInUser)
}

// get parent summary & description
Issue parent = issue
def parentSummary = parent.getSummary()
def parentDescription = parent.getDescription()

// create the Sub-task
createSubTaskIssue("${parentSummary}","${parentDescription}")

