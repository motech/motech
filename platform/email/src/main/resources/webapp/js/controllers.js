(function () {
    'use strict';

    /* Controllers */
    var emailModule = angular.module('motech-email');

    emailModule.controller('SendEmailController', function ($scope, SendEmailService) {
        $scope.mail = {};

        $scope.sendEmail = function () {
            SendEmailService.save(
                {},
                $scope.mail,
                function () {
                    motechAlert('email.header.success', 'email.sent');
                },
                function (response) {
                    handleWithStackTrace('email.header.error', 'server.error', response);
                }
            );
        };
    });

    emailModule.controller('SettingsController', function ($scope, SettingsService) {
        $scope.settings = SettingsService.get();

        $scope.timeMultipliers = {
            'hours': $scope.msg('email.settings.log.units.hours'),
            'days': $scope.msg('email.settings.log.units.days'),
            'weeks': $scope.msg('email.settings.log.units.weeks'),
            'months': $scope.msg('email.settings.log.units.months'),
            'years': $scope.msg('email.settings.log.units.years')
        };

        $scope.submit = function () {

            SettingsService.save(
                {},
                $scope.settings,
                function () {
                    motechAlert('email.header.success', 'email.settings.saved');
                    $scope.settings = SettingsService.get();
                },
                function (response) {
                    handleWithStackTrace('email.header.error', 'server.error', response);
                }
            );
        };

        $scope.isNumeric = function (prop) {
            return $scope.settings.hasOwnProperty(prop) && /^[0-9]+$/.test($scope.settings[prop]);
        };

        $scope.purgeTimeControlsDisabled = function () {
            if ($scope.settings.logPurgeEnable.localeCompare("true") === 0) {
                return false;
            } else {
                return true;
            }
        };

    });
}());
