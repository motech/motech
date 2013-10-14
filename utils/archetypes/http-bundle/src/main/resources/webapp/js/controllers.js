(function() {

    'use strict';

    var helloWorld = angular.module('hello-world');

    phonecatControllers.controller('HelloWorldCtrl', function($scope, HelloWorld) {
        $scope.helloResonse = "";

        $scope.sayHello = function() {
            $scope.helloResonse = HelloWorld.query();
        }
    });
}());
