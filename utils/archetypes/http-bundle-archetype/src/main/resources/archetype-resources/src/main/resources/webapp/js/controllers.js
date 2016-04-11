#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
(function() {
    'use strict';

    /* Controllers */
    var controllers = angular.module('${artifactId}HelloWorld.controllers', []);

    controllers.controller('HelloWorldController', function($scope, $http, HelloWorld, ModalFactory) {

        $scope.sayHelloResult = '';
        $scope.sayHelloCount = 0;

        $scope.sayHello = function() {
            var messageKey = '${artifactId}.info.noResponse';
            $scope.sayHelloResult = $scope.msg(messageKey);
            HelloWorld.get({}, function(response) {
                $scope.sayHelloResult = response.message;
                messageKey = '${artifactId}.info.serviceResponse';
                ModalFactory.motechAlert(response.message, messageKey);
                $scope.sayHelloCount++;
            });
        };

    });
}());
