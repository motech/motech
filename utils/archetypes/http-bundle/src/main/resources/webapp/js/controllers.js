(function() {
    'use strict';

    /* Controllers */
    var helloWorldModule = angular.module('hello-world');

    helloWorldModule.controller('HelloWorldController', function($scope, $http,
            HelloWorld) {

        /* try putting the get() invokation inside another function definition */
        $scope.sayHello = HelloWorld.get({}, function(response) {
            $scope.sayHelloResult = response.message;
            $scope.sayHelloCount++;
        })
        
        $scope.sayHelloResult = 'nothing said yet';
        $scope.sayHelloCount = 0;

    });
}());
