(function () {
    'use strict';

    /* Services */


    angular.module('scheduler.services', ['ngResource']).factory('MotechScheduler', function($resource) {
        return $resource('../scheduler/api/jobs?name=&rows=10&page=0&sortColumn=name&sortDirection=asc&activity=NOTSTARTED,ACTIVE,FINISHED&status=ERROR,BLOCKED,PAUSED,OK&timeFrom=&timeTo=');
    });
}());
