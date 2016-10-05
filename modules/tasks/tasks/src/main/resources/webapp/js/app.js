(function () {
    'use strict';

    /* App Module */

    angular.module('tasks', ['motech-dashboard', 'tasks.controllers', 'tasks.directives',
                                    'tasks.filters', 'tasks.services', 'tasks.utils', 'ngCookies',
                                    'motech-widgets', 'ui.bootstrap'])
    .config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('tasks', {
                abstract: true,
                url: '/tasks',
                views: {
                    'moduleToLoad': {
                        templateUrl: '../tasks/index.html'
                    }
                }
            })
            .state('tasks.dashboard', {
                url: '/dashboard',
                views: {
                    'tasksview': {
                        templateUrl: '../tasks/partials/tasks.html',
                        controller: 'TasksDashboardCtrl'
                    }
                }
            })
            .state('tasks.taskNew', {
                url: '/task/new',
                views: {
                    'tasksview': {
                        templateUrl: '../tasks/partials/form.html',
                        controller: 'TasksManageCtrl'
                    }
                }
            })
            .state('tasks.taskEdit', {
                url: '/task/:taskId/edit',
                views: {
                    'tasksview': {
                        templateUrl: '../tasks/partials/form.html',
                        controller: 'TasksManageCtrl'
                    }
                }
            })
            .state('tasks.taskLog', {
                url: '/task/:taskId/log',
                views: {
                    'tasksview': {
                        templateUrl: '../tasks/partials/history.html',
                        controller: 'TasksLogCtrl'
                    }
                }
            })
            .state('tasks.settings', {
                url: '/settings',
                views: {
                    'tasksview': {
                        templateUrl: '../tasks/partials/settings.html',
                        controller: 'TasksSettingsCtrl'
                    }
                }
            });
    });
}());
