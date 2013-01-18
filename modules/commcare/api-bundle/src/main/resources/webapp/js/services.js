'use strict';

/* Services */

angular.module('settingsServices', ['ngResource']).factory('Settings', function($resource) {
    return $resource('../commcare/settings');
});