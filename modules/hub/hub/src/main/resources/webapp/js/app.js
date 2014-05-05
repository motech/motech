(function () {
    'use strict';

    /* App Module */

    angular.module('hub', ['motech-dashboard', 'hub.controllers', 'hub.directives', 'hub.services', 'ngCookies', 'ngRoute'])
        .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/hub/', {templateUrl: '../motech-test-module-http/resources/partials/say-hello.html', controller: 'HelloWorldController'});
    }]);
}());
