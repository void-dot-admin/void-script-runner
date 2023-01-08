/**
    *Behaviour script that hides
    *the "None" option from a single select CF
    *place this as "server-side script"
    *on the Specific field that you want to hide the option
*/

final String CF_NAME_GITHUB = "GitHub script"
final String CF_ID_GITHUB = "customfield_10232" //id of your CF
def githubScript = getFieldByName(CF_NAME_GITHUB)

String hideNone = """
<script type="text/javascript">
var singleSelect = document.getElementById('${CF_ID_GITHUB}')
singleSelect.remove(0)
</script>
"""
githubScript.setDescription(hideNone)