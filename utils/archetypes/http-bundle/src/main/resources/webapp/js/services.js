(function () {

    'use strict';

    angular.module('helloWorldService', ['ngResource']).factory('HelloWorld', function ($resource) {
        return $resource('../helloWorld/hello/);
    });

}());
