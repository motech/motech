(function () {
    'use strict';

    /* App Module */

    var serverModule = angular.module('motech-dashboard', ['localization', 'ngCookies', 'ui', 'motech-widgets',
        'browserDetect', 'uiServices']).config(['$httpProvider', function($httpProvider) {
        var interceptor = ['$rootScope','$q', function(scope, $q) {
            function success(response) {
                return response;
            }

            function error(response) {
                if (response.status === 403) {
                    window.location = "./accessdenied";
                }

                return $q.reject(response);
            }

            return function(promise) {
                return promise.then(success, error);
            };

            }];
            $httpProvider.responseInterceptors.push(interceptor);
    }]);
}());

