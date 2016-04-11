#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
(function () {
    'use strict';

    /* App Module */

    angular.module('${artifactId}HelloWorld', ['motech-dashboard', '${artifactId}HelloWorld.controllers', '${artifactId}HelloWorld.directives', '${artifactId}HelloWorld.services', 'ngCookies'])
        .config(['$stateProvider', function ($stateProvider) {
            $stateProvider
                .state('${artifactId}', {
                    abstract: true,
                    url: '/${artifactId}',
                    views: {
                        'moduleToLoad': {
                            templateUrl: '../${artifactId}/resources/index.html'
                        }
                    }
                })
                .state('${artifactId}.helloWorld', {
                    url: '/helloWorld',
                    views: {
                        '${artifactId}View': {
                            templateUrl: '../${artifactId}/resources/partials/say-hello.html',
                            controller: 'HelloWorldController'
                        }
                    }
                });
        }]
    );
}());
