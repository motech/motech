'use strict';

/* Services */

angular.module('bundleServices', ['ngResource']).factory('Bundle', function($resource) {
    return $resource('api/bundles/:bundleId/:action', {bundleId:'@bundleId'}, {
        start: {method:'POST', params: {action: 'start'}},
        stop: {method:'POST', params: {action: 'stop'}},
        restart: {method:'POST', params: {action: 'restart'}},
        uninstall: {method: 'POST', params: {action: 'uninstall'}}
    });
});

angular.module('messageServices', ['ngResource']).factory('StatusMessage', function($resource) {
    return $resource('api/messages');
});