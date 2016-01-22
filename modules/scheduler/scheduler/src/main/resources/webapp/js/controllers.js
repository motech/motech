(function () {

    'use strict';

    /* Controllers */

    var controllers = angular.module('scheduler.controllers', []);

    controllers.controller('SchedulerCtrl', function($scope, $http, $timeout, $routeParams, JobsService) {

        $scope.jobDetails = {};

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        }, {
            show: true,
            button: '#scheduler-filters'
        });

        $scope.$on('jobsFetched', function() {
            $scope.jobs = JobsService.get();
            unblockUI();
        });

        JobsService.setListener($scope);
        JobsService.fetchJobs({});

        $scope.reload = function(page, params) {
            if (page >= 1 && page <= $scope.jobs.total) {
                JobsService.setParam("page", page);
                JobsService.fetchJobs();
            }
        };
        
        $scope.getDetails = function(job) {
            if ($scope.jobDetails[job.name] !== undefined) {
            } else {
                if (!job.uiDefined) {
                    JobsService.getDetails(job, function(data) {
                        $scope.jobDetails[job.name] = data;
                    });
                }
            }
        };

        $scope.updateJob = function(job, updated) {
            job.activity = updated.activity;
            job.status = updated.status;
            job.name = updated.name;
            job.group = updated.group;
            job.startDate = updated.startDate;
            job.nextFireDate = updated.nextFireDate;
            job.endDate = updated.endDate;
            job.jobType = updated.jobType;
            job.info = updated.info;
            job.uiDefined = updated.uiDefined;
        };

        $scope.pauseJob = function(job) {
            motechConfirm("scheduler.confirm.pause", "scheduler.confirm", function(response) {
                if (response) {
                    blockUI();
                    JobsService.pauseJob(job, function(updated) {
                        $scope.updateJob(job, updated);
                        unblockUI();
                    });
                }
            })
        };

        $scope.resumeJob = function(job) {
            motechConfirm("scheduler.confirm.resume", "scheduler.confirm", function(response) {
                if (response) {
                    blockUI();
                    JobsService.resumeJob(job, function(updated) {
                       $scope.updateJob(job, updated);
                       unblockUI();
                    });
                }
            })
        };

        $scope.deleteJob = function(job) {
            motechConfirm("scheduler.confirm.delete", "scheduler.confirm", function(response) {
                if (response) {
                    blockUI();
                    JobsService.deleteJob(job, function() {
                        JobsService.fetchJobs();
                    });
                }
            })
        };

        $scope.getStatusIcon = function(status) {
            if (status === "OK") {
                return "play";
            } else if (status === "PAUSED") {
                return "pause";
            } else if (status === "BLOCKED") {
                return "ban";
            } else if (status === "ERROR") {
                return "exclamation-triangle";
            }
            return undefined;
        };

        $scope.getActivityIcon = function(activity) {
            if (activity === "NOTSTARTED") {
                return "clock-o";
            } else if (activity === "ACTIVE") {
                return "play";
            } else if (activity === "FINISHED") {
                return "check";
            }
            return undefined;
        };
    });

}());

