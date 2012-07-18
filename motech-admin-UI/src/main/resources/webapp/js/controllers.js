'use strict';

/* Controllers */

function BundleListCtrl($scope, Bundle, i18nService) {

    const LOADING_STATE = 'LOADING';

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

    $scope.msg = function(key) {
        return i18nService.getMessage(key);
    }

    Bundle.prototype.isActive = function() {
        return this.state == 'ACTIVE';
    }
}

function StatusMsgCtrl($scope, StatusMessage) {
    $scope.messages = StatusMessage.query();

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
}