(function () {
    'use strict';

    /* App Module */

    var webSecurityModule = angular.module('motech-web-security', [
        'motech-dashboard', 'roleService', 'userService', 'permissionService', 'dynamicService',
        'ngCookies', 'ngRoute', 'bootstrap'
    ]);

    webSecurityModule.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/users', {templateUrl: '../websecurity/partials/user.html', controller: 'UserCtrl'}).
            when('/roles', {templateUrl: '../websecurity/partials/role.html', controller: 'RolePermissionCtrl'}).
            when('/permissions', {templateUrl: '../websecurity/partials/permission.html', controller: 'RolePermissionCtrl'}).
            when('/profile', {templateUrl: '../websecurity/partials/profile.html', controller: 'ProfileCtrl'}).
            when('/profile/:username', {templateUrl: '../websecurity/partials/profile.html', controller: 'ProfileCtrl'}).
            when('/dynamicURL', {templateUrl: '../websecurity/partials/dynamic.html', controller: 'DynamicCtrl'}).
            otherwise({redirectTo: '/welcome'});
    }]);

}());

