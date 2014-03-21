(function () {
    'use strict';

    /* App Module */

    var webSecurityModule = angular.module('webSecurity', [ 'motech-dashboard',
        'webSecurity.controllers', 'webSecurity.services', 'webSecurity.directives',
        'webSecurity.filters', 'ngCookies', 'ngRoute'
    ]);

    webSecurityModule.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/webSecurity/users', {templateUrl: '../websecurity/partials/user.html', controller: 'UserCtrl'}).
            when('/webSecurity/roles', {templateUrl: '../websecurity/partials/role.html', controller: 'RolePermissionCtrl'}).
            when('/webSecurity/permissions', {templateUrl: '../websecurity/partials/permission.html', controller: 'RolePermissionCtrl'}).
            when('/webSecurity/profile', {templateUrl: '../websecurity/partials/profile.html', controller: 'ProfileCtrl'}).
            when('/webSecurity/profile/:username', {templateUrl: '../websecurity/partials/profile.html', controller: 'ProfileCtrl'}).
            when('/webSecurity/dynamicURL', {templateUrl: '../websecurity/partials/dynamic.html', controller: 'DynamicCtrl'});
    }]);

}());

