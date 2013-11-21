(function () {

    'use strict';

    /**
    * Creates a entity service which will connect with the server and executes appropriate
    * methods on an entity schema.
    */
    angular.module('entityService', ['ngResource']).factory('Entities', function ($resource) {
        return $resource(
            '../mds/entities/:id/:action',
            { id: '@id' },
            {
                getAdvanced: { method: 'GET', params: { action: 'advanced' } },
                saveAdvanced: { method: 'POST', params: {action: 'advanced' } }
            }
        );
    });

    /**
    * Creates a field service which will connect with the server and executes appropriate methods
    * on a field definition.
    */
    angular.module('fieldService', ['ngResource']).factory('Fields', function ($resource) {
        return $resource(
            '../mds/entities/:entityId/fields/:id',
            { entityId: '@entityId', id: '@id' }
        );
    });

    /**
    * Creates a fieldValidation service which will contain methods related to field validation in general
    */
    angular.module('fieldValidationService', ['ngResource']).factory('FieldsValidation', function ($resource) {
        return $resource(
            '../mds/fields/validation/:action/:type',
            { type: '@type' },
            {
                getForType: { method: 'GET', params: { action: 'get' } }
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
