'use strict';

function TestCallController($scope, Provider, Call) {

    $scope.providers = Provider.all();

    $scope.makeCall = function() {
        $scope.dialed = undefined;
        Call.dial($scope.call,
            function success() {
                $scope.dialed = true;
            },
            function failure() {
                $scope.dialed = false;
            }
        );
    }
}
