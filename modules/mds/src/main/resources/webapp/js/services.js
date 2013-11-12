(function () {

    'use strict';

    /**
    * Creates the entity service that will connect to the server and execute appropriate
    * methods on an entity schema.
    */
    angular.module('entityService', ['ngResource']).factory('Entities', function ($resource) {
        return $resource(
            '../mds/entities/:id/:action/:param',
            { id: '@id' },
            {
                getAdvanced: { method: 'GET', params: { action: 'advanced' } },
                getWorkInProggress: { method: 'GET', params: { action: 'wip' }, isArray: true },
                getFields: { method: 'GET', params: {action: 'fields' }, isArray: true },
                getField: { method: 'GET', params: {action: 'fields'} },
                draft: { method: 'POST', params: {action: 'draft' } },
                abandon: { method: 'POST', params: {action: 'abandon' } },
                commit: { method: 'POST', params: {action: 'commit' } }
            }
        );
    });

    angular.module('mdsSettingsService', ['ngResource']).factory('MdsSettings', function ($resource) {
        return $resource(
            '../mds/settings/:action/', {},
            {
                importFile: { method: 'POST', params: {action: 'importFile' } },
                exportData: { method: 'POST', params: {action: 'exportData' } },
                saveSettings: { method: 'POST', params: {action: 'saveSettings' } },
                getSettings: { method: 'GET', params: { action: 'get' } }
            }
        );
    });
}());
