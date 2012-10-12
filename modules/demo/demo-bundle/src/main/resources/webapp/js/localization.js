'use strict';

/* localization service */

angular.module('localization', []).factory("i18nService", function () {

    var service = {
        ready: false,
        loading: false,

        getMessage: function (key) {
            var msg = key;

            if (this.ready == true) {
                msg = jQuery.i18n.prop(key);
            } else if (this.loading == false) {
                this.init();
            }

            return msg;
        },

        init: function () {
            this.loading = true;
            var self = this;

            jQuery.i18n.properties({
                name: 'messages',
                path: 'bundles/',
                mode: 'map',
                callback: function () {
                    self.ready = true;
                    self.loading = false;
                }
            });
        }
    };

    return service;
});
