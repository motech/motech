'use strict';

/* Controllers */

function BundleListCtrl($scope, Bundle, i18nService) {

    var LOADING_STATE = 'LOADING';

    $scope.bundles = Bundle.query();

    $scope.stopBundle = function(bundle) {
        bundle.$stop(dummyHandler, angularErrorHandler);
    }

    $scope.startBundle = function(bundle) {
        bundle.state = LOADING_STATE;
        bundle.$start(dummyHandler, function() {
            bundle.state = 'RESOLVED';
            motechAlert('bundles.error.start', 'error');
        });
    }

    $scope.restartBundle = function(bundle) {
        bundle.state = LOADING_STATE;
        bundle.$restart(dummyHandler, function() {
             bundle.state = 'RESOLVED';
             motechAlert('bundles.error.restart', 'error');
        });
    }

    $scope.uninstallBundle = function(bundle) {
        jConfirm(jQuery.i18n.prop('bundles.uninstall.confirm'), jQuery.i18n.prop("confirm"), function(val) {
            if (val) {
                var oldState = bundle.state;
                bundle.state = LOADING_STATE;

                bundle.$uninstall(function() {
                    // remove bundle from list
                    $scope.bundles.remove(bundle);
                },
                function() {
                    motechAlert('bundles.error.uninstall', 'error');
                    bundle.state = oldState;
                });
            }
        });
    }

    $scope.getIconClass = function(bundle) {
        var cssClass = '';
        if (!bundle.isActive()) {
            cssClass = 'dullImage';
        }
        return cssClass;
    }

    $scope.bundleStable = function(bundle) {
        return bundle.state != LOADING_STATE;
    }

    $scope.submitBundle = function() {
        $('#bundleUploadForm').ajaxSubmit({
            success : function(data) {
                $scope.bundles.push(data);
            },
            error : jFormErrorHandler
        });
    }

    Bundle.prototype.isActive = function() {
        return this.state == 'ACTIVE';
    }
}

function StatusMsgCtrl($scope, $timeout, StatusMessage) {
    var UPDATE_INTERVAL = 1000 * 30;

    $scope.ignoredMessages = [];
    $scope.messages = [];

    StatusMessage.query(function(data) {
        var mgs = jQuery.grep(data, function(message, index) {
            return jQuery.inArray(message._id, $scope.ignoredMessages) == -1; // not in ignored list
        });
        $scope.messages = mgs;
    });

    $scope.getCssClass = function(msg) {
        var cssClass = 'msg';
        if (msg.level == 'ERROR') {
            cssClass += ' error';
        }
        return cssClass;
    }

    StatusMessage.prototype.getDate = function() {
        return new Date(this.date);
    }

    var update = function() {
        StatusMessage.query(function (newMessages) {
            if (!messagesEqual(newMessages, $scope.messages)) {
                messageFilter(newMessages);
            }

            $timeout(update, UPDATE_INTERVAL);

            function messagesEqual(arg1, arg2) {
                if(arg1.length !== arg2.length) {
                    return false;
                }

                for(var i = arg1.length; i--;) {
                    if(arg1[i]._id != arg2[i]._id)
                        return false;
                }

                return true;
            }
        });
    }

    $scope.remove = function(message) {
        $scope.messages.remove(message);
        $scope.ignoredMessages.push(message._id);
    }

    $timeout(update, UPDATE_INTERVAL);

    var messageFilter = function(data) {
        return jQuery.grep(data, function(message, index) {
            return jQuery.inArray(message._id, $scope.ignoredMessages) == -1; // not in ignored list
        });
    }
}

function MasterCtrl($scope, i18nService) {
    $scope.msg = function(key) {
        return i18nService.getMessage(key);
    }
}