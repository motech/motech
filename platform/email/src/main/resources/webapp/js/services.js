(function () {
    'use strict';

    /* Services */

    angular.module('sendEmailService', ['ngResource']).factory('SendEmailService', function($resource) {
        return $resource('../email/send');
    });

    angular.module('emailAuditService', ['ngResource']).factory('EmailAuditService', function($resource) {
        return $resource('../email/emails');
    });

    angular.module('settingsService', ['ngResource']).factory('SettingsService', function($resource) {
        return $resource('../email/settings');
    });
}());
