/**
 * Behaviour script that prefills description
 * to a default value based on Issuetype
 * that works only on create screen
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
final String REQ_DESCRIPTION = """* Please specify the environment:"""

def descriptionField = getFieldById("description")
def issueContext = issueContext.issueType.name

//should work only on create screen
if (!( getActionName() in ["Create Issue", "Create"] )) {
    return
}

switch(issueContext) {
    case "Bug" : descriptionField.setFormValue(BUG_DESCRIPTION)
        break
    case "Epic" : descriptionField.setFormValue(EPIC_DESCRIPTION)
        break
    case "Requirement" : descriptionField.setFormValue(REQ_DESCRIPTION)
        break
}
