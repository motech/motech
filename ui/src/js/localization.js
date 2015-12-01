/* localization service */

var localizationModule = angular.module('localization', []);

localizationModule.factory("i18nService", function() {
    'use strict';

    var service = {
        getMessage : function(args) {
            return jQuery.i18n.prop.apply(null, $.map(args, function (arg) {
                return arg;
            }));
        },

        init : function(data) {
            jQuery.i18n.map = data;
        }
    };

    return service;
});
