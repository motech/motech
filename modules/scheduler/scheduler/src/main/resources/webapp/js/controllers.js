(function () {

    'use strict';

    /* Controllers */

    var controllers = angular.module('scheduler.controllers', []);

    controllers.controller('SchedulerCtrl', function($scope, $timeout, $routeParams, JobsService) {

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

        $scope.newJob = function() {
            blockUI();
            window.location.href = "#/scheduler/createJob?action=new";
        }

        $scope.editJob = function(job) {
            JobsService.setCurrentJob(job);
            blockUI();
            window.location.href = "#/scheduler/createJob?action=edit";
        }

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

    controllers.controller('SchedulerCreateJobCtrl', function($scope, $timeout, $routeParams, JobsService) {

        innerLayout({}, {
            show: false,
            button: '#scheduler-filters'
        });

        $scope.job = {};
        $scope.job.motechEvent = {};
        $scope.motechEventParameters = [];
        $scope.action = $routeParams.action;

        $scope.jobTypes = [
            { displayName: "Cron", name: "CRON" }, { displayName: "Repeating", name: "REPEATING" },
            { displayName: "Repeating Period", name: "REPEATING_PERIOD"}, { displayName: "Run Once", name: "RUN_ONCE" },
            { displayName: "Day of Week", name: "DAY_OF_WEEK" }
        ];
            
        $scope.days = [
            { displayName: "Monday", value: 0 }, { displayName: "Tuesday", value: 1 },
            { displayName: "Wednesday", value: 2 }, { displayName: "Thursday", value: 3 },
            { displayName: "Friday", value: 4 }, {displayName: "Saturday", value: 5 },
            { displayName: "Sunday", value: 6}
        ];

        function containsKey(map, key) {
            var result = false;
            angular.forEach($scope.motechEventParameters, function(element) {
                if (element.key === key) {
                    result = true;
                }
            });
            return result;
        };

        $scope.addToMap = function(key, value) {
            if (containsKey($scope.motechEventParameters, key)) {
                motechAlert("scheduler.keyAlreadyExists", "schedulerKeyAlreadyExists");
            } else {
                $scope.motechEventParameters.push({
                    "key": key,
                    "value": value
                });
            }
            $timeout(function() {
                $scope.key = "";
                $scope.value = "";
            });

        };

        $scope.resetMap = function() {
            motechConfirm("scheduler.confirm.resetMap", "scheduler.confirm", function(response) {
                if (response) {
                    $timeout(function() {
                        $scope.motechEventParameters = [];
                    });
                }
            });
        }

        $scope.removeFromMap = function(key) {
            motechConfirm("scheduler.confirm.removeItem", "scheduler.confirm", function(response) {
                if (response) {
                    var id;
                    for (var i = 0; i < $scope.motechEventParameters.length; i += 1) {
                        if ($scope.motechEventParameters[i].key === key) {
                            id = i;
                        }
                    }
                    if (id > -1) {
                        $timeout(function () {
                            $scope.motechEventParameters.splice(id, 1);
                        });
                    }
                }
            });
        };

        $scope.parseToDateTime = function(date) {
            if (date && date !== "") {
                var parts = date.split(" ");
                return parts[0] + "T" + parts[1] + new Date().toString().match(/([-\+][0-9]+)\s/)[1];
            }
            return date;
        }

        $scope.createOrUpdateJob = function(action) {
            var job = {};

            job.motechEvent = {};

            for (var field in $scope.job) {
                job[field] = $scope.job[field];
            }

            job.motechEvent.parameters = {};

            angular.forEach($scope.motechEventParameters, function(parameter) {
                job.motechEvent.parameters[parameter.key] = parameter.value;
            });

            if (job.startDate) {
                job.startDate = $scope.parseToDateTime(job.startDate);
            }

            if (job.endDate) {
                job.endDate = $scope.parseToDateTime(job.endDate);
            }

            if ($scope.job.days) {
                for (var day = 0; day < $scope.job.days.length; day += 1) {
                    if (day === 0) {
                        job.days = [];
                    }
                    job.days[day] = $scope.job.days[day];
                }
            }

            job.uiDefined = true;

            function success() {
                window.location.href="#/scheduler";
                unblockUI();
            }

            function failure(response) {
                motechAlert(response.data.key, "scheduler.error", response.data.params);
                unblockUI();
            }

            if (action === 'new') {
                blockUI();
                JobsService.createJob(job, success, failure);
            } else if (action === 'edit'){
                motechConfirm("scheduler.confirm.updateJob", "scheduler.confirm", function(response) {
                    if (response) {
                        blockUI();
                        JobsService.updateJob(job, success, failure);
                    }
                });
            }
        };

        $scope.typeChanged = function() {
            var job = {};
            blockUI();
            job['@jobType'] = $scope.job['@jobType'];
            job.motechEvent = $scope.job.motechEvent;
            job.startDate = $scope.job.startDate;
            $scope.job = job;
            unblockUI();
        }

        $scope.parseDateToString = function(milliseconds) {
            return moment(milliseconds).format("YYYY-MM-DD HH:mm:ss");
        }

        if ($scope.action === 'edit') {
            JobsService.getCurrentJob(function(data) {
                $scope.job = data;
                if ($scope.job.startDate) {
                    $scope.job.startDate = $scope.parseDateToString($scope.job.startDate);
                }

                if ($scope.job.endDate) {
                    $scope.job.endDate = $scope.parseDateToString($scope.job.endDate);
                }
                for (var key in data.motechEvent.parameters) {
                    $scope.addToMap(key, data.motechEvent.parameters[key]);
                }
            });
        }

        unblockUI();
    });

}());

