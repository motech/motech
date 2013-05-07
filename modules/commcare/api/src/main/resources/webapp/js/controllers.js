(function () {
    'use strict';

    /* Controllers */

    var commcareModule = angular.module('commcare');

    commcareModule.controller('SettingsCtrl', function ($scope, Settings, Connection, Permissions) {

        $scope.permissions = Permissions.query();

        $scope.eventStrategyOptions = [ 'minimal', 'partial', 'full' ];

        $scope.settings = Settings.get(function success() {
            if (!$scope.settings.eventStrategy) {
                $scope.settings.eventStrategy = $scope.eventStrategyOptions[0];
            }
            $scope.verify();
        });

        $scope.verify = function() {
            Connection.verify($scope.settings.accountSettings,
                function success()  {
                    $scope.verifySuccessMessage = 'Connection successful';
                    $scope.verifyErrorMessage = '';
                    $scope.connectionVerified = true;
                    $('.commcare .switch-small').bootstrapSwitch('setActive', true);
                },
                function failure(response) {
                    $scope.verifyErrorMessage = response.data.message;
                    $scope.verifySuccessMessage = '';
                    $scope.connectionVerified = false;
                    $('.commcare .switch-small').bootstrapSwitch('setActive', false);
                });
        };

        $scope.isVerifyError = function() {
            return $scope.connectionVerified === false;
        };

        $scope.isVerifySuccess = function() {
            return $scope.canMakeConnection() === true && $scope.connectionVerified === true;
        };

        $scope.hasValue = function(prop) {
            return $scope.settings.hasOwnProperty(prop) && $scope.settings[prop] !== undefined;
        };

        $scope.hasAccountValue = function(prop) {
            return $scope.settings.hasOwnProperty('accountSettings') && $scope.settings.accountSettings[prop] !== undefined;
        };

        $scope.canMakeConnection = function() {
            return $scope.hasAccountValue('commcareBaseUrl') &&
                $scope.hasAccountValue('commcareDomain') &&
                $scope.hasAccountValue('username') &&
                $scope.hasAccountValue('password');
        };

        $scope.saveSettings = function(element) {
            $scope.settings.$save(
                function success() {
                    var controlWrapper = $(element).next('.form-hints');
                    $(controlWrapper).children('.save-status').remove();
                    controlWrapper.append("<span class='save-status form-hint-success'><span class='icon-ok icon-white'/> Value has been saved</span>");
                    $(controlWrapper.children('.save-status')[0]).delay(5000).fadeOut(function() {
                        $(this).remove();
                    });
                },
                function error() {
                    var controlWrapper = $(element).next('.form-hints');
                    $(controlWrapper).children('.save-status').remove();
                    controlWrapper.append("<span class='save-status form-hint'><span class='icon-remove icon-white'/> Unable to save value</span>");
                    $(controlWrapper.children('.save-status')[0]).delay(10000).fadeOut(function() {
                        $(this).remove();
                    });
                });
        };

    });

    commcareModule.controller('ModulesCtrl', function ($scope, Modules) {

        $scope.modules = Modules.query();

    });

    commcareModule.controller('CaseSchemasCtrl', function () {
    });

}());
