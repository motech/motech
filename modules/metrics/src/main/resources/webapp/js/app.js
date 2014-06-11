(function () {
    'use strict';

    /* App Module */

    angular.module('metrics', ['motech-dashboard', 'metrics.controllers', 'metrics.directives', 'ngCookies', 'ngRoute'])
        .config(['$routeProvider',
        function ($routeProvider) {
           $routeProvider.
               when('/metrics/graphite', {templateUrl: '../metrics/resources/partials/operations.html', controller: 'OperationsCtrl'}).
               when('/metrics/settings', {templateUrl: '../metrics/resources/partials/settings.html', controller: 'SettingsCtrl'});
   }]);
}());
