'use strict';

/* App Module */

angular.module('motech-admin', ['bundleServices', 'messageServices', 'localization']).
    config(['$routeProvider', function($routeProvider) {
      $routeProvider.
          when('/bundles', {templateUrl: 'partials/bundles.html', controller: BundleListCtrl}).
          otherwise({redirectTo: '/bundles'});
}]);
