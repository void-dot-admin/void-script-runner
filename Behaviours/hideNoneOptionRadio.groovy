/**
 *Behaviour script that hides
 *the "None" option from a radio button CF
 *place this as "server-side script"
 *on the Specific field that you want to hide the option
 */

final String CF_NAME_YOUTUBE = "YouTube guide"
final String CF_ID_YOUTUBE = "customfield_10231" //id of your CF
def youtubeGuide = getFieldByName(CF_NAME_YOUTUBE)

String hideNone = """
<style type=""text/css>
#${CF_ID_YOUTUBE} > div:nth-child(2) {
    display: none !important
}
</style>
"""
youtubeGuide.setDescription(hideNone)