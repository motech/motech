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

    emailModule.controller('EmailLoggingController', function($scope, EmailAuditService) {

        $scope.availableRange = ['all','table', 'month'];
        $scope.loggingRange = $scope.availableRange[0];

        $scope.change = function(selectedRange) {
            $scope.loggingRange = selectedRange;

            if($scope.loggingRange === 'month') {
                $('#exportDate').removeClass('hidden');
                $scope.month = $('#monthPicker').val();
            } else {
                $('#exportDate').addClass('hidden');
            }
        };

        $("#monthPicker").focus(function () {
            $(".ui-datepicker-current").hide();
        });

        $scope.exportEmailLog = function () {
            $('#exportEmailLogModal').modal('hide');
            $('#exportEmailLogForm').ajaxSubmit({
                type: 'GET',
                data: {
                    range: $scope.loggingRange,
                    month: $('#monthPicker').val()
                },
                success: function () {
                    window.location.replace("../email/emails/export?range="+$scope.loggingRange+"&month="+$('#monthPicker').val());
                    $('#exportEmailLogForm').resetForm();
                    $('#exportEmailLogModal').modal('hide');
                    $scope.change('all');
                }
            });
        };

        $scope.closeExportEmailLogModal = function () {
            $('#exportEmailLogForm').resetForm();
            $('#exportEmailLogModal').modal('hide');
            $scope.change('all');
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
