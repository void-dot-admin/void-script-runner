import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.component.ComponentAccessor
import com.scriptrunner.utils.InsightUtils
import com.atlassian.jira.user.ApplicationUser
import org.apache.log4j.Logger
import org.apache.log4j.Level

/*
    *This post-function script will get the Application Object from the issue
    *using the InsightUtils class we search the object in void-DB schema
          **the InsightUtils class is from Peter, the creator of IsightUtils - check it out here: https://bitbucket.org/peter_dave_sheehan/groovy/src/master/jiraserver/insightUtils/scriptrunnerStandalone/InsightUtils.groovy
    *add the Utils class from the above link into Script Editor
    *we get the value from Owner and add the owner to the issue to be used as secondary approval
    *add this script as a custom scriptrunner post-function
    
*/

// set the logger to be easier to read
def logger = Logger.getLogger("addOwnerApplicationAccessRequest")
logger.setLevel(Level.INFO)

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
def customerRequestType = Issues.getByKey(issue.key.toString())?.getRequestType()?.getName() //used HAPI to get the request type

if ( customerRequestType == "Application access request" ) {
    // get the Application object from the issue and search in void-DB object schema to find attribute value
    def cfApplication = customFieldManager.getCustomFieldObjectsByName("Application")[0]
    def applicationValue = issue.getCustomFieldValue(cfApplication)

    // cf asset value will always contain the name + objectKey, therefore we need to extract the Key
    // we use the empty string to be able to fetch only the key
    def applicationKey = ""
    def applicationKeyJoin = applicationValue.each { applicationKey += it.getObjectKey() } // "+=" operator performs enhanced assignments eg: "a += 2" same as "a = a + 2"
    def applicationObject = InsightUtils.getObjectByKey(applicationKey)
    def ownerValue = InsightUtils.getObjectAttributeValues(applicationObject, 'Owner')

    if ( ownerValue ) {//check if Owner has a value
        def ownerKey = ""
        def stringJoinOwner = ownerValue.each { ownerKey += it.getObjectKey() }
        def ownerObject = InsightUtils.getObjectByKey(ownerKey)
        def owner = InsightUtils.getObjectAttributeValues(ownerObject, 'Name').toString().replace("[","").replace("]","") //the name value will be returned as a List, use replace to get rid of []

        // make use of the below commented line if you need to convert the Owner username into an application user object
        // ApplicationUser ownerJiraUser = ComponentAccessor.getUserManager().getUserByName(owner) //convert the owner into Application User

        // update the owner in the ticket
        issue.update {
            setCustomFieldValue("Owner", owner)
            logger.info("Success: Owner $owner has been added to $issue.key")
        }
    }

}
