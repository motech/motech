(function () {

    'use strict';

    var services = angular.module('data-services.services', ['ngResource']);

    /**
    * Creates the entity service that will connect to the server and execute appropriate
    * methods on an entity schema.
    */
    services.factory('Entities', function ($resource) {
        return $resource(
            '../mds/entities/:id/:action/:param/:params',
            { id: '@id' },
            {
                getAdvanced: { method: 'GET', params: { action: 'advanced' } },
                getAdvancedCommited: { method: 'GET', params: { action: 'advancedCommited' } },
                getSecurity: { method: 'GET', params: { action: 'security' } },
                getWorkInProggress: { method: 'GET', params: { action: 'wip' }, isArray: true },
                getFields: { method: 'GET', params: {action: 'fields' }, isArray: true },
                getField: { method: 'GET', params: {action: 'fields'} },
                getEntityFields: { method: 'GET', params: {action: 'entityFields'}, isArray: true },
                getDisplayFields: { method: 'GET', params: {action: 'displayFields'}, isArray: true },
                getEntity: { method: 'GET', params: {action: 'getEntity'} },
                draft: { method: 'POST', params: {action: 'draft' } },
                abandon: { method: 'POST', params: {action: 'abandon' } },
                commit: { method: 'POST', params: {action: 'commit' } },
                update: { method: 'POST', params: {action: 'update' } }
            }
        );
    });

    services.factory('Instances', function ($resource) {
        return $resource(
            '../mds/instances/:id/:action/:param',
            { id: '@id' },
            {
                newInstance: { method: 'GET', params: { action: 'new' }},
                selectInstance: { method: 'GET', params: { action: 'instance' }},
                deleteInstance: { method: 'DELETE', params: { action: 'delete' }},
                deleteAllInstances: { method: 'DELETE', params: { action: 'deleteAll' }},
                deleteSelectedInstances: { method: 'DELETE', params: { action: 'deleteSelected' }},
                getTrash: { method : 'GET', params : { action: 'trash' }},
                revertInstanceFromTrash: { method: 'GET', params: {action: 'revertFromTrash'}}
            }
        );
    });

    services.factory('History', function ($resource) {
            return $resource(
                '../mds/instances/:entityId/:instanceId/:action/:param',
                {
                    entityId: '@entityId',
                    instanceId: '@instanceId'
                },
                {
                    getHistory: { method: 'GET', params: { action: 'history' } },
                    getPreviousVersion: { method: 'GET', params: { action: 'previousVersion' } },
                    revertPreviousVersion: { method: 'GET', params: { action: 'revert' } }
                }
            );
        });

    services.factory('MdsSettings', function ($resource) {
        return $resource(
            '../mds/settings/:action/', {},
            {
                saveSettings: { method: 'POST', params: {action: 'saveSettings' } },
                getSettings: { method: 'GET', params: { action: 'get' } }
            }
        );
    });

    services.factory('Locale', function ($resource) {
        return $resource('../server/lang/available');
    });

    services.factory('FileUpload', function($http) {
        return {
            upload: function(file, url, onSuccess, onError) {
                var formData = new FormData();
                formData.append('file', file);
                $http.post(url, formData, {
                    transformRequest: angular.identity,
                    headers: {'Content-Type': undefined}
                })
                .success(onSuccess || function() {})
                .error(onError || function() {});
            }
        };
    });

    services.factory("MDSUsers", function($resource) {
        return $resource('../mds/users');
    });

}());
