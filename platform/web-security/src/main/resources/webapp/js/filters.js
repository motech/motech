(function () {
    'use strict';

    var webSecurityModule = angular.module('motech-web-security');

    webSecurityModule.filter('filterPagination', function() {
        return function (input, start) {
            return input.slice(+start);
        };
    });

    webSecurityModule.filter('repeat', function () {
        return function(input, total) {
            var i, t = parseInt(total, 10);

            for (i = 0; i < t; i += 1) {
                input.push(i);
            }

            return input;
        };
    });

}());
