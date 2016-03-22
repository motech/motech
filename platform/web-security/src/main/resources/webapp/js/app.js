(function () {
    'use strict';

    /* App Module */

    var webSecurityModule = angular.module('webSecurity', [ 'motech-dashboard',
        'webSecurity.controllers', 'webSecurity.services', 'webSecurity.directives',
        'webSecurity.filters', 'ngCookies']);

    webSecurityModule.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('webSecurity', {
                url: "/webSecurity",
                abstract: true,
                views: {
                    "moduleToLoad": {
                        templateUrl: "../websecurity/index.html"
                    }
                }
            })
            .state('webSecurity.users', {
                url: '/users',
                parent: 'webSecurity',
                views: {
                    'websecurityview': {
                        templateUrl: '../websecurity/partials/user.html',
                        controller: 'WebSecurityUserCtrl'
                    }
                }
            })
            .state('webSecurity.roles', {
                url: '/roles',
                parent: 'webSecurity',
                views: {
                    'websecurityview': {
                        templateUrl: '../websecurity/partials/role.html',
                        controller: 'WebSecurityRolePermissionCtrl'
                    }
                }
            })
            .state('webSecurity.permissions', {
                url: '/permissions',
                parent: 'webSecurity',
                views: {
                    'websecurityview': {
                        templateUrl: '../websecurity/partials/permission.html',
                        controller: 'WebSecurityRolePermissionCtrl'
                    }
                }
            })
            .state('webSecurity.profile', {
                url: '/profile',
                parent: 'webSecurity',
                views: {
                    'websecurityview': {
                        templateUrl: '../websecurity/partials/profile.html',
                        controller: 'WebSecurityProfileCtrl'
                    }
                }
            })
            .state('webSecurity.dynamicURL', {
                url: '/dynamicURL',
                parent: 'webSecurity',
                views: {
                    'websecurityview': {
                        templateUrl: '../websecurity/partials/dynamic.html',
                        controller: 'WebSecurityDynamicCtrl'
                    }
                }
            });

        /*$routeProvider.
            when('/webSecurity/users', {templateUrl: '../websecurity/partials/user.html', controller: 'WebSecurityUserCtrl'}).
            when('/webSecurity/roles', {templateUrl: '../websecurity/partials/role.html', controller: 'WebSecurityRolePermissionCtrl'}).
            when('/webSecurity/permissions', {templateUrl: '../websecurity/partials/permission.html', controller: 'WebSecurityRolePermissionCtrl'}).
            when('/webSecurity/profile', {templateUrl: '../websecurity/partials/profile.html', controller: 'WebSecurityProfileCtrl'}).
            when('/webSecurity/profile/:username', {templateUrl: '../websecurity/partials/profile.html', controller: 'WebSecurityProfileCtrl'}).
            when('/webSecurity/dynamicURL', {templateUrl: '../websecurity/partials/dynamic.html', controller: 'WebSecurityDynamicCtrl'});*/
    }]);

}());

