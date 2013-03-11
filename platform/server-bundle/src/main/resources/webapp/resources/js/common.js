/* Common functions */

var jFormErrorHandler = function(response) {
    unblockUI();
    jAlert(response.status + ": " + response.statusText);
}

var angularHandler = function(title, defaultMsg) {
    return function(response) {
        handleResponse(title, defaultMsg, response);
    }
}

var handleResponse = function(title, defaultMsg, response) {
        var msg = "error";
        var responseData = (typeof(response) == 'string') ? response : response.data;

        unblockUI();

        if (responseData && responseData.startsWith('key:') && !responseData.endsWith('key')) {
            msg = responseData.split(':')[1];
        } else if (defaultMsg) {
            msg = defaultMsg;
        }
        motechAlert(msg, title);
}

var handleWithStackTrace = function(title, defaultMsg, response) {
    var msg = "error";
    if (response) {
        if(response.responseText) {
            response = response.responseText;
        } else if(response.data) {
            response = response.data;
        }
    }
    if (defaultMsg) {
        msg = defaultMsg;
    }
    motechAlertStackTrace(msg, title, response);
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

function motechConfirm(msg, title, callback) {
    jConfirm(jQuery.i18n.prop(msg), jQuery.i18n.prop(title), callback);
}

function motechAlertStackTrace(msg, title, response, callback) {
    jAlertStackTrace(jQuery.i18n.prop(msg).bold()+": \n"+response, jQuery.i18n.prop(title), callback);
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
