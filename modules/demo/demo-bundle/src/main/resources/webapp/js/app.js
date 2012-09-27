'use strict';

/* App Module */

angular.module('motech-demo', ['motech-dashboard', 'TreeServices', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/trees', {templateUrl: 'module/demo/partials/trees/list.html', controller: TreeListCtrl}).
            when('/trees/create', {templateUrl: 'module/demo/partials/trees/create.html', controller: TreeCreateCtrl}).
            when('/trees/:treeId/execute', {templateUrl: 'module/demo/partials/trees/execute.html', controller: TreeExecuteCtrl}).
            when('/welcome', {templateUrl: 'module/demo/partials/welcome.html'}).
            when('/ivrservice', {templateUrl: 'module/demo/partials/ivrcalls.html', controller: IVRCallCtrl}).
            otherwise({redirectTo: '/welcome'});
}]);
