(function () {

    'use strict';

    /* Controllers */

    var schedulerModule = angular.module('motech-scheduler');


    schedulerModule.controller('SchedulerCtrl', function($scope, $http, $routeParams, MotechScheduler) {
        $scope.innerLayout.addToggleBtn("#scheduler-filters", "east");
    });

}());

