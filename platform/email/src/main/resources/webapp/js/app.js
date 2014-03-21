(function () {
    'use strict';

    /* App Module */

    var app = angular.module('email', ['motech-dashboard', 'ngCookies', 'ngRoute',
        'email.controllers', 'email.directives', 'email.services']);

    app.config(function ($routeProvider) {
        $routeProvider.when('/email/send', {
                templateUrl: '../email/resources/partials/sendEmail.html',
                controller: 'SendEmailController'
            }).when('/email/logging', {
                templateUrl: '../email/resources/partials/emailLogging.html',
                controller: 'EmailLoggingController'
            }).when('/email/settings', {
                templateUrl: '../email/resources/partials/settings.html',
                controller: 'SettingsController'
            });
    });
}());
