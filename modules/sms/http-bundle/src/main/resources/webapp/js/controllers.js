'use strict';

/* Controllers */

function SmsController($scope, $http) {

    function resetDeliveryStatus() {
        $scope.smsDeliveryResult = "";
    };

    $scope.sms = {};

    $scope.sendSMS = function () {
        resetDeliveryStatus();
        $http.post('../smshttp/api/outbound', $scope.sms).success(
            function (data, status, headers, config) {
                $scope.smsDeliveryResult = $scope.msg('sms.sent');
            }
        ).error(function (data, status, headers, config) {
                $scope.smsDeliveryResult = $scope.msg('sms.failed') + " - " +status;
            });
    };

    $scope.smsDeliveryStatus = function () {
        return $scope.smsDeliveryResult;
    };

    resetDeliveryStatus();

}
