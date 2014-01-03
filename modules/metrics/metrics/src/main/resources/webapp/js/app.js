(function () {
    'use strict';

    /* App Module */

    angular.module('motech-platform-metrics', ['motech-dashboard', 'ngCookies', 'ngRoute'])
        .config(['$routeProvider',
        function ($routeProvider) {
           $routeProvider.
               when('/graphite', {templateUrl: '../metrics/resources/partials/operations.html', controller: 'OperationsCtrl'}).
               when('/settings', {templateUrl: '../metrics/resources/partials/settings.html', controller: 'SettingsCtrl'}).
               otherwise({redirectTo: '/graphite'});
   }]);
}());
