'use strict';

/* Controllers */

function SettingsCtrl($scope, Settings) {
    $scope.settings = Settings.get();
    $scope.eventStrategyOptions = [ 'minimal', 'partial', 'full' ];

    $scope.showEventStrategyOptions = function() {
        return $scope.settings.commcareDomain && $scope.isUrl() && $scope.settings.username && $scope.settings.password;
    }

    $scope.submit = function() {
        $scope.settings.$save(function() {
            motechAlert('settings.success.saved', 'main.saved');
        }, function() {
            motechAlert('settings.error.saved', 'main.error');
        });
    }

    $scope.submitAndRestart = function() {
        $scope.settings.$save({restart: true}, function () {
            var loc = new String(window.location), indexOf = loc.indexOf('?');
            window.location = loc.substring(0, indexOf);
        }, function() {
            motechAlert('settings.error.saved', 'main.error');
        });
    }

    $scope.cssClass = function(prop) {
        var msg = 'control-group';

        if (!$scope.hasValue(prop)) {
            msg = msg.concat(' error');
        } else if (prop == 'commcareDomain' && !$scope.isUrl()) {
            msg = msg.concat(' warning');
        }

        return msg;
    }

    $scope.hasValue = function(prop) {
        var has = true;

        if (!$scope.settings.hasOwnProperty(prop) || $scope.settings[prop] == undefined) {
            has = false;
        }

        return has;
    }

    $scope.isUrl = function() {
        return $scope.settings.commcareDomain != undefined && $scope.settings.commcareDomain.indexOf("http://")==0;
    }
}
