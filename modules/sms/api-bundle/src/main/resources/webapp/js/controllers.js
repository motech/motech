'use strict';

/* Controllers */

function SmsController($scope, $http) {

    function resetDeliveryStatus() {
        $scope.smsDeliveryResult = "";
        $('.smsAlert').removeClass("alert alert-success alert-error");
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
                $('.smsAlert').removeClass("alert-error").addClass('alert alert-success');
            }
        ).error(function (data, status, headers, config) {
                setDeliveryStatus($scope.msg('sms.failed') + " - " +status);
                $('.smsAlert').removeClass("alert-success").addClass('alert alert-error');
            });
    };

    $scope.smsDeliveryStatus = function () {
        return $scope.smsDeliveryResult;
    };

    resetDeliveryStatus();

}