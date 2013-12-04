#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
(function () {
    'use strict';

    /* Services */

    angular.module('helloWorldService', ['ngResource']).factory('HelloWorld', function($resource) {
        return $resource('../${artifactId}/sayHello');
    });
}());
