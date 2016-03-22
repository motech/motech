(function () {
    'use strict';

    /* App Module */

    var app = angular.module('admin', ['motech-dashboard', 'admin.filters', 'admin.controllers',
        'admin.directives', 'admin.services', 'ngCookies', 'uiServices']);

    app.config(['$routeProvider', function($routeProvider) {
          $routeProvider.
              when('/admin/bundles', {templateUrl: '../admin/partials/bundles.html', controller: 'AdminBundleListCtrl'}).
              when('/admin/messages', {templateUrl: '../admin/partials/messages.html', controller: 'AdminStatusMsgCtrl'}).
              when('/admin/platform-settings', {templateUrl: '../admin/partials/settings.html', controller: 'AdminSettingsCtrl'}).
              when('/admin/bundle/:bundleId', {templateUrl: '../admin/partials/bundle.html', controller: 'AdminModuleCtrl'}).
              when('/admin/bundleSettings/:bundleId', {templateUrl: '../admin/partials/bundleSettings.html', controller: 'AdminBundleSettingsCtrl'}).
              when('/admin/log', {templateUrl: '../admin/partials/log.html', controller: 'AdminServerLogCtrl'}).
              when('/admin/topics', {templateUrl: '../admin/partials/topic_stats.html', controller: 'AdminTopicStatsCtrl'}).
              when('/admin/queues', {templateUrl: '../admin/partials/queue_stats.html', controller: 'AdminQueueStatsCtrl'}).
              when('/admin/queues/browse', {templateUrl: '../admin/partials/queue_message_stats.html', controller: 'AdminQueueMessageStatsCtrl'}).
              when('/admin/logOptions', {templateUrl: '../admin/partials/logOptions.html', controller: 'AdminServerLogOptionsCtrl'}).
              when('/admin/messagesSettings', {templateUrl: '../admin/partials/notificationRules.html', controller: 'AdminNotificationRuleCtrl'});
    }]);
}());
