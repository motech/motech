(function () {
    'use strict';

    /* App Module */

    angular.module('testJdoDiscriminatorHelloWorld', ['motech-dashboard', 'testJdoDiscriminatorHelloWorld.controllers', 'testJdoDiscriminatorHelloWorld.directives', 'testJdoDiscriminatorHelloWorld.services', 'ngCookies'])
        .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/helloWorld', {templateUrl: '../testJdoDiscriminator/resources/partials/say-hello.html', controller: 'HelloWorldController'});
    }]);
}());
