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


angular.module('CalllogSearchService', ['ngResource']).factory('CalllogSearch', function ($resource) {
    return $resource('../ivr/api/calllog/search');
});

angular.module('CalllogCountService', ['ngResource']).factory('CalllogCount', function ($resource) {
    return $resource('../ivr/api/calllog/count', {}, { query:{method:'GET', isArray:false}});
});

angular.module('CalllogMaxDurationService', ['ngResource']).factory('CalllogMaxDuration', function ($resource) {
    return $resource('../ivr/api/calllog/maxduration', {}, { query:{method:'GET', isArray:false}});
});

angular.module('CalllogPhoneNumberService', ['ngResource']).factory('CalllogPhoneNumber', function($resource) {
    return $resource('../ivr/api/calllog/phone-numbers',{}, { query: {method: 'GET', isArray: true}});
});

