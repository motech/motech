(function () {
    'use strict';

    var uiServices = angular.module('uiServices', ['ngResource']);

    uiServices.factory('Menu', function($resource) {
        return $resource('module/menu');
    });

    uiServices.service('BootstrapDialogManager', ['LoadingModal', '$rootScope', '$timeout', function (LoadingModal, $rootScope, $timeout) {
        var that = this, modalsList = [];

        LoadingModal.openEvent($rootScope, function(event) { that.hide(); });
        LoadingModal.closeEvent($rootScope, function(event) { that.show(); });

        this.open = function (dialog) {
            modalsList.push(dialog);

            if (modalsList.length > 1) {
                modalsList[modalsList.length-2].close();
            }
            if (!LoadingModal.isOpen()) {
                dialog.open();
            }
        };

        this.close = function (dialog) {
            dialog.close();
            that.remove(dialog);
            if (modalsList.length > 0) {
                modalsList[modalsList.length-1].open();
            }
        };

        this.remove = function (dialog) {
            var i, modalsListLength = modalsList.length;

            for (i = 0; i < modalsListLength; i += 1) {
                if (modalsList[i].options.id === dialog.options.id) {
                    modalsList.splice(i, 1);
                    break;
                }
            }
        };

        this.show = function () {
            if (modalsList.length > 0 && !LoadingModal.isOpen()) {
                modalsList[modalsList.length-1].open();
            } else if (modalsList.length > 0) {
                $timeout(function() {
                    that.show();
                }, 500);
            }
        };

        this.hide = function () {
            if (modalsList.length > 0) {
                modalsList[modalsList.length-1].close();
            }
        };

        this.onhide = function (dialog) {
            $timeout(function() {
                if (modalsList.length > 0) {
                    that.remove(dialog);
                    if (modalsList.length > 0) {
                        modalsList[modalsList.length-1].open();
                    }
                }
            }, 200);
        };

    }]);

    uiServices.service('LoadingModal', ['$rootScope', function ($rootScope) {
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
                $rootScope.$emit('loadingModalOpen');
            }
        };

        this.close = function () {
            if (open) {
                dialog.close();
                $rootScope.$emit('loadingModalClose');
            }
            open = false;
        };

        this.isOpen = function () {
            return open;
        };

        this.openEvent = function (scope, callback) {
            var handler = $rootScope.$on('loadingModalOpen', callback);
            scope.$on('$destroy', handler);
        };

        this.closeEvent = function (scope, callback) {
            var handler = $rootScope.$on('loadingModalClose', callback);
            scope.$on('$destroy', handler);
        };

    }]);

    uiServices.factory('ModalFactory', ['BootstrapDialogManager', function (BootstrapDialogManager) {

        var modalFactory = {},

        defaultOptions = {
            type: 'type-primary',
            title: null,
            message: null,
            closable: false,
            draggable: false,
            onhide: null,
            onshow: null,
            buttonLabel: BootstrapDialog.DEFAULT_TEXTS.OK,
            btnCancelLabel: BootstrapDialog.DEFAULT_TEXTS.CANCEL,
            btnOKLabel: BootstrapDialog.DEFAULT_TEXTS.OK,
            btnOKClass: null,
            callback: null
        },

        makeAlert = function (paramOptions) {
            var dialog,
                options = angular.copy(defaultOptions),
                defaultButtons = [{
                    label: options.buttonLabel,
                    action: function (dialogRef) {
                        dialogRef.setData('btnClicked', true);
                        if (typeof dialogRef.getData('callback') === 'function' && dialogRef.getData('callback').call(this, true) === false) {
                            return false;
                        }
                        return BootstrapDialogManager.close(dialogRef);
                    }
                }];

            options = $.extend(true, options, paramOptions);

            dialog = new BootstrapDialog({
                type: options.type,
                size: options.size,
                title: options.title,
                message: options.message,
                closable: options.closable,
                draggable: options.draggable,
                onhide: options.onhide,
                onshow: options.onshow,
                data: {
                    callback: options.callback
                },
                buttons: options.buttons && options.buttons !== undefined ? options.buttons : defaultButtons
            });
            BootstrapDialogManager.open(dialog, false);
            return dialog;
        },

        makeConfirm = function (paramOptions) {
            var dialog,
                options = angular.copy(defaultOptions),
                defaultButtons = [{
                    label: options.btnCancelLabel,
                    action: function (dialogRef) {
                        if (typeof dialogRef.getData('callback') === 'function' && dialogRef.getData('callback').call(this, false) === false) {
                            return false;
                        }
                        return BootstrapDialogManager.close(dialogRef);
                    }
                }, {
                    label: options.btnOKLabel,
                    cssClass: options.btnOKClass,
                    action: function (dialogRef) {
                        if (typeof dialogRef.getData('callback') === 'function' && dialogRef.getData('callback').call(this, true) === false) {
                            return false;
                        }
                        return BootstrapDialogManager.close(dialogRef);
                    }
                }];

            options = $.extend(true, options, paramOptions);

            if (options.btnOKClass === null) {
                options.btnOKClass = ['btn', options.type.split('-')[1]].join('-');
            }

            dialog = new BootstrapDialog({
                type: options.type,
                size: options.size,
                title: options.title,
                message: options.message,
                closable: options.closable,
                draggable: options.draggable,
                onhide: options.onhide,
                onshow: options.onshow,
                data: {
                    callback: options.callback
                },
                buttons: options.buttons && options.buttons !== undefined ? options.buttons : defaultButtons
            });
            BootstrapDialogManager.open(dialog, false);
            return dialog;
        };

        modalFactory.showConfirm = function (msg, title, callback) {
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

        modalFactory.showAlert = function (msg, title, callback) {
            if (typeof msg === 'object' && msg.constructor === {}.constructor) {
                return makeAlert(msg);
            } else {
                return makeAlert({
                    message: jQuery.i18n.prop(msg),
                    title: title && title !== undefined ? jQuery.i18n.prop(title) : jQuery.i18n.prop('server.bootstrapDialog.alert'),
                    callback: callback && callback !== undefined ? callback : null
                });
            }
        };

        modalFactory.showSuccessAlert = function (msg, title, callback) {
            return makeAlert({
                type: 'type-success',
                title: title && title !== undefined ? jQuery.i18n.prop(title) : jQuery.i18n.prop('server.success'),
                message: jQuery.i18n.prop(msg),
                callback: callback && callback !== undefined ? callback : null
            });
        };

        modalFactory.showErrorAlert = function (msg, title, rawMsg) {
            if (!msg && !rawMsg) {
                msg = 'server.error.errorUnknown';
            }
            return makeAlert({
                type: 'type-danger', //Error type
                title: title && title !== undefined ? jQuery.i18n.prop(title) : jQuery.i18n.prop('server.error'),
                message: msg && msg !== undefined ? jQuery.i18n.prop(msg) : rawMsg
            });
        };

        modalFactory.showErrorAlertWithResponse = function (defaultMsg, title, response, callback) {
            var msg = { value: "server.error", literal: false, params: [] },
                responseData = (typeof(response) === 'string') ? response : response.data;

            msg = parseResponse(responseData, defaultMsg);

            if (callback) {
                callback(msg.value, title, msg.params);
            } else if (msg.literal) {
                return makeAlert({
                    type: 'type-danger',
                    title: title && title !== undefined ? jQuery.i18n.prop(title) : jQuery.i18n.prop('server.error'),
                    message: msg.value,
                    callback: callback
                });
            } else {
                return makeAlert({
                    type: 'type-danger', //Error type
                    title: title && title !== undefined ? jQuery.i18n.prop(title) : jQuery.i18n.prop('server.error'),
                    message: jQuery.i18n.prop.apply(null, [msg.value].concat(msg.params))
                });
            }
        };

        modalFactory.showErrorWithStackTrace = function(msg, title, response) {
            var defaultMsg = 'server.error';
            if (response) {
                if(response.responseText) {
                    response = response.responseText;
                } else if(response.data) {
                    response = response.data;
                }
            }
            if (!msg) {
                msg = defaultMsg;
            }
            return makeAlert({
                type: 'type-danger', //Error type
                size: 'size-wide',
                title: title && title !== undefined ? jQuery.i18n.prop(title) : jQuery.i18n.prop('server.error'),
                message: jQuery.i18n.prop(msg).bold() + ": \n" + response
            });
        };

        return modalFactory;

    }]);
}());
