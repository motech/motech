/* localization service */

var localizationModule = angular.module('localization', []);

localizationModule.factory("i18nService", function() {
    'use strict';

    var service = {
        getMessage : function(key, value) {
            return jQuery.i18n.prop(key, value);
        },

        init : function(data) {
            jQuery.i18n.map = data;
        }
    };

    return service;
});
