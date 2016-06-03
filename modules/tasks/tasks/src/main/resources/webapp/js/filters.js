(function () {
    'use strict';

    /* Filters */

    var filters = angular.module('tasks.filters', []);

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

    filters.filter('postActionField', function () {
        return function (fields, selectedAction, id) {
            var filtered = [];

            angular.forEach(fields, function (field) {
                if (field.prefix !== 'pa' || (field.prefix === 'pa' && field.objectId < id)) {
                    filtered.push(field);
                }
            });

            return filtered;
        };
    });

}());
