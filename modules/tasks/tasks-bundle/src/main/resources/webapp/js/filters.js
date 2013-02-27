(function () {
    'use strict';

    /* Filters */

    var widgetModule = angular.module('motech-tasks');

    widgetModule.filter('filterPagination', function () {
        return function (input, start) {
            start = +start;
            return input.slice(start);
        };
    });

    widgetModule.filter('fromNow', function () {
        return function (date) {
            return moment(date).fromNow();
        };
    });

    widgetModule.filter('repeat', function () {
        return function (input, obj) {
            var i, current = obj.current, total = obj.total;

            if (current > 0) {
                for (i = current - 2; i < current && i < +total; i += 1) {
                    if (i >= 0) {
                        input.push(i);
                    }
                }
            }

            if (current < total) {
                for (i = current; i < current + 3 && i < +total; i += 1) {
                    input.push(i);
                }
            }

            return input;
        };
    });

}());