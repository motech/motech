#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
(function () {
    'use strict';

    /* Services */

    var services = angular.module('helloWorld.services', ['ngResource']);

    services.factory('HelloWorld', function($resource) {
        return $resource('../${artifactId}/sayHello');
    });
}());
