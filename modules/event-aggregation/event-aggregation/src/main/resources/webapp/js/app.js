(function () {
    'use strict';

    var app = angular.module('eventAggregation', ['motech-dashboard', 'eventAggregation.services',
        'eventAggregation.controllers', 'eventAggregation.directives', 'ngCookies', 'ngRoute'])
        .config(['$routeProvider', function($routeProvider) {
            $routeProvider.
                when('/eventAggregation', {
                    templateUrl: '../event-aggregation/resources/partials/rules.html', controller: 'RulesController'
                }).
                when('/eventAggregation/rules/create/:scheduleType', {
                    templateUrl: '../event-aggregation/resources/partials/new_rule.html', controller: 'NewRulesController'
                }).
                when('/eventAggregation/rules', {
                    templateUrl: '../event-aggregation/resources/partials/rules.html', controller: 'RulesController'
                }).
                when('/eventAggregation/rules/:ruleName/aggregations/:eventStatus', {
                    templateUrl: '../event-aggregation/resources/partials/aggregations.html', controller: 'AggregationsController'
                }).
                when('/eventAggregation/rules/:ruleName/edit', {
                    templateUrl: '../event-aggregation/resources/partials/new_rule.html', controller: 'NewRulesController'
                });
        }]);
}());
