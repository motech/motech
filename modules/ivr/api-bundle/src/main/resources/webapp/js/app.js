'use strict';

/* App Module */

angular.module('motech-ivr', ['motech-dashboard', 'TestCallServices', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/test-call', {templateUrl: '../ivr/partials/test-call.html'}).
            otherwise({redirectTo: '/test-call'});
}]);
