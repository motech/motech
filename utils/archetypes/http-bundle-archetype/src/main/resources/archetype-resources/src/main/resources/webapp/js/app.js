#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
(function () {
    'use strict';

    /* App Module */

    angular.module('helloWorld', ['motech-dashboard', 'helloWorld.controllers', 'helloWorld.directives', 'helloWorld.services', 'ngCookies', 'ngRoute'])
        .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/helloWorld/', {templateUrl: '../${artifactId}/resources/partials/say-hello.html', controller: 'HelloWorldController'});
    }]);
}());
