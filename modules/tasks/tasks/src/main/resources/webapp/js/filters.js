(function () {
    'use strict';

    /* Filters */

    var filters = angular.module('tasks.filters', []);

    filters.filter('filterPagination', function() {
        return function (input, start) {
            return input.slice(+start);
        };
    });

    filters.filter('fromNow', function () {
        return function (date) {
            return moment(date).fromNow();
        };
    });

    filters.filter('idLessThan', function () {
        return function (dataSources, id) {
            var filtered = [];

            angular.forEach(dataSources, function (source) {
                if (source.objectId === undefined || source.objectId < id) {
                    filtered.push(source);
                }
            });

            return filtered;
        };
    });

    filters.filter('orderLessThan', function () {
        return function (dataSources, order) {
            var filtered = [];

                angular.forEach(dataSources, function (source) {
                    if (source.order < order) {
                        filtered.push(source);
                    }
                });

            return filtered;
        };
    });

    filters.filter('postActionField', function (ManageTaskUtils) {
        return function (fields, id) {
            var filtered = [];

            angular.forEach(fields, function (field) {
                if (field.prefix !== ManageTaskUtils.POST_ACTION_PREFIX || (field.prefix === ManageTaskUtils.POST_ACTION_PREFIX && field.objectId < id)) {
                    filtered.push(field);
                }
            });

            return filtered;
        };
    });

}());
