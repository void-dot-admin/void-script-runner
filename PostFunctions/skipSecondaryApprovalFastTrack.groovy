import com.scriptrunner.utils.InsightUtils

/*
    *This post-function script will get the Application Object from the issue
    *using the InsightUtils class we search the object in void-DB schema
          **the InsightUtils class is from Peter, the creator of IsightUtils - check it out here: https://bitbucket.org/peter_dave_sheehan/groovy/src/master/jiraserver/insightUtils/scriptrunnerStandalone/InsightUtils.groovy
    *add the Utils class from the above link into Script Editor
    *we get the attribute value from Application and fetch the object status
    *based on the object status we skip or not the secondary approval
    *add this script as a fast-track scriptrunner post-function
    *make sure you add the skip transition Action and check all Skip checkboxes
*/

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
    def status = InsightUtils.getObjectAttributeValues(applicationObject, 'Status').toString().replace("[","").replace("]","") //the status value will be returned as a List, use replace to get rid of []

    // if the status of the License is Free -> Skip the owner approval
    if ( status == "Free" ) {
        return true // this will trigger the "Action" - transition you added ouside of the console 
    }else {
        return false // this will not do anything
    }

}
