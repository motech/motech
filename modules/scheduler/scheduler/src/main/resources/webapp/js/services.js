(function () {
    'use strict';

    /* Services */

    angular.module('scheduler.services', ['ngResource']).factory('JobsService', function($resource) {
        var listener = {},
            jobs = {},
            params = {
                name: "",
                rows: "10",
                page: "1",
                sortColumn: "name",
                sortDirection: "asc",
                activity: "NOTSTARTED,ACTIVE,FINISHED",
                status: "ERROR,BLOCKED,PAUSED,OK",
                timeFrom: "",
                timeTo: ""
            },
            source = $resource('../scheduler/api/jobs', {}, {
            "get": {
                method: "GET"
            },
            "getDetails": {
                url: '../scheduler/api/job/details',
                method: "POST",
                params: {}
            },
            "pauseJob": {
                url: '../scheduler/api/job/pause',
                method: "POST",
                params: {}
            },
            "resumeJob": {
                url: '../scheduler/api/job/resume',
                method: "POST",
                params: {}
            },
            "deleteJob": {
                url: '../scheduler/api/job/delete',
                method: "POST",
                params: {}
            }
        });
        return {
            "get": function() {
                return jobs;
            },
            "getDetails": function(job, success) {
                source.getDetails(job, success);
            },
            "pauseJob": function(job, success) {
                source.pauseJob(job, success);
            },
            "resumeJob": function(job, success) {
                source.resumeJob(job, success);
            },
            "deleteJob": function(job, success) {
                source.deleteJob(job, success);
            },
            "setListener": function(scope) {
                listener = scope;
            },
            "setParam": function(fieldName, value) {
                params[fieldName] = value;
            }, 
            "fetchJobs": function() {
                source.get(params, function(data) {
                    jobs = data;
                    listener.$broadcast('jobsFetched');
                });
            }
        }
    });
}());
