(function () {
    'use strict';

    /* App Module */

    angular.module('motech-sms', ['motech-dashboard', 'ngCookies', 'bootstrap', 'smsRecordsService']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/smstest', {templateUrl: '../sms/resource/partials/smstest.html', controller: 'SmsController'}).
                when('/smslogging', {templateUrl: '../sms/resource/partials/smslogging.html'}).
                otherwise({redirectTo: '/smstest'});
    }]);
}());