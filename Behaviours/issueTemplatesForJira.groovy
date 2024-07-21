import com.atlassian.jira.issue.fields.config.manager.FieldConfigManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import static com.atlassian.jira.issue.IssueFieldConstants.*

/*
  *This behaviour will creat an issue Template for your project
  *Just make sure to add this behaviour as a Server Side script for your Issue Picker scripted field
  *is NOT working as an Initializer
  *FULL VIDEO GUIDE: https://www.youtube.com/watch?v=f_qumaGtBrc
*/

def template = getFieldById(getFieldChanged())
def templateValue = template?.getValue()

// make sure that the issuePicker has a value
def issueTemplate
if (templateValue) {
    // get the issue from the Template: issue picker field
    def issueManager = ComponentAccessor.issueManager
    issueTemplate = issueManager.getIssueObject("$templateValue") // using Jira API
    //issueTemplate = Issues.getByKey("$templateValue") //using HAPI 
}

// multi-value example by NAME
def toolsCF = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Tools").getAt(0)
// single-select example by ID
def taskChoiceCF = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10201")
// text-field example by NAME
def justificationCF = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Justification").getAt(0)

// get fields from SCREEN examples
// example for system fields
def summary = getFieldById("summary")
def description = getFieldById("description")
def labels = getFieldById(LABELS)
// example for CF by Name
def tools = getFieldByName("Tools")
// example for CF by ID
def taskChoice = getFieldById("customfield_10201")
// example for CF by Name
def justification = getFieldByName("Justification")


//GET FIELD OPTION FROM CF
def taskChoiceFieldConfig = ComponentAccessor.getComponent(FieldConfigManager).getFieldConfig(10301) // "Task Choice" fieldContextID REPLACE 10301 WITH YOUR CONTEXT ID
def taskChoiceFieldOptions = ComponentAccessor.getOptionsManager().getOptions(taskChoiceFieldConfig)
def toolsFieldConfig = ComponentAccessor.getComponent(FieldConfigManager).getFieldConfig(10700) // "Tools" fieldContextID - multi-select
def toolsFieldOptions = ComponentAccessor.getOptionsManager().getOptions(toolsFieldConfig)

// FETCH FIELD VALUES FROM THE TEMPLATE (issue selected by the user)
// EXAMPLE FOR SYSTEM FIELDS
def summaryTemplate = issueTemplate?.getSummary()
def descriptionTemplate = issueTemplate?.getDescription()
def labelsTemplate = issueTemplate?.getLabels() as String []
// EXAMPLE FOR CF TEXT
def justificationTemplate = issueTemplate?.getCustomFieldValue(justificationCF)
// EXAMPLE FOR CF MULTIPLE
def toolsTemplate = issueTemplate?.getCustomFieldValue(toolsCF)
// EXAMPLE FOR CF SINGLE
def taskChoiceTemplate = issueTemplate?.getCustomFieldValue(taskChoiceCF)

// GET THE OPTION/s FOUND IN THE TEMPLATE FROM THE FIELD OPTIONS ONLY FOR SINGLE OR MULTI
def toolsValues = toolsFieldOptions?.findAll { it in toolsTemplate }
def taskChoiceValues = taskChoiceFieldOptions?.find { it == taskChoiceTemplate }

// ATTACH THE TEMPLATE VALUES TO THE SCREEN FIELDS
// EXAMPLE FOR SYSTEM FIELD
summary?.setFormValue(summaryTemplate)
description?.setFormValue(descriptionTemplate)
labels.setFormValue(labelsTemplate)
// EXAMPLE FOR TEXT FIELD
justification?.setFormValue(justificationTemplate)
// EXAMPLE FOR SINGLE SELECT
taskChoice?.setFormValue(taskChoiceValues?.optionId)
// EAXAMPLE FOR MULTI SELECT
tools?.setFormValue(toolsValues?.optionId)

