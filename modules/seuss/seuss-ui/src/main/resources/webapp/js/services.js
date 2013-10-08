(function () {

    'use strict';

    angular.module('entityService', ['ngResource']).factory('Entities', function ($resource) {
        return $resource('../seuss/entities/:id', {id: '@id'});
    });

}());
