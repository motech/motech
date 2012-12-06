'use strict';

/* App Module */

angular.module('motech-smshttp', ['motech-dashboard', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/sms-test', {templateUrl: '../smshttp/partials/smstest.html', controller: SmsController}).
//            when('/dataMapping', {templateUrl: '../commcare/partials/dataMapping.html', controller: DataMappingCtrl}).
            otherwise({redirectTo: '/sms-test'});
}]);
