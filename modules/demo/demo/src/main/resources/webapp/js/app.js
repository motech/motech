'use strict';

/* App Module */

angular.module('motech-demo', ['motech-dashboard', 'TreeServices', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/trees', {templateUrl: '../demo/partials/trees/list.html', controller: TreeListCtrl}).
            when('/trees/create', {templateUrl: '../demo/partials/trees/create.html', controller: TreeCreateCtrl}).
            when('/trees/:treeId/execute', {templateUrl: '../demo/partials/trees/execute.html', controller: TreeExecuteCtrl}).
            when('/welcome', {templateUrl: '../demo/partials/welcome.html'}).
            when('/event', {templateUrl: '../demo/partials/event.html', controller: EventCtrl}).
            when('/ivrservicejsp', {templateUrl: '../demo/api/ivrcalls', controller: IVRCallCtrl}).
            otherwise({redirectTo: '/welcome'});
}]);
