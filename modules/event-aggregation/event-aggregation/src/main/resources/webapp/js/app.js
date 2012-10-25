'use strict';

var app = angular.module('motech-event-aggregation', ['motech-dashboard', 'AggregationRuleServices', 'ngCookies', 'bootstrap'])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.
            when('/', {
                templateUrl: '../event-aggregation/partials/dashboard.html'
            }).
            when('/rules/create/:scheduleType', {
                templateUrl: '../event-aggregation/partials/new_rule.html', controller: NewRulesController
            }).
            when('/rules', {
                templateUrl: '../event-aggregation/partials/rules.html', controller: RulesController
            }).
            when('/rules/:ruleName/aggregations/:eventStatus', {
                templateUrl: '../event-aggregation/partials/aggregations.html', controller: AggregationsController
            }).
            when('/rules/:ruleName/edit', {
                templateUrl: '../event-aggregation/partials/new_rule.html', controller: NewRulesController
            }).
            otherwise({
                redirectTo: '/'
            });
    }]);
