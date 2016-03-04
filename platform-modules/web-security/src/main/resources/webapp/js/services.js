(function () {
    'use strict';

    /* Services */

    var services = angular.module('webSecurity.services', ['ngResource']);

    services.factory('Roles', function($resource) {
        return $resource('../websecurity/api/web-api/roles');
    });

    services.factory('Users', function($resource) {
        return $resource('../websecurity/api/users');
    });

    services.factory('Permissions', function($resource) {
        return $resource('../websecurity/api/web-api/permissions/:permissionName', {permissionName:'@permissionName'});
    });

    services.factory('Dynamic', function($resource) {
        return $resource('../websecurity/api/web-api/securityRules');
    });

}());
