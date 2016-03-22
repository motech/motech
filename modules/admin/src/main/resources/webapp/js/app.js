(function () {
    'use strict';

    /* App Module */

    var app = angular.module('admin', ['motech-dashboard', 'admin.filters', 'admin.controllers',
        'admin.directives', 'admin.services', 'ngCookies']);

    app.config(['$stateProvider', function ($stateProvider, $urlRouterProvider) {
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
                   'adminview': {
                       templateUrl: '../admin/partials/bundles.html',
                       controller: 'AdminBundleListCtrl'
                   }
               }
            })
            .state('admin.messages', {
               url: '/messages',
               parent: 'admin',
               views: {
                   'adminview': {
                       templateUrl: '../admin/partials/messages.html',
                       controller: 'AdminStatusMsgCtrl'
                   }
               }
            })
            .state('admin.platform-settings', {
               url: '/platform-settings',
               parent: 'admin',
               views: {
                   'adminview': {
                       templateUrl: '../admin/partials/settings.html',
                       controller: 'AdminSettingsCtrl'
                   }
               }
            })
            .state('admin.bundleId', {
               url: '/bundle/:bundleId',
               parent: 'admin',
               views: {
                   'adminview': {
                       templateUrl: '../admin/partials/bundle.html',
                       controller: 'AdminModuleCtrl'
                   }
               }
            })
            .state('admin.bundleSettings', {
               url: '/bundleSettings/:bundleId',
               parent: 'admin',
               views: {
                   'adminview': {
                       templateUrl: '../admin/partials/bundleSettings.html',
                       controller: 'AdminBundleSettingsCtrl'
                   }
               }
            })
            .state('admin.log', {
               url: '/log',
               parent: 'admin',
               views: {
                   'adminview': {
                       templateUrl: '../admin/partials/log.html',
                       controller: 'AdminServerLogCtrl'
                   }
               }
            })
            .state('admin.logOptions', {
               url: '/log/logOptions',
               parent: 'admin',
               views: {
                   'adminview': {
                       templateUrl: '../admin/partials/logOptions.html',
                       controller: 'AdminServerLogOptionsCtrl'
                   }
               }
            })
            .state('admin.topics', {
               url: '/topics',
               parent: 'admin',
               views: {
                   'adminview': {
                       templateUrl: '../admin/partials/topic_stats.html',
                       controller: 'AdminTopicStatsCtrl'
                   }
               }
            })
            .state('admin.queues', {
               url: '/queues',
               parent: 'admin',
               views: {
                   'adminview': {
                       templateUrl: '../admin/partials/queue_stats.html',
                       controller: 'AdminQueueStatsCtrl'
                   }
               }
            })
            .state('admin.browse', {
               url: '/queues/browse',
               parent: 'admin',
               views: {
                   'adminview': {
                       templateUrl: '../admin/partials/queue_message_stats.html',
                       controller: 'AdminQueueMessageStatsCtrl'
                   }
               }
            })
            .state('admin.messagesSettings', {
               url: '/messagesSettings',
               parent: 'admin',
               views: {
                   'adminview': {
                       templateUrl: '../admin/partials/notificationRules.html',
                       controller: 'AdminNotificationRuleCtrl'
                   }
               }
            })
            ;
    }]);
      /*.config(['$routeProvider', function($routeProvider) {
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
    }]);*/
}());
