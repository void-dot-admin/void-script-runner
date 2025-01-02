import static com.atlassian.jira.issue.IssueFieldConstants.RESOLUTION

/* 
    *This Initialiser behaviour will restrict the resolutions
    *based on the Issue Types
    *please place this script as an Initialiser in your behaviour
*/

def resolution = getFieldById(RESOLUTION)
// get the Issue Type name from the issue where you trigger the transition
def issueType = issueContext.getIssueType().name

// only run if the transition is "Done"
if (getActionName() == "Done") {
    // based on the Issue Type name, restrict resolution values
    if (issueType == "Task"){
        resolution.setFieldOptions(["Done", "Canceled"])
    }else if (issueType == "Bug"){
        resolution.setFieldOptions(["Done", "Cannot Reproduce","Invalid"])
    }
    
}

