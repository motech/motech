(function() {
    'use strict';

    /* Controllers */
    var helloWorldModule = angular.module('hello-world');

    helloWorldModule.controller('HelloWorldController', function($scope, $http) {

        $scope.sayHelloResult = 'nothing said yet';

        $scope.sayHello = function() {
            $scope.sayHelloResult = '';
            $http.get('../http-bundle/sayHello').success(
                    function(data, status, headers, config) {
                        $scope.sayHelloResult = data;
                    }).error(
                    function(data, status, headers, config) {
                        $scope.sayHelloResult = 'error saying hello';
                    });
        };
        
    });
}());
