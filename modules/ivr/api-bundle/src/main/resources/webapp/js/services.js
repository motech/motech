'use strict';

angular.module('TestCallServices', ['ngResource'])

    .factory('Provider', function($resource) {
        return $resource('../ivr/api/providers', {}, {
            all: {
                method: 'GET',
                params: {},
                isArray: true
            }
        })
    })

    .factory('Call', function($resource) {
        return $resource('../ivr/api/test-call', {}, {
            dial: {
                method: 'POST'
            }
        });
    });
