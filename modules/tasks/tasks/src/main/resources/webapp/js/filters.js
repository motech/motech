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
                var array = [];

                angular.forEach(dataSources, function (source) {
                    if (source.objectId < id) {
                        array.push(source);
                    }
                });

                return array;
            };
        });

    filters.filter('orderLessThan', function () {
                return function (dataSources, order) {
                    var array = [];

                    angular.forEach(dataSources, function (source) {
                        if (source.order < order) {
                            array.push(source);
                        }
                    });

                    return array;
                };
            });

}());
