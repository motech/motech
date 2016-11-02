(function () {
    'use strict';

    /* App Module */

    var serverModule = angular.module('motech-dashboard', ['localization', 'ngCookies', 'ui',
        'motech-widgets', 'browserDetect', 'uiServices', 'ui.router', 'oc.lazyLoad', 'textAngular']);

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

    serverModule.config(function($stateProvider, $locationProvider, $urlRouterProvider, $ocLazyLoadProvider) {
        $urlRouterProvider.otherwise("/home" );

        $stateProvider
            .state('#', {
                url: "/", // root route
                views: {
                    "moduleToLoad": {
                        controller: 'MotechMasterCtrl',
                        templateUrl: '../server/resources/partials/main.html'
                    }
                }
            })
            .state('home', {
                url: "/home",
                views: {
                    "moduleToLoad": {
                        controller: 'MotechHomeCtrl',
                        templateUrl: '../server/resources/partials/main.html'
                    }
                }
            })
            .state('homepage', {
                url: "/homepage",
                views: {
                    "homeview": {
                        controller: 'MotechHomepageCtrl',
                        templateUrl: '../server/resources/partials/homepage.html'
                    }
                }
            });

            // Without server side support html5 must be disabled.
            $locationProvider.html5Mode(false);
    });

    serverModule.config(['$ocLazyLoadProvider', function ($ocLazyLoadProvider) {
        $.ajax({
            url: '../server/module/config',
            success:  function (data, status, headers, config, timeout) {
                if (headers.getResponseHeader('login-required') !== 'true' ) {
                    var modules = [];

                    angular.forEach(data, function(value, key) {
                        if (value.template !== null) {
                            modules.push({'name': value.name, series: true, 'template': value.template, 'files':[value.script, value.css]});
                        } else {
                            modules.push({'name': value.name, 'files':[value.script]});
                        }
                    });
                    angular.forEach(modules, function(module, key) {
                        if (module.name && module.name.indexOf('.') < 0) {
                            angular.forEach(data, function(value, key) {
                                var indexOfName = value.name.indexOf('.');
                                if (indexOfName > 0 && value.name.substring(0, indexOfName) === module.name) {
                                    module.files.push(value.script);
                                }
                            });
                        }
                    });
                    $ocLazyLoadProvider.config({
                        events: true,
                        debug: true,
                        modules: modules
                    });
                }
            }
        });
    }]);
}());

