(function () {
    'use strict';

    /* Services */

    var services = angular.module('email.services', ['ngResource']);

    services.factory('SendEmailService', function($resource) {
        return $resource('../email/send');
    });

    services.factory('EmailAuditService', function($resource) {
        return $resource('../email/emails');
    });

    services.factory('SettingsService', function($resource) {
        return $resource('../email/settings');
    });

}());
