'use strict';

/* App Module */

angular.module('motech-sms', ['motech-dashboard', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/sms-test', {templateUrl: '../sms/resource/partials/smstest.html', controller: SmsController}).
            otherwise({redirectTo: '/sms-test'});
}]);
