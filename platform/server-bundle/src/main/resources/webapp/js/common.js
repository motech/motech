    /* Common functions */

function motechAlert(msg, title, callback) {
    'use strict';
    jAlert(jQuery.i18n.prop(msg), jQuery.i18n.prop(title), callback);
}

function motechConfirm(msg, title, callback) {
    'use strict';
    jConfirm(jQuery.i18n.prop(msg), jQuery.i18n.prop(title), callback);
}

function motechAlertStackTrace(msg, title, response, callback) {
    'use strict';
    jAlertStackTrace(jQuery.i18n.prop(msg).bold()+": \n"+response, jQuery.i18n.prop(title), callback);
}

function blockUI() {
    'use strict';
    // TODO: Please use correct load.gif file.
    $.blockUI({message : '<h3><img src="../../../../../../../../modules/ivr/motech-calllog/src/main/resources/webapp/img/load.gif" alt="loading" /></h3>'});
}

function unblockUI() {
    'use strict';
    $.unblockUI();
}

var jFormErrorHandler = function(response) {
        'use strict';
        unblockUI();
        jAlert(response.status + ": " + response.statusText);
    },

    handleResponse = function(title, defaultMsg, response) {
        'use strict';
        var msg = "server.error",
            responseData = (typeof(response) === 'string') ? response : response.data;

        unblockUI();

        if ((typeof(responseData) === 'string') && responseData.startsWith('key:') && !responseData.endsWith('key')) {
            msg = responseData.split(':')[1];
        } else if (defaultMsg) {
            msg = defaultMsg;
        }
        motechAlert(msg, title);
    },

    angularHandler = function(title, defaultMsg) {
        'use strict';
        return function(response) {
            handleResponse(title, defaultMsg, response);
        };
    },

    handleWithStackTrace = function(title, defaultMsg, response) {
        'use strict';
        var msg = "server.error";
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
    },

    alertHandler = function(msg, title) {
        'use strict';
        return function() {
            unblockUI();
            motechAlert(msg, title);
        };
    },

    alertHandlerWithCallback = function(msg, callback) {
        'use strict';
        return function() {
            unblockUI();
            motechAlert(msg, 'success', callback);
        };
    },

    dummyHandler = function() {'use strict';},

/* Define "finished typing" as 5 second puase */
    typingTimer,
    doneTypingInterval = 5 * 1000;


function captureTyping(callback) {
    'use strict';
    clearTimeout(typingTimer);
    typingTimer = setTimeout(callback, doneTypingInterval);
}

