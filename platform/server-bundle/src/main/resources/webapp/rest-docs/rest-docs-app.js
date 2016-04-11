(function () {
    'use strict';

    /* Module for the REST documentation section */

    var restDocModule = angular.module('rest-docs', ['motech-dashboard', 'uiServices']);

    restDocModule.config(['$routeProvider', function($routeProvider) {
          $routeProvider.when('/rest-docs/:restUrl', {templateUrl: '../server/resources/partials/rest-docs.html',
                                                      controller: 'ServerRestDocsCtrl'});
    }]);

    restDocModule.controller('ServerRestDocsCtrl', function ($scope, $location, $http, ModalFactory) {

        $scope.getRestModuleName = function() {
            $scope.before = $location.path();
            var splitPath = $location.path().split('/');
            return splitPath[splitPath.length - 1];
        };

        // get the url for the docs, then get the url
        $http.get("../server/module/rest-docs/" + $scope.getRestModuleName()).success(function(data) {
            // call that url, send the server prefix in the param
            window.swaggerUi = new SwaggerUi({
                url: getServerPrefix() + "/module" + data + "?serverPrefix=" + getServerPrefix(),
                dom_id: "swagger-ui-container",
                supportedSubmitMethods: ['get', 'post', 'put', 'delete'],
                onFailure: function(data) {
                    ModalFactory.motechAlert(data, "error");
                },
                onComplete: function() {
                    // remove hrefs starting with hashbang (no need for them anyway)
                    // in order to prevent Angular routing inside Swagger UI
                    $("#swagger-ui-container").find('a').each(function() {
                        var href = $(this).attr('href');
                        if (href && href.startsWith('#!')) {
                            $(this).removeAttr('href');
                        }
                    });
                }
            });

            window.swaggerUi.load();
        }).error( ModalFactory.alertHandler('server.error', 'server.error.rest.url'));
    });
}());
