(function () {
    'use strict';

    /* App Module */

    angular.module('batch', ['motech-dashboard', 'batch.controllers', 'batch.directives', 'batch.services', 'ngCookies', 'ngRoute'])
        .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/batch/', {templateUrl: '../motech-test-module-http/resources/partials/say-hello.html', controller: 'HelloWorldController'});
    }]);
}());
