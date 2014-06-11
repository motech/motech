(function () {

    'use strict';

    /* Controllers */

    var controllers = angular.module('metrics.controllers', []);

    controllers.controller('OperationsCtrl', function($scope, $http) {

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

    controllers.controller('SettingsCtrl', function($scope, $http) {
        $scope.metricsImplementations = [];
        $scope.selectedImplementations = [];

        $scope.statsdAgentConfig = {
            serverPort : "",
            serverHost : "",
            graphiteUrl : "",
            generateHostBasedStats : false
        };

        $http({method:'GET', url:'../metrics/backend/available'}).
            success(
            function(data) {
                $scope.metricsImplementations = data;
                $scope.getAllSettings();
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

        $scope.getAllSettings = function() {
            $scope.metricsSettings = {};
            angular.forEach($scope.metricsImplementations, function (impl) {
                $http({method:'GET', url:'../metrics/backend/' + impl + '/settings'}).
                    success(
                        function(data) {
                            if(!jQuery.isEmptyObject(data)) {
                                $scope.metricsSettings[impl] = data;
                            }
                         }
                    );
            });
        };

        $scope.saveSettings = function(impl, settings) {
            $http.post('../metrics/backend/' + impl + '/settings/', settings).
                success(alertHandler('metrics.settings.saved', 'metrics.success')).
                error(function(response) {
                    jAlert(response, $scope.msg('metrics.error'));
                });
        };

    });
}());

