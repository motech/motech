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
    $.blockUI({message : '<h3><img src="../server/resources/img/load.gif" alt="loading" /></h3>'});
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
            motechAlert(msg, 'server.success', callback);
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

function defaultLayout() {
    'use strict';
    return function() {
        return {
            restrict: 'EA',
            link: function(scope, elm, attrs) {
                var eastSelector;
                /*
                * Define options for inner layout
                */
                scope.innerLayoutOptions = {
                    name: 'innerLayout',
                    resizable: true,
                    slidable: true,
                    closable: true,
                    east__paneSelector: "#inner-east",
                    center__paneSelector: "#inner-center",
                    east__spacing_open: 6,
                    spacing_closed: 35,
                    east__size: 300,
                    showErrorMessages: true, // some panes do not have an inner layout
                    resizeWhileDragging: true,
                    center__minHeight: 100,
                    contentSelector: ".ui-layout-content",
                    togglerContent_open: '',
                    togglerContent_closed: '<div><i class="icon-caret-left button"></i></div>',
                    autoReopen: false, // auto-open panes that were previously auto-closed due to 'no room'
                    noRoom: true,
                    togglerAlign_closed: "top", // align to top of resizer
                    togglerAlign_open: "top",
                    togglerLength_open: 0,
                    togglerLength_closed: 35,
                    togglerTip_open: "Close This Pane",
                    togglerTip_closed: "Open This Pane",
                    east__initClosed: true,
                    initHidden: true
                    //isHidden: true
                };

                // create the page-layout, which will ALSO create the tabs-wrapper child-layout
                scope.innerLayout = elm.layout(scope.innerLayoutOptions);
            }
        };
    };
}
