(function () {
    'use strict';

    /* App Module */

    var scheduler = angular.module('motech-scheduler', [
            'motech-dashboard', 'motechSchedulerService', 'ngCookies', 'ngRoute','motech-widgets'
        ]);

        scheduler.config( function ($routeProvider) {

            $routeProvider.when(
                '/scheduler',
                {
                    templateUrl: '../scheduler/partials/scheduler.html',
                    controller: 'SchedulerCtrl'
                }
            );

            $routeProvider.otherwise({
                redirectTo: '/scheduler'
            });

        });

}());
