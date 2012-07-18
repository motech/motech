/* Common functions */

var jFormErrorHandler = function(response) {
    jAlert(response.status + ": " + response.statusText);
}

var angularErrorHandler = function(response) {
    // TODO : better error handling
    motechAlert("error");
}

var alertHandler = function(msg) {
    return function() {
        motechAlert(msg);
    }
}

var dummyHandler = function() {}

function motechAlert(msg) {
    jAlert(jQuery.i18n.prop(msg));
}

function motechAlert(msg, title) {
    jAlert(jQuery.i18n.prop(msg), jQuery.i18n.prop(title));
}

function motechAlert(msg, title, callback) {
    jAlert(jQuery.i18n.prop(msg), jQuery.i18n.prop(title), callback);
}