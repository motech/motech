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

/* Define "finished typing" as 5 second puase */
var typingTimer;
var doneTypingInterval = 5 * 1000;

function captureTyping(callback) {
    clearTimeout(typingTimer);
    typingTimer = setTimeout(callback, doneTypingInterval);
}

