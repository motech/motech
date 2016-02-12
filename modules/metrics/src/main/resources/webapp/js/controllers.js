(function () {
    'use strict';

    var controllers = angular.module('metrics.controllers', []);

    controllers.controller('MetricsConfigCtrl', function ($scope, Config) {
        $scope.settings = Config.get();
        $scope.timeUnits = Config.timeUnits();

        $scope.submit = function () {
            $scope.settings.$save(function () {
                motechAlert('metrics.settings.success.saved', 'server.saved');
            }, function () {
                motechAlert('metrics.settings.error.saved', 'server.error');
            });
        }
    });

    controllers.controller('MetricsCtrl', function ($scope, $filter, $http, Metrics) {
        $scope.metrics = Metrics.query();
        $scope.ratioGauge = {};
        $scope.ratioGauge.numerator = {};
        $scope.ratioGauge.denominator = {};

        $scope.setSelectedNumerator = function () {
            $scope.selectedNumerator = $filter('filter')($scope.metrics, $scope.ratioGauge.numerator.type)[0];
        };

        $scope.setSelectedDenominator = function () {
            $scope.selectedDenominator = $filter('filter')($scope.metrics, $scope.ratioGauge.denominator.type)[0];
        };

        $scope.submit = function () {
            $http.post('../metrics/api/metrics/ratioGauge', $scope.ratioGauge);
        };
    });
}());