'use strict';

/* Services */

angular.module('patientService', ['ngResource']).factory('Patient', function ($resource) {
    return $resource('../mrs/api/patients/:motechId', {motechId:'@motechId'}, {
        update: { method: 'PUT' },
    });
});