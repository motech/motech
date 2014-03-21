(function () {
    'use strict';

    /* Services */

    var services = angular.module('admin.services', ['ngResource']);

    services.factory('Bundle', function($resource) {
        return $resource('../admin/api/bundles/:bundleId/:action', {bundleId:'@bundleId'}, {
            start: {method:'POST', params: {action: 'start'}},
            stop: {method:'POST', params: {action: 'stop'}},
            restart: {method:'POST', params: {action: 'restart'}},
            uninstall: {method: 'POST', params: {action: 'uninstall'}},
            uninstallWithConfig: {method: 'POST', params: {action: 'uninstallconfig'}},
            details: {method: 'GET', params: {action: 'detail'}}
        });
    });

    services.factory('StatusMessage', function($resource) {
        return $resource('../admin/api/messages');
    });

    services.factory('NotificationRule', function($resource) {
        return $resource('../admin/api/messages/rules/:ruleId', {ruleId: '@_id'});
    });

    services.factory('NotificationRuleDto', function($resource) {
        return $resource('../admin/api/messages/rules/dto');
    });

    services.factory('PlatformSettings', function($resource) {
        return $resource('../admin/api/settings/platform');
    });

    services.factory('ModuleSettings', function($resource) {
        return $resource('../admin/api/settings/:bundleId');
    });

    services.factory('LogService', function($resource) {
        return $resource('../admin/api/log/level');
    });

}());
