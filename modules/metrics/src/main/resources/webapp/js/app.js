(function () {
    'use strict';

    angular.module('metrics', ['motech-dashboard', 'metrics.controllers', 'metrics.services', 'ngCookies']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/metrics/settings', {templateUrl: '../metrics/resources/partials/settings.html', controller: 'MetricsConfigCtrl'}).
                when('/metrics/metrics', {templateUrl: '../metrics/resources/partials/metrics.html', controller: 'MetricsCtrl'});
        }]);
}());
