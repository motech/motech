(function () {
    'use strict';

    /* App Module */

    var serverModule = angular.module('motech-dashboard', ['localization', 'ngCookies', 'ui',
        'motech-widgets', 'browserDetect', 'uiServices', 'loadOnDemand', 'ngRoute']);

    serverModule.config(['$httpProvider', function($httpProvider) {
        var interceptor = ['$q', function($q, $location) {
            function success(response) {
                if ((response.headers !== undefined && response.headers('login-required') === "true") || response.status === 408) {
                    response.status = 408;
                    window.location.replace(window.location.pathname);
                    $location.path(window.location.pathname);
                    $location.replace(window.location.pathname);
                }
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

        $httpProvider.interceptors.push(interceptor);
    }]);

    serverModule.config(['$loadOnDemandProvider', function ($loadOnDemandProvider) {
        $.ajax({
            url: '../server/module/config',
            success:  function (data, status, headers, config,timeout) {
                if (headers.getResponseHeader('login-required') !== 'true' ) {
                    $loadOnDemandProvider.config(data);
                }
            }
        });
    }]);
}());
