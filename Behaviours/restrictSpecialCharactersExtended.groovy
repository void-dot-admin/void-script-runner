/**
 * Behaviour script to restrict Special Characters
 * Extended Version
 * in a custom field of type: text signle-line | you can use it in a multi-line CF also
 * Note:
 - after you create the Behaviour
 - add field: add the field that where you want to restrict Special Chars
 - add this script as a "server-side script"
 - Important: don't add the script as an initialiser!
 */

def customField = getFieldByName("Youtube")
def specialChar = /[^a-zA-Z0-9\s]/ //what is inside [^] is allowed; \s = space + new line
def noNumbers = /[^a-zA-Z\s]/
def normalChar = /[^A-Z\s]/

if ( customField?.value =~ specialChar ) {
    customField.setError("Special characters are not allowed.")
}
else if ( customField?.value =~ noNumbers ) {
    customField.setError("Numbers are not allowed.")
}
else if ( customField?.value =~ normalChar ) {
    customField.setError("Only capital letters are allowed.")
}
else {
    customField.clearError()
}
