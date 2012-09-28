
'use strict';

/* Services */

angular.module('TreeServices', ['ngResource']).factory('Tree', function ($resource) {
    return $resource('../demo/api/trees/:treeId/:action', { treeId: '@_id' }, {
        remove: { method: 'POST', params: { action: 'remove' } }
    });
});