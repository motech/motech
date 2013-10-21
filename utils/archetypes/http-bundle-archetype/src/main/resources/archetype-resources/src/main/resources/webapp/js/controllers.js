(function() {
    'use strict';

    /* Controllers */
    var helloWorldModule = angular.module('hello-world');

    helloWorldModule.controller('HelloWorldController', function($scope, $http, HelloWorld) {

        $scope.sayHelloResult = '';
        $scope.sayHelloCount = 0;
        
        $scope.sayHello = function() {
            $scope.sayHelloResult = $scope.msg('${artifactId}.info.noResponse');
            HelloWorld.get({}, function(response) {
                $scope.sayHelloResult = response.message;
                motechAlert(response.message, '${artifactId}.info.serviceResponse');
                $scope.sayHelloCount++;
            });
        };
        
    });
}());
