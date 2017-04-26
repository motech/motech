(function () {
    'use strict';

    /* Services */

    angular.module('scheduler.services', ['ngResource']).factory('JobsService', function($resource) {
        var listener = {},
            currentJob = {},
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
            "createJob": {
                url: "../scheduler/api/jobs/new",
                method: "POST"
            },
            "updateJob": {
                url: "../scheduler/api/jobs/edit",
                method: "POST"
            },
            "getDetails": {
                url: '../scheduler/api/job/details',
                method: "GET",
                params: {}
            },
            "getJob": {
                url: '../scheduler/api/job',
                method: "GET"
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
            "createJob": function(job, success, failure) {
                source.createJob(job, success, failure);
            },
            "updateJob": function(job, success, failure) {
                source.updateJob(job, success, failure);
            },
            "getDetails": function(job, success) {
                source.getDetails(job, success);
            },
            "getCurrentJob": function(success) {
                source.getJob(currentJob, success);
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
            "setCurrentJob": function(job) {
                currentJob = job;
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
