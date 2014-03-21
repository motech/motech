    /* Common functions */

function motechAlert(msg, title, params, callback) {
    'use strict';
    jAlert(jQuery.i18n.prop.apply(null, [msg].concat(params)), jQuery.i18n.prop(title), callback);
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

    handleResponse = function(title, defaultMsg, response, callback) {
        'use strict';
        var msg = "server.error",
            params = [], i,
            responseData = (typeof(response) === 'string') ? response : response.data;

        unblockUI();

        if ((typeof(responseData) === 'string') && responseData.startsWith('key:') && !responseData.endsWith('key')) {
             if (responseData.indexOf('params:') !== -1) {
                msg = responseData.split('\n')[0].split(':')[1];
                params = responseData.split('\n')[1].split(':')[1].split(',');
             } else {
                msg = responseData.split(':')[1];
             }
        } else if (defaultMsg) {
            msg = defaultMsg;
        }

        if (callback) {
            callback(title, msg, params);
        } else {
            motechAlert(msg, title, params);
        }
    },

    angularHandler = function(title, defaultMsg, callback) {
        'use strict';
        return function(response) {
            handleResponse(title, defaultMsg, response, callback);
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

function innerLayout(conf, eastConfig) {
    'use strict';

    var config = conf || {},
        defaults = {
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
        },
        element = angular.element('#outer-center'),
        button = angular.element(eastConfig && eastConfig.button),
        options = {},
        layout;

    element.livequery(function () {
        $.extend(options, defaults, config);

        layout = element.layout(options);
        layout.destroy();
        layout = element.layout(options);

        if (eastConfig && eastConfig.show) {
            button.livequery(function () {
                layout.addCloseBtn("#tbarCloseEast", "east");
                layout.addToggleBtn(eastConfig.button, "east");
                button.expire();
            });

            layout.show('east');
        } else {
            layout.hide('east');
        }

        element.expire();
    });
}

function defaultView(view) {
    'use strict';
    if (!view) {
        view = window.location.hash.substring(2, window.location.hash.length);
    }

    var location = window.location.href,
        indexOfHash = location.indexOf('#'),
        to = (indexOfHash < 0) ? location.length : indexOfHash,
        newLocation = location.substr(0, to) + '#' + view;

    if (newLocation === location) {
        window.location += '#';
    }

    window.location = newLocation;
}
