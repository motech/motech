(function () {
    'use strict';

    /* App Module */

    var serverModule = angular.module('motech-dashboard', ['localization', 'ngCookies', 'ui',
        'motech-widgets', 'browserDetect', 'uiServices', 'loadOnDemand']);

    serverModule.config(['$httpProvider', function($httpProvider) {
        var interceptor = ['$q', function($q) {
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

    serverModule.config(['$loadOnDemandProvider', function ($loadOnDemandProvider) {
        $.ajax({
            url: '../server/module/config',
            success:  function (data) {
                $loadOnDemandProvider.config(data);
            }
        });
    }]);
}());

