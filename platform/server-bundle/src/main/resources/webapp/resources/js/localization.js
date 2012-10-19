'use strict';

/* localization service */

var localizationModule = angular.module('localization', []);

localizationModule.factory("i18nService", function() {

    var service = {
        ready : false,
        loading : false,
        name : '',
        path : '',

        getMessage : function(key) {
            var msg = '';

            if (this.ready == true) {
                msg = jQuery.i18n.prop(key);
            }

            return msg;
        },

        init : function(lang, name, path, handler) {
            this.ready = false;
            this.loading = true;
            this.name = name;
            this.path = path;

            var self = this;

            jQuery.i18n.properties({
                name: name,
                path: path,
                mode:'map',
                language: lang || null,
                callback: function() {
                    self.ready = true;
                    self.loading = false;
                    if (handler) {
                        handler();
                    }
                }
            });
        },

    }

    return service;
});
