import com.atlassian.jira.permission.JiraPermissionHolderType
import com.atlassian.jira.permission.ProjectPermissions
import org.apache.log4j.Logger
import org.apache.log4j.Level

/*
    * this script is adding the group jira-administrators to all Permissions scheme from all projects as "Browse project" permission
    * you don't need to change anything in the script
    * just copy-paste it in the Script Console and click -> Run
    * ignore any static type checking errors, the script is working, depending on your scriprunner version there could be compiler issues
*/

// set the logger to be easier to read
def logger = Logger.getLogger("addJiraAdminToPermissionSchemes")
logger.setLevel(Level.INFO)

// for each project find all permission schemes & add jira-system-admin group for browse projects
def allProjects = Projects.getAllProjects().key
allProjects.each { String key ->
    def projectsByKey = Projects.getByKey(key)
    def permissionScheme = projectsByKey.getPermissionScheme()

    // for each permissions scheme add the jira group
    permissionScheme.each { scheme ->
    def permissionSchemeName = scheme.getName()
        scheme.addPermission(ProjectPermissions.BROWSE_PROJECTS, JiraPermissionHolderType.GROUP, 'jira-administrators')
        logger.info("Added jira admin group to Browse project for: $permissionSchemeName permissions scheme")
    }
}

logger.info("Done")
return
