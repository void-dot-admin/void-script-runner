import com.atlassian.jira.component.ComponentAccessor 
import com.atlassian.jira.web.bean.PagerFilter 
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.query.Query 
import com.atlassian.jira.issue.Issue 
import org.apache.log4j.Logger 
import org.apache.log4j.Level

/*
    * This script will add a new issue linkage between 2 issues that already have a link ( found with the JQL from line 25 )
    * just copy-paste this is your console
    * replace any changeable variables like the JQL, linkTypeId, the newLink Outward
    * run the script
    * i've created this script in the situation where one link should be replace with a new one
    * there is a part2 of this that will remove the OLD link from ALL issues - check the other files in the repo
*/

// set the logger to be easier to read
def logger = Logger.getLogger("bulkReplaceIssueLinkType.addNewIssueLink")
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

// add the Link 
regularIssues.each { Issue issue -> 

    def issuekey = issue.getKey()

    // Specify the Link Type ID of the current Link ( OLD )
    final def linkTypeId = 10001 // id of Cloners issue link
    def issueLinkManager = ComponentAccessor.issueLinkManager

    // for each Link get the source & destination 
    issueLinkManager.getOutwardLinks(issue.id).each {
        if (it.linkTypeId == linkTypeId) {

            def destinationissuekey = it.getDestinationObject().getKey() as String
            def sourceIssue = Issues.getByKey(issuekey.toString())
            def destinationIssue = Issues.getByKey(destinationissuekey)

            // link the source & detination issues
            sourceIssue.link("duplicates", destinationIssue) //
            logger.info("\"Duplicate\" issue linking of type duplicates-Outward has been added to $sourceIssue")
        }
    }
}
