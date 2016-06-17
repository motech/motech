(function () {
    'use strict';

    /* App Module */

    var app = angular.module('email', ['motech-dashboard', 'ngCookies',
        'email.controllers', 'email.directives', 'email.services', 'uiServices']);

    app.config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('email', {
                url: "/email",
                abstract: true,
                views: {
                    "moduleToLoad": {
                    templateUrl: "../email/resources/index.html"
                    }
                }
            })
            .state('email.send', {
                url: '/send',
                parent: 'email',
                views: {
                    'emailView': {
                        templateUrl: '../email/resources/partials/sendEmail.html',
                        controller: 'EmailSendCtrl'
                    }
                }
            })
            .state('email.logging', {
                url: "/logging",
                parent: 'email',
                views: {
                    'emailView': {
                        controller: 'EmailLoggingCtrl',
                        templateUrl: '../email/resources/partials/emailLogging.html'
                    }
                }
            })
            .state('email.settings', {
                url: "/settings",
                parent: 'email',
                views: {
                    'emailView': {
                        controller: 'EmailSettingsCtrl',
                        templateUrl: '../email/resources/partials/settings.html'
                    }
                }
            });
    });
}());
