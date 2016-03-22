(function () {
    'use strict';

    /* App Module */

    var serverModule = angular.module('motech-dashboard', ['localization', 'ngCookies', 'ui',
        'motech-widgets', 'browserDetect', 'uiServices', 'ui.router', 'oc.lazyLoad']);

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
        $urlRouterProvider.otherwise("/");

        $stateProvider
            .state('/', {
            url: "/", // root route
                views: {
                    "moduleToLoad": {
                        controller: 'MotechMasterCtrl',
                        template: "<h3>Index page</h3> "
                    }
                }
            })
            .state('home', {
                url: "/home",
                views: {
                    "moduleToLoad": {
                        controller: 'MotechHomeCtrl'
                    }
                }
            });

            // Without server side support html5 must be disabled.
            $locationProvider.html5Mode(false);
        });

    serverModule.config(['$ocLazyLoadProvider', function ($ocLazyLoadProvider) {
        $.ajax({
            url: '../server/module/config',
            success:  function (data, status, headers, config,timeout) {
                if (headers.getResponseHeader('login-required') !== 'true' ) {
                    var modules = [];
                    angular.forEach(data, function(value, key) {
                        if (value.template !== null) {
                            modules.push({'name': value.name, 'template': value.template, 'files':[value.script, value.css]});
                            //console.info(value.name);
                        }
                    });
                    angular.forEach(modules, function(module, key) {
                        angular.forEach(data, function(value, key) {
                            //if (value.name && value.name !== undefined) {
                                var indexOfName = value.name.indexOf('.');
                                if (indexOfName > 0 && value.name.substring(0, indexOfName) === module.name) {
                                    module.files.push(value.script);
                                }
                           // }
                        });
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


/*[
{"name":"tasks.controllers","script":"../tasks/js/controllers.js","template":null,"css":null},
{"name":"tasks.utils","script":"../tasks/js/util.js","template":null,"css":null},
{"name":"tasks.directives","script":"../tasks/js/directives.js","template":null,"css":null},
{"name":"tasks.services","script":"../tasks/js/services.js","template":null,"css":null},
{"name":"tasks.filters","script":"../tasks/js/filters.js","template":null,"css":null},
{"name":"tasks","script":"../tasks/js/app.js","template":"../tasks/index.html","css":"../tasks/css/tasks.css"},
{"name":"email.services","script":"../email/resources/js/services.js","template":null,"css":null},
{"name":"email.directives","script":"../email/resources/js/directives.js","template":null,"css":null},
{"name":"email.controllers","script":"../email/resources/js/controllers.js","template":null,"css":null},
{"name":"email","script":"../email/resources/js/app.js","template":"../email/resources/index.html","css":"../email/resources/css/email.css"},
{"name":"data-services.utils","script":"../mds/resources/js/util.js","template":null,"css":null},
{"name":"data-services.controllers","script":"../mds/resources/js/controllers.js","template":null,"css":null},
{"name":"data-services.directives","script":"../mds/resources/js/directives.js","template":null,"css":null},
{"name":"data-services.services","script":"../mds/resources/js/services.js","template":null,"css":null},
{"name":"data-services","script":"../mds/resources/js/app.js","template":"../mds/resources/index.html","css":"../mds/resources/css/mds.css"},
{"name":"admin.services","script":"../admin/js/services.js","template":null,"css":null},
{"name":"admin.directives","script":"../admin/js/directives.js","template":null,"css":null},
{"name":"admin.controllers","script":"../admin/js/controllers.js","template":null,"css":null},
{"name":"admin.filters","script":"../admin/js/filters.js","template":null,"css":null},
{"name":"admin","script":"../admin/js/app.js","template":"../admin/index.html","css":"../admin/css/admin.css"},
{"name":"webSecurity.directives","script":"../websecurity/js/directives.js","template":null,"css":null},
{"name":"webSecurity.services","script":"../websecurity/js/services.js","template":null,"css":null},
{"name":"webSecurity.controllers","script":"../websecurity/js/controllers.js","template":null,"css":null},
{"name":"webSecurity.filters","script":"../websecurity/js/filters.js","template":null,"css":null},
{"name":"webSecurity","script":"../websecurity/js/app.js","template":"../websecurity/index.html","css":"../websecurity/css/websecurity.css"},
{"name":"scheduler.services","script":"../scheduler/js/services.js","template":null,"css":null},
{"name":"scheduler.directives","script":"../scheduler/js/directives.js","template":null,"css":null},
{"name":"scheduler.controllers","script":"../scheduler/js/controllers.js","template":null,"css":null},
{"name":"scheduler","script":"../scheduler/js/app.js","template":"../scheduler/index.html","css":"../scheduler/css/scheduler.css"},
{"name":"uiServices","script":"../server/resources/js/services.js","template":null,"css":null},
{"name":"rest-docs","script":"../server/resources/rest-docs/rest-docs-app.js","template":"../server/resources/partials/rest-docs-index.html","css":null}
]*/

