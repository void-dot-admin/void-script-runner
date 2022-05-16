import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.query.Query
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import org.apache.log4j.Level
import org.apache.log4j.Logger
import com.atlassian.jira.event.type.EventDispatchOption

/**
 * Script for migrating value of custom field user picker (single-user) ->
 * -> to custom field user picker (multi-users) on specific Issue Type
 * For the regular issues (excluding archived issues)
 * Trigger the migration from the Script Console: paste the script -> Press Run
 * Note:
 - Also works if the issue is in a non-editable status (set by workflow properties)
 - Will not fire an issue update event
 - Will not write an entry in the issue history
 - Will not reindex the issue, need to trigger reindexing manually after the script has run
 - Modify the script with your CF names
 */

log = Logger.getLogger("scriptConsole.migrateCustomFieldValueUserPicker")
log.setLevel(Level.INFO)

final String ISSUETYPE_NAME = "Epic"
final String CF_NAME_SINGLE_USER = "User Manager"
final String CF_NAME_MULTI_USER = "All Managers"

def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def customFieldSingle = ComponentAccessor.customFieldManager.getCustomFieldObjectsByName(CF_NAME_SINGLE_USER).getAt(0)
def customFieldMulti = ComponentAccessor.customFieldManager.getCustomFieldObjectsByName(CF_NAME_MULTI_USER).getAt(0)

if ( !customFieldSingle ) {
    log.error "No custom field found with name '${CF_NAME_SINGLE_USER}'"
    return
}

if ( !customFieldMulti ) {
    log.error "No custom field found with name '${CF_NAME_MULTI_USER}'"
    return
}

// Search in the regular issues using JQL
// added \" + \" because User is a reserved term in JQL and the field "User Manager" needs the quotes
def queryString = "issuetype = ${ISSUETYPE_NAME} AND \"${CF_NAME_SINGLE_USER}\" is not EMPTY"
SearchService searchService = ComponentAccessor.getComponent(SearchService.class)
def parseResult = searchService.parseQuery(user, queryString.toString())
Query query = parseResult.getQuery()

SearchResults issueSearchResults = searchService.searchOverrideSecurity(user, query, PagerFilter.getUnlimitedFilter())
def regularIssues = issueSearchResults?.results

if (!regularIssues) {
    log.warn ("No '${ISSUETYPE_NAME}' issues found with '${CF_NAME_SINGLE_USER}' to migrate.")
    return
}

// Run the migration
log.info "BEGIN migration of '${CF_NAME_SINGLE_USER}' value to '${CF_NAME_MULTI_USER}' " +
        "for ${regularIssues.size()} ${ISSUETYPE_NAME} issue: ${regularIssues*.key}"

regularIssues?.each{
    MutableIssue issue = ComponentAccessor.issueManager.getIssueObject(it.id)
    def cfMultiUserValue = issue.getCustomFieldValue(customFieldMulti)
    def cfSingleUserValue = issue.getCustomFieldValue(customFieldSingle)
    def changeHolder = new DefaultIssueChangeHolder()

    customFieldMulti.updateValue(null, issue, new ModifiedValue([cfMultiUserValue], [cfSingleUserValue]), changeHolder)
    ComponentAccessor.getIssueManager().updateIssue(user, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
    log.info "Migrated value of '${CF_NAME_SINGLE_USER}' to '${CF_NAME_MULTI_USER}' for ${ISSUETYPE_NAME} ${issue.key}"
}
log.info "END migration"
return
