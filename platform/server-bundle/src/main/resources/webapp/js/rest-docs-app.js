(function () {
    'use strict';

    /* Module for the REST documentation section */

    var restDocModule = angular.module('motech-rest-docs', ['motech-dashboard']);

    restDocModule.config(['$routeProvider', function($routeProvider) {
          $routeProvider.when('/server/rest-docs/:resturl', {templateUrl: '../server/resources/partials/rest-docs.html', controller: 'ServerRestDocsCtrl'});
    }]);

    restDocModule.controller('ServerRestDocsCtrl', function ($scope, $routeParams) {
        $scope.afunc = function() {
            return $routeParams;
        };
    });
}());

