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

}());
