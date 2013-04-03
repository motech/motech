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

    $scope.cssClass = function(prop) {
        var msg = 'control-group';

        if (!$scope.hasValue(prop)) {
            msg = msg.concat(' error');
        }

        return msg;
    }

    $scope.hasValue = function(prop) {
        return $scope.settings.hasOwnProperty(prop) && $scope.settings[prop] != undefined;
    }

    $scope.hasAccountSettings = function() {
        return $scope.hasValue('commcareBaseUrl') && $scope.hasValue('commcareDomain') && $scope.hasValue('username') && $scope.hasValue('password');
    }
}
