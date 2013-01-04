'use strict';

angular.module('AggregationRuleServices', ['ngResource'])

    .factory('AggregationRules', function ($resource) {
        return $resource('../event-aggregation/rules/:ruleName', {}, {
            all: {
                method:'GET',
                params:{},
                isArray:true
            },
            find: {
                method:'GET',
                params:{
                    ruleName: 'ruleName'
                },
                isArray:false
            },
            update: {
                method:'PUT'
            }
        });
    })

    .factory('Aggregations', function ($resource) {
        return $resource('../event-aggregation/aggregations/:ruleName/:eventStatus', {}, {
            find: {
                method:'GET',
                params:{
                    ruleName: 'ruleName',
                    eventStatus: 'eventStatus'
                },
                isArray:true
            }
        });
    });
