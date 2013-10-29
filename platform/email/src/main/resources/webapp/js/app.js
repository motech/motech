(function () {
    'use strict';

    /* App Module */

    angular.module('motech-email', ['motech-dashboard', 'ngCookies', 'ngRoute', 'bootstrap', 'sendEmailService', 'settingsService', 'emailAuditService']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/send', {templateUrl: '../email/resources/partials/sendEmail.html', controller: 'SendEmailController'}).
                when('/logging', {templateUrl: '../email/resources/partials/emailLogging.html', controller: 'EmailLoggingController'}).
                when('/settings', {templateUrl: '../email/resources/partials/settings.html', controller: 'SettingsController'}).
                otherwise({redirectTo: '/send'});
    }]);
}());
