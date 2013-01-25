'use strict';

/* put your routes here */

angular.module('${artifactId}', ['motech-dashboard', 'YourModuleServices', 'ngCookies', 'bootstrap'])
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider
            .when('/welcome', { templateUrl: '../${artifactId}/resources/partials/welcome.html', controller: YourController })
            .otherwise({redirectTo: '/welcome'});
    }]);
