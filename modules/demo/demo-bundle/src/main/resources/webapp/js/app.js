'use strict';

/* App Module */

angular.module('motech-demo', ['TreeServices', 'localization', 'ngCookies', 'bootstrap']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/trees', {templateUrl: 'partials/trees/list.html', controller: TreeListCtrl}).
        when('/trees/create', {templateUrl: 'partials/trees/create.html', controller: TreeCreateCtrl}).
        when('/trees/:treeId/execute', {templateUrl: 'partials/trees/execute.html', controller: TreeExecuteCtrl}).
        when('/welcome', {templateUrl: 'partials/welcome.html'}).
        otherwise({redirectTo: '/welcome'});
}]);
