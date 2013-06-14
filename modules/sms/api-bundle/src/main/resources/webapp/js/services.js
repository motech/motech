(function () {
    'use strict';

    /* Services */

    angular.module('smsRecordsService', ['ngResource']).factory('SmsRecords', function($resource) {
        return $resource('../smsapi/smslogging');
    });
}());