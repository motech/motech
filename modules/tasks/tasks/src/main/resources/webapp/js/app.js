(function () {
    'use strict';

    /* App Module */

    angular.module('tasks', ['motech-dashboard', 'tasks.controllers', 'tasks.directives',
                                    'tasks.filters', 'tasks.services', 'tasks.utils', 'ngCookies',
                                    'motech-widgets', 'uiServices']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/tasks/dashboard', {templateUrl: '../tasks/partials/tasks.html', controller: 'TasksDashboardCtrl'}).
                when('/tasks/task/new', {templateUrl: '../tasks/partials/form.html', controller: 'TasksManageCtrl'}).
                when('/tasks/task/:taskId/edit', {templateUrl: '../tasks/partials/form.html', controller: 'TasksManageCtrl'}).
                when('/tasks/task/:taskId/log', {templateUrl: '../tasks/partials/history.html', controller: 'TasksLogCtrl'}).
                when('/tasks/settings', {templateUrl: '../tasks/partials/settings.html', controller: 'TasksSettingsCtrl'});
        }]);
}());
