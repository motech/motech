'use strict';

/* Services */

angular.module('roleService', ['ngResource']).factory('Roles', function($resource) {
    return $resource('../websecurity/api/roles');
});

angular.module('userService', ['ngResource']).factory('Users', function($resource) {
    return $resource('../websecurity/api/users');
});

angular.module('permissionService', ['ngResource']).factory('Permissions', function($resource) {
    return $resource('../websecurity/api/permissions');
});
