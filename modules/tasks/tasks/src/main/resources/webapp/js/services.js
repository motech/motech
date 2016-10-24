 (function () {
    'use strict';

    /* Services */

    var services = angular.module('tasks.services', ['ngResource']);

    services.factory('Channels', function ($resource) {
        return $resource('../tasks/api/channel');
    });

    services.factory('Tasks', function ($resource) {
        return $resource('../tasks/api/task/:taskId', {taskId: '@id'});
    });

    services.factory('Activities', function ($resource) {
        return $resource('../tasks/api/activity/:taskId');
    });

    services.factory('DataSources', function ($resource) {
        return $resource('../tasks/api/datasource');
    });

    services.factory('Settings', function($resource) {
        return $resource('../tasks/api/settings');
    });

    services.factory('Triggers', function($resource) {
        return $resource('../tasks/api/channel/triggers', {}, {
            "getTrigger":  {
                url: "../tasks/api/channel/trigger",
                method: "POST"
            }
        });
    });

    services.factory('HelpStringManipulation', function($http, $compile, $templateCache, BootstrapDialogManager) {
        var dialog,
            compiledMessage;

        this.open = function ($scope) {
            $http.get('../tasks/partials/help/manipulation.html', {cache: $templateCache}).success(function (html) {
               compiledMessage = $compile(html)($scope);
               dialog = new BootstrapDialog({
                    size: 'size-wide',
                    title: jQuery.i18n.prop('task.helpManipulation'),
                    message: compiledMessage,
                    buttons: [{
                        label: jQuery.i18n.prop('task.close'),
                        cssClass: 'btn btn-default',
                        action: function (dialogItself) {
                            BootstrapDialogManager.close(dialogItself);
                        }
                    }],
                    onhide: function (dialog) {
                        BootstrapDialogManager.onhide(dialog);
                    }
                });
                BootstrapDialogManager.open(dialog);
            });
        };

        this.close = function () {
            BootstrapDialogManager.close(dialog);
        };
        return this;
    });

}());
