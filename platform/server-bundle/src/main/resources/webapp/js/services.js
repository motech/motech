(function () {
    'use strict';

    var uiServices = angular.module('uiServices', ['ngResource']);
    uiServices.factory('Menu', function($resource) {
        return $resource('module/menu');
    });

    uiServices.service('BootstrapDialogManager', function () {
        var modals2 = [];

        this.open = function (dialog, isThisLoadingModal) {
            modals2.push(dialog);
            console.log("open"+modals2.length);
            if (modals2.length > 1) {
                modals2[modals2.length-2].close();
            }
            dialog.open();
        };

        this.close = function (dialog) {
            dialog.close();
            modals2.splice(modals2.length-1);
            console.log("close"+modals2.length);
            if (modals2.length > 0) {
                modals2[modals2.length-1].open();
            }
        };

        this.remove = function () {
            modals2[modals2.length-1].close();
            modals2.splice(modals2.length-1);
            console.log("remove"+modals2.length);
            if (modals2.length > 0) {
                modals2[modals2.length-1].open();
            }
        };
    });

    uiServices.service('LoadingModal', function (BootstrapDialogManager) {
        var open = false;

        this.open = function () {
            if (!open) {
                var dialog = new BootstrapDialog({
                    message: function(dialogRef){
                        var $message = $(
                            '<div class="splash-logo"><img src="./../../static/common/img/motech-logo.gif" alt="motech-logo"></div>' +
                            '<div class="clearfix"></div>' +
                            '<div class="splash-loader"><img src="./../../static/common/img/loadingbar.gif" alt="Loading..."></div>' +
                            '<div class="clearfix"></div>' + '<br>');
                        return $message;
                    },
                    closable: false,
                    draggable: false
                });
                dialog.realize();
                dialog.getModalHeader().hide();
                dialog.getModalFooter().hide();
                dialog.getModalContent().addClass('splash');
                dialog.getModalContent().css('margin-top', '40%');
                dialog.getModalBody().css('padding', '0px');

                BootstrapDialogManager.open(dialog, true);
                open = true;
            }
        };

        this.close = function () {
            if (open) {
                BootstrapDialogManager.remove();
            }
            open = false;
        };

        this.isOpen = function () {
            return open;
        };
    });

    uiServices.service('Modal', function (LoadingModal, BootstrapDialogManager) {

        this.alert = function () {
            var options = {}, dialog,
            defaultOptions = {
                type: BootstrapDialog.TYPE_PRIMARY,
                title: null,
                message: null,
                closable: false,
                draggable: false,
                buttonLabel: BootstrapDialog.DEFAULT_TEXTS.OK,
                callback: null
            };

            if (typeof arguments[0] === 'object' && arguments[0].constructor === {}.constructor) {
                options = $.extend(true, defaultOptions, arguments[0]);
            } else {
                options = $.extend(true, defaultOptions, {
                    message: arguments[0],
                    callback: typeof arguments[1] !== 'undefined' ? arguments[1] : null
                });
            }

            dialog = new BootstrapDialog({
                type: options.type,
                title: options.title,
                message: options.message,
                closable: options.closable,
                draggable: options.draggable,
                data: {
                    callback: options.callback
                },
                buttons: [{
                        label: options.buttonLabel,
                        action: function (dialog) {
                            BootstrapDialogManager.close(dialog);
                            dialog.setData('btnClicked', true);
                            if (typeof dialog.getData('callback') === 'function' && dialog.getData('callback').call(this, true) === false) {
                                return false;
                            }
                        }
                    }]
            });
            BootstrapDialogManager.open(dialog, false);
        };

        this.confirm = function () {
            var options = {}, dialog,
            defaultOptions = {
                type: BootstrapDialog.TYPE_PRIMARY,
                title: null,
                message: null,
                closable: false,
                draggable: false,
                btnCancelLabel: BootstrapDialog.DEFAULT_TEXTS.CANCEL,
                btnOKLabel: BootstrapDialog.DEFAULT_TEXTS.OK,
                btnOKClass: null,
                callback: null
            };
            if (typeof arguments[0] === 'object' && arguments[0].constructor === {}.constructor) {
                options = $.extend(true, defaultOptions, arguments[0]);
            } else {
                options = $.extend(true, defaultOptions, {
                    message: arguments[0],
                    closable: false,
                    buttonLabel: BootstrapDialog.DEFAULT_TEXTS.OK,
                    callback: typeof arguments[1] !== 'undefined' ? arguments[1] : null
                });
            }
            if (options.btnOKClass === null) {
                options.btnOKClass = ['btn', options.type.split('-')[1]].join('-');
            }

            dialog = new BootstrapDialog({
                type: options.type,
                title: options.title,
                message: options.message,
                closable: options.closable,
                draggable: options.draggable,
                data: {
                    callback: options.callback
                },
                buttons: [{
                    label: options.btnCancelLabel,
                    action: function (dialog) {
                        BootstrapDialogManager.close(dialog);
                        if (typeof dialog.getData('callback') === 'function' && dialog.getData('callback').call(this, false) === false) {
                            return false;
                        }
                    }
                }, {
                    label: options.btnOKLabel,
                    cssClass: options.btnOKClass,
                    action: function (dialog) {
                        BootstrapDialogManager.close(dialog);
                        if (typeof dialog.getData('callback') === 'function' && dialog.getData('callback').call(this, true) === false) {
                            return false;
                        }
                    }
                }]
            });
            BootstrapDialogManager.open(dialog, false);
        };

        this.openLoadingModal = function () {
            LoadingModal.open();
        };

        this.closeLoadingModal = function () {
            LoadingModal.close();
        };

        this.motechAlert = function (msg, title, params, callback) {
            this.alert({
                title: jQuery.i18n.prop(title),
                message: jQuery.i18n.prop.apply(null, [msg].concat(params)),
                callback: callback
            });
        };

        this.motechAlertStackTrace = function (msg, title, response, callback) {
            if( title === null || title === '') {
                title = 'Alert';
            }
            this.alert({
                title: title,
                message: jQuery.i18n.prop(msg).bold() + ": \n" + response,
                callback: callback
            });
        };

        this.motechConfirm = function (msg, title, callback) {
            this.confirm({
                title: jQuery.i18n.prop(title),
                message: jQuery.i18n.prop(msg),
                callback: callback
            });
        };

        this.jFormErrorHandler = function (response) {
            this.closeLoadingModal();
            this.alert({
                type: BootstrapDialog.TYPE_DANGER, //Error type
                message: response.status + ": " + response.statusText
            });
        };

        this.handleResponse = function (title, defaultMsg, response, callback) {
            var msg = { value: "server.error", literal: false, params: [] },
                responseData = (typeof(response) === 'string') ? response : response.data;

            this.closeLoadingModal();
            msg = parseResponse(responseData, defaultMsg);

            if (callback) {
                callback(title, msg.value, msg.params);
            } else if (msg.literal) {
                this.alert({
                    type: BootstrapDialog.TYPE_DANGER,
                    title: jQuery.i18n.prop(title),
                    message: msg.value,
                    callback: callback
                });
            } else {
                this.motechAlert(msg.value, title, msg.params);
            }
        };

        this.alertHandler = function (msg, title, callback) {
            return function() {
                this.closeLoadingModal();
                this.motechAlert(msg, title, callback);
            };
        };

        this.angularHandler = function(title, defaultMsg, callback) {
            return function(response) {
                this.handleResponse(title, defaultMsg, response, callback);
            };
        };

        this.handleWithStackTrace = function(title, defaultMsg, response) {
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
            this.motechAlertStackTrace(msg, title, response);
        };

    });
}());
