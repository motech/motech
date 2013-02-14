angular.module('CalllogSearchService', ['ngResource']).factory('CalllogSearch', function ($resource) {
    return $resource('../callLog/search');
});

angular.module('CalllogCountService', ['ngResource']).factory('CalllogCount', function ($resource) {
    return $resource('../callLog/count', {}, { query:{method:'GET', isArray:false}});
});

angular.module('CalllogMaxDurationService', ['ngResource']).factory('CalllogMaxDuration', function ($resource) {
    return $resource('../callLog/maxduration', {}, { query:{method:'GET', isArray:false}});
});

angular.module('CalllogPhoneNumberService', ['ngResource']).factory('CalllogPhoneNumber', function($resource) {
        return $resource('../callLog/phone-numbers',{}, { query: {method: 'GET', isArray: true}});
});
