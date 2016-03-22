(function () {
    'use strict';

    var uiServices = angular.module('uiServices', ['ngResource']);

    uiServices.factory('Menu', function($resource) {
        return $resource('module/menu');
    });

    uiServices.service('ModalServ', function () {
    var modals = [], blocked = [];

        this.open = function (dialog) {
            modals.push(dialog);
            blocked.push(false);
            console.log('Modal dodany do listy, modals: '+modals.length+', blocked '+blocked.length);
            if (modals.length > 1) {
                modals[modals.length-2].close();
                console.log('Teraz lista ma dlugosc '+modals.length+', poprzedni modal zamkniety');
            }
            dialog.open();
            console.log('Modal otworzony!!!');
        };

        this.close = function (dialog) {
            dialog.close();
            modals.splice(modals.length-1);
            blocked.splice(blocked.length-1);
            console.log(modals.length+' '+blocked.length);
            if (modals.length > 0) {
                modals[modals.length-1].open();
            }
        };

        this.blockUI = function () {
            console.log('blockUI');
            if (!blocked[blocked.length-1]) {
                var dialog = new BootstrapDialog({
                    message: function(dialogRef){
                        var $message = $(
                            '<div class="splash-logo"><img src="./../../static/common/img/motech-logo.gif" alt="motech-logo"></div>' +
                            '<div class="clearfix"></div>' +
                            '<div class="splash-loader"><img src="./../../static/common/img/loadingbar.gif" alt="Loading..."></div>' +
                            '<div class="clearfix"></div>' + '<br>');
                        return $message;
                    },
                    closable: false
                });
                dialog.realize();
                dialog.getModalHeader().hide();
                dialog.getModalFooter().hide();
                dialog.getModalContent().addClass('splash');
                dialog.getModalContent().css('margin-top', '40%');
                dialog.getModalBody().css('padding', '0px');
                modals.push(dialog);
                if (modals.length > 1) {
                    modals[modals.length-2].close();
                }
                dialog.open();
                blocked.push(true);
                console.log(+modals.length+' '+blocked.length);
            }
        };

        this.unblockUI = function () {
            if (blocked[blocked.length-1]) {
                modals[modals.length-1].close();
                modals.splice(modals.length-1);
                blocked.splice(blocked.length-1);
                console.log(modals.length+' '+blocked.length);
            }
        };

        this.remove = function() {
            console.log('Modal zamkniety!!!');
            modals.splice(modals.length-1);
            blocked.splice(blocked.length-1);
            console.log(modals.length+' '+blocked.length);
            if (modals.length > 0) {
                modals[modals.length-1].open();
                console.log('otworzony poprzedni modal!');
            }
        };

        this.motechAlert = function (msg, title, params, callback) {
            if (modals.length > 0) {
                modals[modals.length-1].close();
            }
            var dialog = BootstrapDialog.alert({
                title: jQuery.i18n.prop(title),
                message: jQuery.i18n.prop.apply(null, [msg].concat(params)),
                callback: callback
            });
            modals.push(dialog);
            blocked.push(false);
            console.log('Modal dodany do listy, modals: '+modals.length+', blocked '+blocked.length);
        };

        this.motechConfirm = function (msg, title, callback) {
            if (modals.length > 0) {
                modals[modals.length-1].close();
            }
            var dialog = BootstrapDialog.confirm({
                title: jQuery.i18n.prop(title),
                message: jQuery.i18n.prop(msg),
                callback: callback
            });
            modals.push(dialog);
            blocked.push(false);
            console.log(modals.length+' '+blocked.length);
        };

        this.motechAlertStackTrace = function (msg, title, response, callback) {
            if (modals.length > 0) {
                modals[modals.length-1].close();
            }
            if( title === null || title === '') {
                title = 'Alert';
            }
            var dialog = BootstrapDialog.alert({
                title: title,
                message: jQuery.i18n.prop(msg).bold() + ": \n" + response,
                callback: callback
            });
            modals.push(dialog);
            blocked.push(false);
            console.log(modals.length+' '+blocked.length);
        };

        this.jFormErrorHandler = function (response) {
            this.unblockUI();
            var dialog = new BootstrapDialog ({
                type: BootstrapDialog.TYPE_DANGER,
                message: response.status + ": " + response.statusText,
                buttons: [{
                    label: 'OK',
                    action: function(dialog){
                        this.close(dialog);
                    }
                }]
            });
            this.open(dialog);
        };

        this.handleResponse = function (title, defaultMsg, response, callback) {
            var msg = { value: "server.error", literal: false, params: [] },
                responseData = (typeof(response) === 'string') ? response : response.data;

            this.unblockUI();
            msg = parseResponse(responseData, defaultMsg);

            if (callback) {
                callback(title, msg.value, msg.params);
            } else if (msg.literal) {
                BootstrapDialog.alert({
                    type: BootstrapDialog.TYPE_DANGER,
                    title: jQuery.i18n.prop(title),
                    message: msg.value,
                    callback: callback
                });
            } else {
                this.motechAlert(msg.value, title, msg.params);
            }
        };

        this.alertHandler = function (msg, title) {
            return function() {
                this.unblockUI();
                this.motechAlert(msg, title);
            };
        };

        this.alertHandlerWithCallback = function (msg, callback) {
            return function() {
                this.unblockUI();
                this.motechAlert(msg, 'server.success', callback);
            };
        };



    });

}());
