import com.atlassian.jira.component.ComponentAccessor
import groovy.time.TimeCategory
import java.util.Date.*

/*
  * Make sure to select "Text Field (multi-line)"
  * as Template in the scripted field configuration
  * because we return a String and NOT a DateValue
*/

def customFieldManager = ComponentAccessor.getCustomFieldManager()

// get the Date fields + values
def startDeploymentCF = customFieldManager.getCustomFieldObjectsByName("Deployment Start").getAt(0)
def endDeploymentCF = customFieldManager.getCustomFieldObjectsByName("Deployment End").getAt(0)
def startDeploymentValue = issue?.getCustomFieldValue(startDeploymentCF)
def endDeploymentValue = issue?.getCustomFieldValue(endDeploymentCF)

if ( !startDeploymentValue || !endDeploymentValue ) {
    return // stop the script in case any of the values are null
}

// make the substraction using TimeCategory class & convert the result to hours
def timeInDeployment = TimeCategory.minus(endDeploymentValue, startDeploymentValue)
def timeInDeploymentHours = timeInDeployment.getDays() * 1440 + timeInDeployment.getHours() + "h"

return timeInDeploymentHours
