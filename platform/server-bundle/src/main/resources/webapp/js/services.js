(function () {
    'use strict';

    var uiServices = angular.module('uiServices', ['ngResource']);
    uiServices.factory('Menu', function($resource) {
        return $resource('module/menu');
    });

    uiServices.service('BootstrapDialogManager', function () {
        var modalsList = [];

        this.open = function (dialog) {
            modalsList.push(dialog);
            if (modalsList.length > 1) {
                modalsList[modalsList.length-2].close();
            }
            dialog.open();
        };

        this.close = function (dialog) {
            dialog.close();
            modalsList.splice(modalsList.length-1);
            if (modalsList.length > 0) {
                modalsList[modalsList.length-1].open();
            }
        };

        this.remove = function () {
            modalsList[modalsList.length-1].close();
            modalsList.splice(modalsList.length-1);
            if (modalsList.length > 0) {
                modalsList[modalsList.length-1].open();
            }
        };
    });

    uiServices.service('LoadingModal', function () {
        var dialog, open = false;

        this.open = function () {
            if (!open) {
                dialog = new BootstrapDialog({
                    message: function(dialogRef) {
                        var $message = $('<div></div>'),
                        pageToLoad = dialog.getData('pageToLoad');
                        $message.load(pageToLoad);

                        return $message;
                    },
                    data: {
                        'pageToLoad': '../server/resources/partials/loading-splash.html'
                    },
                    closable: false,
                    draggable: false
                });

                dialog.realize();
                dialog.getModalHeader().hide();
                dialog.getModalFooter().hide();
                dialog.getModalContent().addClass('splash');
                dialog.getModalContent().css('margin-top', '40%');
                dialog.getModalBody().css('padding', '0');

                dialog.open();
                open = true;
            }
        };

        this.close = function () {
            if (open) {
                dialog.close();
            }
            open = false;
        };

        this.isOpen = function () {
            return open;
        };
    });

    uiServices.factory('ModalFactory', function (LoadingModal, BootstrapDialogManager) {

        var modalFactory = {},

        defaultOptions = {
            type: 'type-primary',
            title: null,
            message: null,
            closable: false,
            draggable: false,
            buttonLabel: BootstrapDialog.DEFAULT_TEXTS.OK,
            btnCancelLabel: BootstrapDialog.DEFAULT_TEXTS.CANCEL,
            btnOKLabel: BootstrapDialog.DEFAULT_TEXTS.OK,
            btnOKClass: null,
            callback: null
        },

        makeAlert = function (paramOptions) {
            var dialog,
                options = angular.copy(defaultOptions);

            options = $.extend(true, options, paramOptions);

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
                            dialog.setData('btnClicked', true);
                            if (typeof dialog.getData('callback') === 'function' && dialog.getData('callback').call(this, true) === false) {
                                return false;
                            }
                            return BootstrapDialogManager.close(dialog);
                        }
                    }]
            });
            BootstrapDialogManager.open(dialog, false);
            return dialog;
        },

        makeConfirm = function (paramOptions) {
            var dialog,
                options = angular.copy(defaultOptions);

            options = $.extend(true, options, paramOptions);

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
                        if (typeof dialog.getData('callback') === 'function' && dialog.getData('callback').call(this, false) === false) {
                            return false;
                        }
                        return BootstrapDialogManager.close(dialog);
                    }
                }, {
                    label: options.btnOKLabel,
                    cssClass: options.btnOKClass,
                    action: function (dialog) {
                        if (typeof dialog.getData('callback') === 'function' && dialog.getData('callback').call(this, true) === false) {
                            return false;
                        }
                        return BootstrapDialogManager.close(dialog);
                    }
                }]
            });
            BootstrapDialogManager.open(dialog, false);
            return dialog;
        };

        modalFactory.alert = function (msg, title, callback) {
            if (typeof msg === 'object' && msg.constructor === {}.constructor) {
                return makeAlert(msg);
            } else {
                return makeAlert({
                    message: msg,
                    title: title && title !== undefined ? title : jQuery.i18n.prop('server.bootstrapDialog.alert'),
                    callback: callback && callback !== undefined ? callback : null
                });
            }
        };

        modalFactory.motechAlert = function (msg, title, params, callback) {
            return modalFactory.alert({
                title: jQuery.i18n.prop(title),
                message: jQuery.i18n.prop.apply(null, [msg].concat(params)),
                callback: callback && callback !== undefined ? callback : null
            });
        };

        modalFactory.motechAlertStackTrace = function (msg, title, response, callback) {
            if (title === null || title === '') {
                title = 'server.bootstrapDialog.alert';
            }
            return modalFactory.alert({
                type: 'type-danger', //Error type
                title: jQuery.i18n.prop(title),
                message: jQuery.i18n.prop(msg).bold() + ": \n" + response,
                callback: callback
            });
        };

        modalFactory.confirm = function (msg, title, callback) {
            if (typeof msg === 'object' && msg.constructor === {}.constructor) {
                return makeConfirm(msg);
            } else {
                return makeConfirm({
                    message: jQuery.i18n.prop(msg),
                    title: title && title !== undefined ? jQuery.i18n.prop(title) : jQuery.i18n.prop('server.bootstrapDialog.confirm'),
                    callback: callback && callback !== undefined ? callback : null
                });
            }
        };

        modalFactory.errorAlert = function (response) {
            LoadingModal.close();
            return modalFactory.alert({
                type: 'type-danger', //Error type
                title: jQuery.i18n.prop('server.error'),
                message: response.status + ": " + response.statusText
            });
        };

        modalFactory.handleResponse = function (title, defaultMsg, response, callback) {
            var msg = { value: "server.error", literal: false, params: [] },
                responseData = (typeof(response) === 'string') ? response : response.data;

            LoadingModal.close();
            msg = parseResponse(responseData, defaultMsg);

            if (callback) {
                callback(title, msg.value, msg.params);
            } else if (msg.literal) {
                return modalFactory.alert({
                    type: 'type-danger',
                    title: jQuery.i18n.prop(title),
                    message: msg.value,
                    callback: callback
                });
            } else {
                return modalFactory.motechAlert(msg.value, title, msg.params);
            }
        };

        modalFactory.alertHandler = function (msg, title, callback) {
            return function() {
                LoadingModal.close();
                return modalFactory.alert(msg, title, callback);
            };
        };

        modalFactory.angularHandler = function(title, defaultMsg, callback) {
            return function(response) {
                return modalFactory.handleResponse(title, defaultMsg, response, callback);
            };
        };

        modalFactory.handleWithStackTrace = function(title, defaultMsg, response) {
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
            return modalFactory.motechAlertStackTrace(msg, title, response);
        };

        return modalFactory;

    });
}());
