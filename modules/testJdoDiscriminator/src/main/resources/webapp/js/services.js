(function () {
    'use strict';

    /* Services */

    var services = angular.module('testJdoDiscriminatorHelloWorld.services', ['ngResource']);

    services.factory('HelloWorld', function($resource) {
        return $resource('../testJdoDiscriminator/sayHello');
    });
}());
