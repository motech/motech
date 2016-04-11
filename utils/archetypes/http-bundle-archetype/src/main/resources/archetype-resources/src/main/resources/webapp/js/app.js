#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
(function () {
    'use strict';

    /* App Module */

    angular.module('${artifactId}HelloWorld', ['motech-dashboard', '${artifactId}HelloWorld.controllers', '${artifactId}HelloWorld.directives',
            '${artifactId}HelloWorld.services', 'ngCookies', 'uiServices'])
        .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/${artifactId}/helloWorld', {templateUrl: '../${artifactId}/resources/partials/say-hello.html', controller: 'HelloWorldController'});
    }]);
}());
