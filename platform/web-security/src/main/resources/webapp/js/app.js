(function () {
    'use strict';

    /* App Module */

    var webSecurityModule = angular.module('webSecurity', [ 'motech-dashboard',
        'webSecurity.controllers', 'webSecurity.services', 'webSecurity.directives',
        'webSecurity.filters', 'ngCookies', 'uiServices']);

    webSecurityModule.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/webSecurity/users', {templateUrl: '../websecurity/partials/user.html', controller: 'WebSecurityUserCtrl'}).
            when('/webSecurity/roles', {templateUrl: '../websecurity/partials/role.html', controller: 'WebSecurityRolePermissionCtrl'}).
            when('/webSecurity/permissions', {templateUrl: '../websecurity/partials/permission.html', controller: 'WebSecurityRolePermissionCtrl'}).
            when('/webSecurity/profile', {templateUrl: '../websecurity/partials/profile.html', controller: 'WebSecurityProfileCtrl'}).
            when('/webSecurity/profile/:username', {templateUrl: '../websecurity/partials/profile.html', controller: 'WebSecurityProfileCtrl'}).
            when('/webSecurity/dynamicURL', {templateUrl: '../websecurity/partials/dynamic.html', controller: 'WebSecurityDynamicCtrl'});
    }]);

}());

