(function () {
    'use strict';

    /* Services */

    var services = angular.module('batch.services', ['ngResource']);

    services.factory('HelloWorld', function($resource) {
        return $resource('../batch/batch/sayHello');
    });
}());
