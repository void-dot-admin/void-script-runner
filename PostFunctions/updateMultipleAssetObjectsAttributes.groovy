import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.component.ComponentAccessor
import com.scriptrunner.utils.InsightUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level

/*
    *This post-function script will get the Application & User Objects from the issue
    *using the InsightUtils class we search the object in void-DB schema
          **the InsightUtils class is from Peter, the creator of IsightUtils - check it out here: https://bitbucket.org/peter_dave_sheehan/groovy/src/master/jiraserver/insightUtils/scriptrunnerStandalone/InsightUtils.groovy
    *add the Utils class from the above link into Script Editor
    *we search in each Asset object to find specific attribute to link all of the together
    *add this script as a custom scriptrunner post-function
    
*/

// set the logger to be easier to read
def logger = Logger.getLogger("applicationAccessRequest")
logger.setLevel(Level.INFO)

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
def customerRequestType = Issues.getByKey(issue.key.toString())?.getRequestType()?.getName() //used HAPI to get the request type

if ( customerRequestType == "Application access request" ) {
    // get the Application object from the issue and search in void-DB object schema to find attribute value
    def cfApplication = customFieldManager.getCustomFieldObjectsByName("Application")[0]
    def applicationValue = issue.getCustomFieldValue(cfApplication)
    def cfEmployee = customFieldManager.getCustomFieldObjectsByName("User Object")[0]
    def employeeValue = issue.getCustomFieldValue(cfEmployee)

    // cf asset value will always contain the name + objectKey, therefore we need to extract the Key
    // we use the empty string to be able to fetch only the key
    def applicationKey = ""
    def applicationKeyJoin = applicationValue.each { applicationKey += it.getObjectKey() } // "+=" operator performs enhanced assignments eg: "a += 2" same as "a = a + 2"
    def applicationObject = InsightUtils.getObjectByKey(applicationKey)
    def status = InsightUtils.getObjectAttributeValues(applicationObject, 'Status').toString().replace("[","").replace("]","") //the status value will be returned as a List, use replace to get rid of []
    def applicationComputersOldValue = InsightUtils.getObjectAttributeValues(applicationObject, 'Computer') //get existing value from Computer attribute, this is important as we want to keep these values when we add the new Computer
    
    // get the Employee object
    def employeeKey = ""
    def employeeKeyJoin = employeeValue.each { employeeKey += it.getObjectKey() }
    def employeeObject = InsightUtils.getObjectByKey(employeeKey)
    def employeeComputer = InsightUtils.getObjectAttributeValues(employeeObject, 'Computer')

    // get the Computer Object
    def computerKey = ""
    def computerKeyJoin = employeeComputer.each { computerKey += it.getObjectKey() }
    def employeeComputerObject = InsightUtils.getObjectByKey(computerKey)

    if ( applicationComputersOldValue ) { // this will fire when the Application Object already has a list of computers
        // add employeeComputerObject element to the List of Computers from application Object
        def applicationComputersNewValue = applicationComputersOldValue << employeeComputerObject
        
        // update the Application object and add the employee Computer to the already existing list of Computers
        def updateApplication = InsightUtils.setObjectAttribute(applicationObject, "Computer", applicationComputersNewValue)
        logger.info("$applicationObject has been updated: $employeeComputerObject has been added to the existing list of computers")
    }else { // this will fire when the Application Object doesn't have any computers attached
        // update the Application Object with the employee Computer
        def updateApplication = InsightUtils.setObjectAttribute(applicationObject, "Computer", employeeComputerObject)
        logger.info("$applicationObject has been updated with the following Computer: $employeeComputerObject")
    }
    
    if ( status == "Licensed" ) { // check if application has a license
        // get the License Object 
        def licenseValue = InsightUtils.getObjectAttributeValues(applicationObject, 'License') // this will return only the value of the attribute
        def licenseKey = ""
        def licenseKeyJoin = licenseValue.each { licenseKey += it.getObjectKey() }
        def licenseObject = InsightUtils.getObjectByKey(licenseKey)
        
        // update the Employee Object with the License attribute 
        def updateEmployee = InsightUtils.setObjectAttribute(employeeObject, "Software Licenses", licenseObject)
        logger.info("The following license $licenseObject has been added to $employeeObject")
    }

}
