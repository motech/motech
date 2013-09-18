(function () {

    'use strict';

    angular.module('objectService', ['ngResource']).factory('Objects', function ($resource) {
        return $resource('../seuss/objects/:id', {id: '@id'});
    });

}());
