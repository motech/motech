'use strict';

/* App Module */

angular.module('commcare', ['motech-dashboard', 'settingsServices', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/settings', {templateUrl: '../commcare/resources/partials/settings.html', controller: SettingsCtrl}).
            //when('/dataMapping', {templateUrl: '../commcare/resources/partials/dataMapping.html', controller: DataMappingCtrl}).
            otherwise({redirectTo: '/settings'});
}]);
