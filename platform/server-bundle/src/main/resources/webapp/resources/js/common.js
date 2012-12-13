/* Common functions */

var jFormErrorHandler = function(response) {
    unblockUI();
    jAlert(response.status + ": " + response.statusText);
}

var angularHandler = function(title, defaultMsg) {
    return function(response) {
        unblockUI();

        var msg = "error";

        if (response && response.startsWith('key:') && !response.endsWith('key')) {
            msg = response.split(':')[1];
        } else if (defaultMsg) {
            msg = defaultMsg;
        }

        motechAlert(msg, title);
    }
}

var alertHandler = function(msg, title) {
    return function() {
        unblockUI();
        motechAlert(msg, title);
    }
}

var alertHandlerWithCallback = function(msg, callback) {
    return function() {
        unblockUI();
        motechAlert(msg, 'success', callback);
    }
}

var dummyHandler = function() {}

function motechAlert(msg) {
    jAlert(jQuery.i18n.prop(msg), "");
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

function blockUI() {
    $.blockUI({message : '<h3><img src="../img/load.gif" alt="loading" /></h3>'});
}

function unblockUI() {
    $.unblockUI();
}

