(function () {
    'use strict';

    /* App Module */

    angular.module('motech-tasks', ['motech-dashboard', 'channelServices', 'taskServices', 'activityServices',
                                    'manageTaskUtils', 'dataSourceServices', 'ngCookies', 'bootstrap',
                                    'motech-widgets']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/dashboard', {templateUrl: '../tasks/partials/tasks.html', controller: 'DashboardCtrl'}).
                when('/task/new', {templateUrl: '../tasks/partials/form.html', controller: 'ManageTaskCtrl'}).
                when('/task/:taskId/edit', {templateUrl: '../tasks/partials/form.html', controller: 'ManageTaskCtrl'}).
                when('/task/:taskId/log', {templateUrl: '../tasks/partials/history.html', controller: 'LogCtrl'}).
                otherwise({redirectTo: '/dashboard'});
        }]);
}());
