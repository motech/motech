(function () {
    'use strict';

    /* App Module */

    angular.module('motech-ivr', ['motech-dashboard', 'motech-widgets', 'TestCallServices', 'CalllogSearchService', 'CalllogCountService',
        'CalllogPhoneNumberService', 'CalllogMaxDurationService', 'ngCookies', 'bootstrap']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/test-call', {templateUrl:'../ivr/partials/test-call.html'}).
                when('/call-logs', { templateUrl:'../ivr/partials/call-logs.html' }).
                otherwise({redirectTo:'/test-call'});
        }]);
}());
