import com.onresolve.jira.groovy.user.FormField

/*
    * This behaviour will allow users to only add
    * numbers from 1 to 24 in a textField ( single or multi-line )
    * add this as a server-side script on the Text Field
    * this is NOT working as an Initialiser
*/

FormField textField = getFieldById(getFieldChanged())
String textFieldValue = textField.getFormValue() as String
def regex = "[1-9]|1[0-9]|2[0-4]"

/* regex explained:
    * [1-9] - number from 1 to 9
    * 1[0-91 - number from 10 to 19
    * 2[0-9] - number from 20 to 24
    * is required to add "|" - equivalent to an OR
*/

if (textFieldValue) {
    if (!textFieldValue.matches(regex)) {
        textField.setError("You can only add numbers between 1-24 to this field!")
    }else {
        textField.clearError()
    }
}else {
    textField.clearError()
}
