(function () {
    'use strict';

    /* Services */

    angular.module('helloWorldService', ['ngResource']).factory('HelloWorld', function($resource) {
        return $resource('../${artifactId}/sayHello');
    });
}());
