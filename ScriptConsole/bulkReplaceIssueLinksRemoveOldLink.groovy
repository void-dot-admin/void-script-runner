import com.atlassian.jira.component.ComponentAccessor 
import com.atlassian.jira.web.bean.PagerFilter 
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.query.Query 
import com.atlassian.jira.issue.Issue 
import org.apache.log4j.Logger 
import org.apache.log4j.Level

/*
    * This script will remove the Issue link (specific one) from each issue found on the JQL
    * just copy-paste this is your console
    * replace any changeable variables like the JQL, linkTypeId
    * run the script
    * I've created this script in the situation where you need to remove in bulk a specific issue link from a set of issues
*/

// set the logger to be easier to read
def logger = Logger.getLogger("bulkReplaceIssueLinkType.removeOldIssueLink")
logger.setLevel(Level.INFO)
def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser

// search the issues
def queryString = "issueLinkType = \"clones\"" // add your JQL here
SearchService searchService = ComponentAccessor.getComponent (SearchService.class)
def parseResult = searchService.parseQuery(user, queryString.toString())
Query query = parseResult.getQuery()

// this search will bypass any hidden issues where you don't have access
def issueSearchResults = searchService.searchOverrideSecurity(user, query, PagerFilter.getUnlimitedFilter())
def regularIssues = issueSearchResults?.results

/*  *this section is used to check how many issues the JQL will give you. Uncomment this block if you need
    // def countRegularIssues = regularIssues.count { it.key }
    // logger.info("Number of regular issues: $countRegularIssues")
*/

// remove Cloners-old from the regular issues 
regularIssues.each { Issue issue ->

    def issuekey = issue.getKey()

    // Specify the Link Type ID of the current Link ( OLD )
    final def linkTypeId = 10001 // id of Cloners issue link
    def issueLinkManager = ComponentAccessor.issueLinkManager
    issueLinkManager.getOutwardLinks(issue.id).each {
        if (it.linkTypeId == linkTypeId) {
            // remove the OLD link from the issues
            issueLinkManager.removeIssueLink(it, user)
            logger.info("issueLinkType \"Cloners\" has been removed from $issue.key") 
        }
    }
}

logger.info("Done")
return
