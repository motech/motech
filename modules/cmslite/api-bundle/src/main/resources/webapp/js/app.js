(function () {
    'use strict';

    /* App Module */

    angular.module('motech-cmslite', ['motech-dashboard', 'resourceServices',
                                    'ngCookies', 'bootstrap', 'motech-widgets']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/resources', {templateUrl: '../cmsliteapi/resources/partials/resources.html', controller: 'ResourceCtrl'}).
                otherwise({redirectTo: '/resources'});
        }]);
}());
