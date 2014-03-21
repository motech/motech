(function () {

    'use strict';

    /* Controllers */

    var controllers = angular.module('scheduler.controllers', []);


    controllers.controller('SchedulerCtrl', function($scope, $http, $routeParams, MotechScheduler) {
        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        }, {
            show: true,
            button: '#scheduler-filters'
        });
    });

}());

