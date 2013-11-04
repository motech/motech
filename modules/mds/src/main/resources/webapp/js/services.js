(function () {

    'use strict';

    /**
    * Creates a entity service which will connect with the server and executes appropriate
    * methods on an entity schema.
    */
    angular.module('entityService', ['ngResource']).factory('Entities', function ($resource) {
        return $resource(
            '../mds/entities/:id',
            { id: '@id' }
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
