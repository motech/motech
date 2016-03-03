(function () {
    'use strict';

    var filters = angular.module('admin.filters', []);

    filters.filter('moduleName', function () {
        return function (input) {
            return input.replace(/(motech\s|\sapi|\sbundle)/ig, '');
        };
    });

}());
