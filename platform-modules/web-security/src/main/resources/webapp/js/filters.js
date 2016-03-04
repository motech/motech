(function () {
    'use strict';

    var filters = angular.module('webSecurity.filters', []);

    filters.filter('filterPagination', function() {
        return function (input, start) {
            return input.slice(+start);
        };
    });

    filters.filter('repeat', function () {
        return function(input, total) {
            var i, t = parseInt(total, 10);

            for (i = 0; i < t; i += 1) {
                input.push(i);
            }

            return input;
        };
    });

}());
