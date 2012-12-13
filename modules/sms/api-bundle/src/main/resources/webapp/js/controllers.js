'use strict';

/* Controllers */

function SmsController($scope, $http) {

    function resetDeliveryStatus() {
        $scope.smsDeliveryResult = "";
    };

    function setDeliveryStatus(status){
       $scope.smsDeliveryResult = status;
    };

    $scope.sms = {};

    $scope.sendSMS = function () {
        setDeliveryStatus($scope.msg('sms.sending'));
        $http.post('../smsapi/outbound', $scope.sms).success(
            function (data, status, headers, config) {
                setDeliveryStatus($scope.msg('sms.sent'));
            }
        ).error(function (data, status, headers, config) {
                setDeliveryStatus($scope.msg('sms.failed') + " - " +status);
            });
    };

    $scope.smsDeliveryStatus = function () {
        return $scope.smsDeliveryResult;
    };

    resetDeliveryStatus();

}
