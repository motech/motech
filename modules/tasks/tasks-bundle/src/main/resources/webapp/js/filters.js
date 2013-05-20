(function () {
    'use strict';

    /* Filters */

    var widgetModule = angular.module('motech-tasks');

    widgetModule.filter('fromNow', function () {
        return function (date) {
            return moment(date).fromNow();
        };
    });

    widgetModule.filter('idLessThan', function () {
            return function (dataSources, id) {
                var array = [];

                angular.forEach(dataSources, function (source) {
                    if (source.id < id) {
                        array.push(source);
                    }
                });

                return array;
            };
        });

}());