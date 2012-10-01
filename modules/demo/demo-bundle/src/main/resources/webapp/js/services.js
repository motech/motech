
'use strict';

/* Services */

angular.module('TreeServices', ['ngResource']).factory('Tree', function ($resource) {
    return $resource('module/demo/api/trees/:treeId/:action', { treeId: '@id' }, {
        remove: { method: 'POST', params: { action: 'remove' } }
    });
});