angular.module('CalllogServices', ['ngResource'])
    .factory('CalllogService', function($resource) {
        return $resource('../callLog/search');
});
