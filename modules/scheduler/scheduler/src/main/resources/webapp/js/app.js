(function () {
    'use strict';

    /* App Module */

    var scheduler = angular.module('scheduler', [ 'motech-dashboard', 'scheduler.services',
        'scheduler.controllers', 'scheduler.directives', 'ngCookies', 'uiServices'
    ]);

    scheduler.config(function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.when("scheduler", "/dashboard");
        $urlRouterProvider.otherwise("/dashboard");
        $stateProvider
            .state('scheduler', {
                url: "/scheduler",
                abstract: true,
                views: {
                    "moduleToLoad": {
                    templateUrl: "../scheduler/index.html"
                    }
                }
            })
            .state('scheduler.dashboard', {
                url: "/dashboard",
                parent: 'scheduler',
                views: {
                    'schedulerview': {
                        templateUrl: '../scheduler/partials/scheduler.html',
                        controller: 'SchedulerCtrl'
                    }
                }
            })
            .state('scheduler.createJob', {
                url: "/createJob?action?currJob",
                parent: 'scheduler',
                views: {
                    'schedulerview': {
                        templateUrl: '../scheduler/partials/createJob.html',
                        controller: 'SchedulerCreateJobCtrl'
                    }
                }
            });
    });
}());
