(function () {
    'use strict';

    angular.module('uiServices', ['ngResource']).factory('Menu', function($resource) {
        return $resource('modulemenu');
    });

}());
