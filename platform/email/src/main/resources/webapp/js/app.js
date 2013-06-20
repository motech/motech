(function () {
    'use strict';

    /* App Module */

    angular.module('motech-email', ['motech-dashboard', 'ngCookies', 'bootstrap', 'sendEmailService', 'settingsService']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/send', {templateUrl: '../email/resources/partials/sendEmail.html', controller: 'SendEmailController'}).
                when('/settings', {templateUrl: '../email/resources/partials/settings.html', controller: 'SettingsController'}).
                otherwise({redirectTo: '/send'});
    }]);
}());
