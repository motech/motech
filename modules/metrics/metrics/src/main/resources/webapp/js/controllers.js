(function () {

    'use strict';

    /* Controllers */

    var metricsModule = angular.module('motech-platform-metrics');

    metricsModule.controller('OperationsCtrl', function($scope, $http) {

        $http({method:'GET', url:'../metrics/settings/getGraphiteUrl'}).
            success(function (data) {
                if(data) {
                    $scope.graphiteUrl = data.concat("/composer");
                }

                // prefix with http://
                if ($scope.graphiteUrl && $scope.graphiteUrl.lastIndexOf("http://") !== 0) {
                    $scope.graphiteUrl = "http://" + $scope.graphiteUrl;
                }
            });
    });

    metricsModule.controller('SettingsCtrl', function($scope, $http) {
        $scope.metricsImplementations = [];
        $scope.selectedImplementations = [];

        $scope.statsdAgentConfig = {
            serverPort : "",
            serverHost : "",
            graphiteUrl : "",
            generateHostBasedStats : false
        };

        $http({method:'GET', url:'../metrics/settings/getAll'}).
            success(
            function(data) {
                $scope.statsdAgentConfig = data;
        });

        $scope.saveStatsdAgentConfig = function() {
            $http.post('../metrics/settings/save', $scope.statsdAgentConfig).
                success(alertHandler('metrics.settings.saved', 'metrics.success')).
                error(function(response) {
                var msg = 'metrics.error',
                responseData = (typeof(response) === 'string') ? response : response.data;
                if (typeof(responseData) === 'string') {
                            msg = responseData;
                }
                motechAlert(msg, 'metrics.error');
                });
        };

        $http({method:'GET', url:'../metrics/backend/available'}).
            success(
            function(data) {
                $scope.metricsImplementations = data;
        });

        $http({method:'GET', url:'../metrics/backend/used'}).
            success(
            function(data) {
                $scope.selectedImplementations = data;
        });

        $scope.setUsedImplementations = function() {
            $http.post('../metrics/backend/used', $scope.selectedImplementations).
                success(alertHandler('metrics.settings.saved', 'metrics.success')).
                error(function(response) {
                    motechAlert(response, 'metrics.error');
                });
        };
    });
}());

