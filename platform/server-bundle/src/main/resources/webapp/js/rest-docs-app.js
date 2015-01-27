(function () {
    'use strict';

    /* Module for the REST documentation section */

    var restDocModule = angular.module('rest-docs', ['motech-dashboard', 'ngRoute']);

    restDocModule.config(['$routeProvider', function($routeProvider) {
          $routeProvider.when('/rest-docs/:restUrl', {templateUrl: '../server/resources/partials/rest-docs.html', controller: 'ServerRestDocsCtrl'});
    }]);

    restDocModule.controller('ServerRestDocsCtrl', function ($scope, $location, $http) {

        $scope.json = '';

        $scope.getRestModuleName = function() {
            var splitPath = $location.path().split('/');
            return splitPath[splitPath.length - 1];
        };

        // get the url for the docs, then get the url
        $http.get("../server/module/rest-docs/" + getRestModuleName).success(function(data) {
            $http.get(data).success(function(data) {
                $scop.json = data;
            }).error(alertHandler('server.error', 'server.error.rest.model'));
        }).error(alertHandler('server.error', 'server.error.rest.url')));
    });
}());

