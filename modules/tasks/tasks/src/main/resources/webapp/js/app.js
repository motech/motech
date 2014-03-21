(function () {
    'use strict';

    /* App Module */

    angular.module('tasks', ['motech-dashboard', 'tasks.controllers', 'tasks.directives',
                                    'tasks.filters', 'tasks.services', 'tasks.utils', 'ngCookies',
                                    'ngRoute', 'motech-widgets']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/tasks/dashboard', {templateUrl: '../tasks/partials/tasks.html', controller: 'DashboardCtrl'}).
                when('/tasks/task/new', {templateUrl: '../tasks/partials/form.html', controller: 'ManageTaskCtrl'}).
                when('/tasks/task/:taskId/edit', {templateUrl: '../tasks/partials/form.html', controller: 'ManageTaskCtrl'}).
                when('/tasks/task/:taskId/log', {templateUrl: '../tasks/partials/history.html', controller: 'LogCtrl'}).
                when('/tasks/settings', {templateUrl: '../tasks/partials/settings.html', controller: 'SettingsCtrl'});
        }]);
}());
