'use strict';

/* App Module */

angular.module('motech-commcare', ['motech-dashboard', 'settingsServices', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/settings', {templateUrl: '../commcare/partials/settings.html', controller: SettingsCtrl}).
//            when('/dataMapping', {templateUrl: '../commcare/partials/dataMapping.html', controller: DataMappingCtrl}).
            otherwise({redirectTo: '/settings'});
}]);
