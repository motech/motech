(function () {
    'use strict';

    /* App Module */

    var app = angular.module('admin', ['motech-dashboard', 'admin.filters', 'admin.controllers',
        'admin.directives', 'admin.services', 'ngCookies', 'ngRoute']);

    app.config(['$routeProvider', function($routeProvider) {
          $routeProvider.
              when('/admin/bundles', {templateUrl: '../admin/partials/bundles.html', controller: 'BundleListCtrl'}).
              when('/admin/messages', {templateUrl: '../admin/partials/messages.html', controller: 'StatusMsgCtrl'}).
              when('/admin/platform-settings', {templateUrl: '../admin/partials/settings.html', controller: 'SettingsCtrl'}).
              when('/admin/bundle/:bundleId', {templateUrl: '../admin/partials/bundle.html', controller: 'ModuleCtrl'}).
              when('/admin/bundleSettings/:bundleId', {templateUrl: '../admin/partials/bundleSettings.html', controller: 'BundleSettingsCtrl'}).
              when('/admin/log', {templateUrl: '../admin/partials/log.html', controller: 'ServerLogCtrl'}).
              when('/admin/queues', {templateUrl: '../admin/partials/queue_stats.html', controller: 'QueueStatisticsCtrl'}).
              when('/admin/queues/browse', {templateUrl: '../admin/partials/queue_message_stats.html', controller: 'MessageStatisticsCtrl'}).
              when('/admin/logOptions', {templateUrl: '../admin/partials/logOptions.html', controller: 'ServerLogOptionsCtrl'}).
              when('/admin/messagesSettings', {templateUrl: '../admin/partials/notificationRules.html', controller: 'NotificationRuleCtrl'});
    }]);
}());
