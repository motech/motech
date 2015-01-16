(function () {
    'use strict';

    /* App Module */

    var app = angular.module('email', ['motech-dashboard', 'ngCookies',
        'email.controllers', 'email.directives', 'email.services', 'ngSanitize', 'textAngular']);

    app.config(function ($routeProvider) {
        $routeProvider.when('/email/send', {
                templateUrl: '../email/resources/partials/sendEmail.html',
                controller: 'EmailSendCtrl'
            }).when('/email/logging', {
                templateUrl: '../email/resources/partials/emailLogging.html',
                controller: 'EmailLoggingCtrl'
            }).when('/email/settings', {
                templateUrl: '../email/resources/partials/settings.html',
                controller: 'EmailSettingsCtrl'
            });
    });
}());
