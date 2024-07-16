import com.atlassian.jira.scheme.SchemeEntity
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.security.IssueSecuritySchemeManager
import org.apache.log4j.Logger
import org.apache.log4j.Level

/*
    * this script is adding the group jira-administrators to all Issue Security Schemes on all levels
    * you don't need to change anything in the script
    * just copy-paste it in the Script Console and click -> Run
*/

// set the logger to be easier to read
def logger = Logger.getLogger("addJiraAdminToIssueSecurity")
logger.setLevel(Level.INFO)

def issueSecurityLevelManager = ComponentAccessor.getIssueSecurityLevelManager()
def issueSecuritySchemeManager = ComponentAccessor.getComponent(IssueSecuritySchemeManager)

// get all the Issue Security Schemes and for each scheme & level add jira-admin group
def schemes = issueSecuritySchemeManager.getSchemeObjects()
schemes.each { it ->
    // get the scheme levels
    def levels = ComponentAccessor.getIssueSecurityLevelManager().getIssueSecurityLevels(it.getId())
    levels.each { lvl ->
        def entity = new SchemeEntity("group", "jira-administrators", lvl.id)
        def genericValue = issueSecuritySchemeManager.getScheme(it.id)
        def addPermissions = issueSecuritySchemeManager.createSchemeEntity(genericValue, entity)
        logger.info ("jira-admin group has been added to \"$addPermissions\" security level")
    }
}

logger.info("Done")
return
