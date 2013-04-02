(function () {
    'use strict';

    /* Services */

    angular.module('resourceServices', ['ngResource']).factory('Resources', function ($resource) {
        return $resource('../cmsliteapi/resource/:type/:language/:name');
    });

}());