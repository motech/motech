'use strict';

function NewRulesController($scope, AggregationRules, i18nService, $routeParams, $http) {

    if ($routeParams.ruleName != undefined) {

        $scope.editMode = true;

        $scope.rule = AggregationRules.find({
            ruleName: $routeParams.ruleName
        }, function success(response) {
            $scope.scheduleType = $scope.rule.aggregationSchedule.scheduleType.split('_')[0];
            $scope.scheduleTypePartial = '../event-aggregation/resources/partials/new_' + $scope.scheduleType + '_schedule.html';
            $("#tagsinput").val($scope.rule.fields);
        });

    } else {
        $scope.scheduleType = 'periodic';
        if ($routeParams.scheduleType === 'cron' || $routeParams.scheduleType === 'custom') {
            $scope.scheduleType = $routeParams.scheduleType;
        }
        if ($scope.scheduleType == 'periodic') {
            $scope.rule = {
                "aggregationSchedule": {
                    "scheduleType": $scope.scheduleType + "_request",
                    "startTimeInMillis": moment().valueOf().toString()
                },
                "state": "running"
            }
        } else {
            $scope.rule = {
                "aggregationSchedule": {
                    "scheduleType": $scope.scheduleType + "_request"
                },
                "state": "running"
            }
        }
        $scope.scheduleTypePartial = '../event-aggregation/resources/partials/new_' + $scope.scheduleType + '_schedule.html';
    }

    $scope.errors = [];
    $scope.isSuccess = false;

    $scope.update = function() {
        $scope.rule.fields = $('#tagsinput').val().split(',');

        AggregationRules.update($scope.rule,
            function success(response) {
                $scope.isSuccess = true;
                $scope.errors = [];
            },
            function error(response) {
                $scope.errors = response.data;
            });
    }
}

function RulesController($scope, AggregationRules, i18nService, $routeParams, $http) {

    $scope.allRules = AggregationRules.query();
}

function AggregationsController($scope, AggregationRules, Aggregations, i18nService, $routeParams, $http) {

    $scope.eventStatus = $routeParams.eventStatus;

    $scope.rule = AggregationRules.find({
        ruleName: $routeParams.ruleName
    });

    $scope.aggregations = Aggregations.find({
        ruleName: $routeParams.ruleName,
        eventStatus: $routeParams.eventStatus
    });

}
