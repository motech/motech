(function () {
    'use strict';

    /* App Module */

    angular.module('commcare', ['motech-dashboard', 'settingsServices', 'permissionsServices', 'modulesServices', 'connectionService', 'ngCookies', 'bootstrap']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/settings', {templateUrl: '../commcare/resources/partials/settings.html', controller: 'SettingsCtrl' }).
                when('/forms', {templateUrl: '../commcare/resources/partials/forms.html', controller: 'ModulesCtrl' }).
                when('/cases', {templateUrl: '../commcare/resources/partials/cases.html', controller: 'CaseSchemasCtrl' }).
                otherwise({redirectTo: '/settings'});
    }]);
}());
