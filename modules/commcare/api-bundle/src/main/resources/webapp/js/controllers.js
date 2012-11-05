'use strict';

/* Controllers */

function SettingsCtrl($scope, Settings) {
    $scope.settings = Settings.get();
    $scope.eventStrategyOptions = [ 'minimal', 'partial', 'full' ];

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
        return $scope.settings.hasOwnProperty(prop) && $scope.settings[prop] != undefined;
    }

    $scope.isUrl = function() {
        var urlregex = new RegExp("(((http|https)://)|(www\.))+(([a-zA-Z0-9\._-]+\.[a-zA-Z]{2,6})|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(/[a-zA-Z0-9\&amp;%_\./-~-]*)?");
        return $scope.hasValue('commcareDomain') && urlregex.test($scope.settings.commcareDomain);
    }

    $scope.hasAccountSettings = function() {
        return $scope.hasValue('commcareDomain') && $scope.hasValue('username') && $scope.hasValue('password') && $scope.isUrl();
    }
}
