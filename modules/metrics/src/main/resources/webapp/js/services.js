(function () {
    'use strict';

    var services = angular.module('metrics.services', ['ngResource']);

    services.factory('Config', function ($resource) {
        return $resource('../metrics/api/config', null, {
            timeUnits: {
                method: 'GET',
                url: '../metrics/api/config/timeUnits'
            }
        });
    });

    services.factory('Metrics', function ($resource) {
        return $resource('../metrics/api/metrics/:metricType', {metricType: '@type'}, {
           getRatioGauge: {
               method: 'GET',
               url: '../metrics/api/metrics/ratioGauge/new'
           },
           getMetricTypes: {
               method: 'GET',
               url: '../metrics/api/metrics/metricTypes'
           },
           getRatioGaugeValues: {
               method: 'GET',
               url: '../metrics/api/metrics/ratioGaugeValues'
           }
        });
    });
}());