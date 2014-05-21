(function () {
    'use strict';

    /* Services */

    var services = angular.module('hub.services', ['ngResource']);

    services.factory('HelloWorld', function($resource) {
        return $resource('../hub/hub/sayHello');
    });
}());
