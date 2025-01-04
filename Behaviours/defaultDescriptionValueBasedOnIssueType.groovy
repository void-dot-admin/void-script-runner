/**
 * Behaviour script that prefills description
 * to a default value based on Issuetype
 * that works only on create screen
 * add this script as a server-side-script behaviour on "Issue Type" field
 */

final String BUG_DESCRIPTION = """* Response/Error problem: 

{noformat}
* Steps to reproduce: 
* Expected results: 
* Actual results: 
"""
final String EPIC_DESCRIPTION = """* For what environment: 
* UI testing (y/n): 
"""
final String TASK_DESCRIPTION = """* Please specify the environment:"""

def descriptionField = getFieldById("description")
def issueContext = issueContext.issueType.name

//will work only on create screen
if (!( getActionName() in ["Create Issue", "Create"] )) {
    return
}

switch(issueContext) {
    case "Bug" : descriptionField.setFormValue(BUG_DESCRIPTION)
        break
    case "Epic" : descriptionField.setFormValue(EPIC_DESCRIPTION)
        break
    case "Task" : descriptionField.setFormValue(TASK_DESCRIPTION)
        break
}
