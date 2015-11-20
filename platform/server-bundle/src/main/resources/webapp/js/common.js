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
    $.blockUI({theme: false, message :
        '<div class="splash">' +
            '<div class="splash-logo"><img src="./../../static/img/motech-logo.gif" alt="motech-logo"></div>' +
            '<div class="clearfix"></div>' +
            '<div class="splash-loader"><img src="./../../static/img/loadingbar.gif" alt="Loading..."></div>' +
            '<div class="clearfix"></div>' + '<br>' +
        '</div>'
    });
}

function resizeLayout() {
    'use strict';
    setTimeout(function () {
        var outerCenterElement = $('#outer-center');
        if (outerCenterElement.length !== undefined && outerCenterElement.length > 0) {
            outerCenterElement.layout().resizeAll();
        }
    }, 200);
}

function unblockUI() {
    'use strict';
    $.unblockUI();
    resizeLayout();
}

function getAvailableTabs(moduleName, callback) {
    'use strict';
    return $.ajax({
        url: '../server/available/' + moduleName,
        success: callback,
        async: false
    });
}

var jFormErrorHandler = function(response) {
        'use strict';
        unblockUI();
        jAlert(response.status + ": " + response.statusText);
    },

    parseResponse = function (responseData, defaultMsg) {
        'use strict';
        var msg = { value: '', literal: false, params: [] };

        if ((typeof(responseData) === 'string') && responseData.startsWith('key:') && !responseData.endsWith('key')) {
             if (responseData.indexOf('params:') !== -1) {
                msg.value = responseData.split('\n')[0].split(':')[1];
                msg.params = responseData.split('\n')[1].split(':')[1].split(',');
             } else {
                msg.value = responseData.split(':')[1];
             }
        } else if ((typeof(responseData) === 'string') && responseData.startsWith('literal:')) {
            msg.value = responseData.split(':')[1];
            msg.literal = true;
        } else if (defaultMsg) {
            msg.value = defaultMsg;
        }
        return msg;
    },

    handleResponse = function(title, defaultMsg, response, callback) {
        'use strict';
        var msg = { value: "server.error", literal: false, params: [] },
            responseData = (typeof(response) === 'string') ? response : response.data;

        unblockUI();
        msg = parseResponse(responseData, defaultMsg);

        if (callback) {
            callback(title, msg.value, msg.params);
        } else if (msg.literal) {
            jAlert(msg.value, jQuery.i18n.prop(title), callback);
        } else {
            motechAlert(msg.value, title, msg.params);
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
    doneTypingInterval = 5 * 1000,

    /* Default settings for jgrid */
    jgridDefaultSettings = function () {
        'use strict';
        $.extend($.jgrid.defaults, {
            shrinkToFit: true,
            autowidth: true,
            rownumbers: false,
            rowNum: 10,
            rowList: [10, 20, 50, 100],
            width: '100%',
            height: 'auto',
            sortorder: 'asc',
            recordpos: 'left',
            onPaging: function (pgButton) {
                var newPage = 1, last;
                if ("user" === pgButton) {
                    newPage = parseInt($(this.p.pager).find('input:text').val(), 10);
                    last = parseInt($(this).getGridParam("lastpage"), 10);
                    if (newPage > last || newPage === 0) {
                        return 'stop';
                    }
                }
            }
        });
    };


function captureTyping(callback) {
    'use strict';
    clearTimeout(typingTimer);
    typingTimer = setTimeout(callback, doneTypingInterval);
}

function innerLayout(conf, eastConfig) {
    'use strict';

    var config = conf || {},
        options = {
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
            togglerContent_open: '<i class="fa fa-caret-right button"></i>',
            togglerContent_closed: '<i class="fa fa-caret-left button"></i>',
            autoReopen: false, // auto-open panes that were previously auto-closed due to 'no room'
            noRoom: true,
            east__togglerAlign_closed: "top", // align to top of resizer
            east__togglerAlign_open: "top",
            east__togglerLength_open: 35,
            east__togglerLength_closed: 35,
            east__togglerTip_open: "Close This Pane",
            east__togglerTip_closed: "Open This Pane",
            east__initClosed: true,
            initHidden: true,
            onresize_start: function () {
                $('#inner-center').trigger("change");
                return false;
            },
            onopen: function (paneName) {
                $('.ui-layout-button-toggle-'+paneName).addClass('active');
            },
            onclose: function (paneName) {
                $('.ui-layout-button-toggle-'+paneName).removeClass('active');
            },
            defaults: {
                enableCursorHotkey: false
            }
        },
        element = angular.element('#outer-center'),
        button = angular.element(eastConfig && eastConfig.button),
        defaults = {},
        layout;

    element.livequery(function () {
        $.extend(options, defaults, config);

        layout = element.layout(options);
        layout.destroy();
        layout = element.layout(options);

        if (eastConfig && eastConfig.show) {
            button.livequery(function () {
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
