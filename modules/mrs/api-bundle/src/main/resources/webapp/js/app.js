'use strict';

/* App Module */

angular.module('motech-mrs', ['motech-dashboard', 'patientService', 'ngCookies', 'bootstrap', 'motech-widgets']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/patients', {templateUrl: '../mrs/resources/partials/patients.html', controller: PatientMrsCtrl}).
            when('/patients/:motechId', {templateUrl: '../mrs/resources/partials/patients.html', controller: PatientMrsCtrl}).
            when('/settings', {templateUrl: '../mrs/resources/partials/settings.html', controller: SettingsMrsCtrl}).
            when('/mrs/new', {templateUrl: '../mrs/resources/partials/form.html', controller: ManagePatientMrsCtrl}).
            when('/mrs/:motechId/edit', {templateUrl: '../mrs/resources/partials/form.html', controller: ManagePatientMrsCtrl}).
            when('/mrs/:motechId/editAttributes', {templateUrl: '../mrs/resources/partials/attributes.html', controller: ManagePatientMrsCtrl}).
            otherwise({redirectTo: '/patients'});
    }
]);