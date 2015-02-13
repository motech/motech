(function () {
    'use strict';

    /* Module for the REST documentation section */

    var restDocModule = angular.module('rest-docs', ['motech-dashboard', 'ngRoute']);

    restDocModule.config(['$routeProvider', function($routeProvider) {
          $routeProvider.when('/rest-docs/:restUrl', {templateUrl: '../server/resources/partials/rest-docs.html', controller: 'ServerRestDocsCtrl'});
    }]);

    restDocModule.controller('ServerRestDocsCtrl', function ($scope, $location, $http) {

        // start when location is available
        $scope.$on("$locationChangeSuccess", function() {
            // get the url for the docs, then get the url
            $http.get("../server/module/rest-docs/" + $scope.getRestModuleName()).success(function(data) {
                // call that url, send the server prefix in the param
                window.swaggerUi = new SwaggerUi({
                    url: "../" + data + "?serverPrefix=" + getServerPrefix(),
                    dom_id: "swagger-ui-container",
                    supportedSubmitMethods: ['get', 'post', 'put', 'delete'],
                    onFailure: function(data) {
                        motechAlert(data, "error");
                    }
                });

                window.swaggerUi.load();
            }).error(alertHandler('server.error', 'server.error.rest.url'));
        });

        $scope.getRestModuleName = function() {
            $scope.before = $location.path();
            var splitPath = $location.path().split('/');
            return splitPath[splitPath.length - 1];
        };
    });
}());

