(function () {
    'use strict';

    /* App Module */

    var app = angular.module('admin', ['motech-dashboard', 'admin.filters', 'admin.controllers',
        'admin.directives', 'admin.services', 'ngCookies', 'uiServices']);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('admin', {
               url: "/admin",
               abstract: true,
               views: {
                   "moduleToLoad": {
                       templateUrl: "../admin/index.html"
                   }
               }
            })
            .state('admin.bundles', {
               url: '/bundles',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/bundles.html',
                       controller: 'AdminBundleListCtrl'
                   }
               }
            })
            .state('admin.messages', {
               url: '/messages',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/messages.html',
                       controller: 'AdminStatusMsgCtrl'
                   }
               }
            })
            .state('admin.platform-settings', {
               url: '/platform-settings',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/settings.html',
                       controller: 'AdminSettingsCtrl'
                   }
               }
            })
            .state('admin.bundle', {
               url: '/bundle/:bundleId',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/bundle.html',
                       controller: 'AdminModuleCtrl'
                   }
               }
            })
            .state('admin.bundleSettings', {
               url: '/bundleSettings/:bundleId',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/bundleSettings.html',
                       controller: 'AdminBundleSettingsCtrl'
                   }
               }
            })
            .state('admin.log', {
               url: '/log',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/log.html',
                       controller: 'AdminServerLogCtrl'
                   }
               }
            })
            .state('admin.logOptions', {
               url: '/logOptions',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/logOptions.html',
                       controller: 'AdminServerLogOptionsCtrl'
                   }
               }
            })
            .state('admin.topics', {
               url: '/topics',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/topic_stats.html',
                       controller: 'AdminTopicStatsCtrl'
                   }
               }
            })
            .state('admin.queues', {
               url: '/queues',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/queue_stats.html',
                       controller: 'AdminQueueStatsCtrl'
                   }
               }
            })
            .state('admin.browse', {
               url: '/browse?queueName',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/queue_message_stats.html',
                       controller: 'AdminQueueMessageStatsCtrl'
                   }
               }
            })
            .state('admin.messagesSettings', {
               url: '/messagesSettings',
               parent: 'admin',
               views: {
                   'adminView': {
                       templateUrl: '../admin/partials/notificationRules.html',
                       controller: 'AdminNotificationRuleCtrl'
                   }
               }
            });
    }]);
}());
