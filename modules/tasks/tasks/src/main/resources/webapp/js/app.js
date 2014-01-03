(function () {
    'use strict';

    /* App Module */

    angular.module('motech-tasks', ['motech-dashboard', 'channelServices', 'taskServices', 'activityServices',
                                    'manageTaskUtils', 'dataSourceServices', 'settingsServices', 'ngCookies', 'ngRoute',
                                    'motech-widgets']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/dashboard', {templateUrl: '../tasks/partials/tasks.html', controller: 'DashboardCtrl'}).
                when('/task/new', {templateUrl: '../tasks/partials/form.html', controller: 'ManageTaskCtrl'}).
                when('/task/:taskId/edit', {templateUrl: '../tasks/partials/form.html', controller: 'ManageTaskCtrl'}).
                when('/task/:taskId/log', {templateUrl: '../tasks/partials/history.html', controller: 'LogCtrl'}).
                when('/settings', {templateUrl: '../tasks/partials/settings.html', controller: 'SettingsCtrl'}).
                otherwise({redirectTo: '/dashboard'});
        }]);
}());
