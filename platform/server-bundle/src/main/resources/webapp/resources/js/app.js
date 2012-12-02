'use strict';

/* App Module */

angular.module('motech-dashboard', ['localization', 'ngCookies']).config(['$httpProvider', function($httpProvider) {
    var interceptor = ['$rootScope','$q', function(scope, $q) {
        function success(response) {
            return response;
        }

        function error(response) {
            var status = response.status;
            if (status == 403) {
                window.location = "./accessdenied";
            }
            return $q.reject(response);
        }

        return function(promise) {
            return promise.then(success, error);
        }

        }];
        $httpProvider.responseInterceptors.push(interceptor);
}]);


