function CalllogController($scope, CalllogService) {


    $scope.$on('$viewContentLoaded', function () {

        $("#slider").slider({
            range:true,
            min:0,
            max:500,
            values:[0, 300],
            slide:function (event, ui) {
                $("#duration").text("" + ui.values[0] + " - " + ui.values[1] + " seconds");
            }
        });

        $("#duration").text("" + $("#slider").slider("values", 0) + " - " + $("#slider").slider("values", 1) + " seconds");
        $("#from").datetimepicker();
        $("#to").datetimepicker();
        $("#jqxexpander").jqxExpander();
    });

    $scope.search = function () {
        var min = $("#slider").slider("values",0);
        var max = $("#slider").slider("values",1);
        $scope.calllogs = CalllogService.query(
            {'phoneNumber': $scope.phoneNumber,
             'minDuration': min,
             'maxDuration': max,
             'fromDate': $("#from").val(),
             'toDate': $("#to").val()
            });
    };
    $scope.phoneNumber = "";
    $scope.calllogs = CalllogService.query();
}
