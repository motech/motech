angular.module('CalllogSearchService', ['ngResource']).factory('CalllogSearch', function($resource) {
        return $resource('../callLog/search');
});

angular.module('CalllogCountService', ['ngResource']).factory('CalllogCount', function($resource) {
        return $resource('../callLog/count',{}, { query: {method: 'GET', isArray: false}});
});
