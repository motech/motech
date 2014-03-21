(function () {
    'use strict';

    /* Services */

    var services = angular.module('tasks.services', ['ngResource']);

    services.factory('Channels', function ($resource) {
        return $resource('../tasks/api/channel');
    });

    services.factory('Tasks', function ($resource) {
        return $resource('../tasks/api/task/:taskId', {taskId: '@_id'});
    });

    services.factory('Activities', function ($resource) {
        return $resource('../tasks/api/activity/:taskId');
    });

    services.factory('DataSources', function ($resource) {
        return $resource('../tasks/api/datasource');
    });

    services.factory('Settings', function($resource) {
        return $resource('../tasks/api/settings');
    });

}());
