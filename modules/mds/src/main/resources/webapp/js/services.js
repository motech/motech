(function () {

    'use strict';

    angular.module('entityService', ['ngResource']).factory('Entities', function ($resource) {
        return $resource('../mds/entities/:id', {id: '@id'});
    });

}());
