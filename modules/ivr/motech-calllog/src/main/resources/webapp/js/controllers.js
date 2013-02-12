function CalllogController($scope, CalllogSearch, CalllogCount) {
    $scope.phoneNumber = "";
    $scope.sortColumn = "";
    $scope.currentPage = 0;
    $scope.sortReverse= false;
    $scope.pageCount = {'count': 0 };

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

    $scope.countPages = function(){
            $scope.pageCount=CalllogCount.query(
              { 'phoneNumber': $scope.phoneNumber,
                'minDuration': $scope.min,
                'maxDuration': $scope.max,
                'fromDate': $scope.from,
                'toDate': $scope.to,
                'answered': $scope.answered,
                'busy': $scope.busy,
                'failed': $scope.failed,
                'noAnswer': $scope.noAnswer,
                'unknown': $scope.unknown,
                'page': 0,
                'sortColumn': "",
                'sortReverse' : false
              });
    };


    $scope.getCalllogs = function() {

      $scope.calllogs = CalllogSearch.query(
      {'phoneNumber': $scope.phoneNumber,
       'minDuration': $scope.min,
       'maxDuration': $scope.max,
       'fromDate': $scope.from,
       'toDate': $scope.to,
       'answered': $scope.answered,
       'busy': $scope.busy,
       'failed': $scope.failed,
       'noAnswer': $scope.noAnswer,
       'unknown': $scope.unknown,
       'page': $scope.currentPage,
       'sortColumn': $scope.sortColumn,
       'sortReverse' : $scope.sortReverse
      });
    };

    $scope.sort = function(column){
        if($scope.sortColumn == column){
            $scope.sortReverse = !$scope.sortReverse;
        }else{
            $scope.sortReverse = false;
        }
        $scope.sortColumn = column;
        $scope.getCalllogs();
    }

    $scope.search = function () {
        $scope.currentPage = 0;
        $scope.sortColumn = "";
        $scope.sortReverse=false;

        $scope.min = $("#slider").slider("values",0);
        $scope.max = $("#slider").slider("values",1);
        $scope.from = $("#from").val();
        $scope.to = $("#to").val();
        $scope.answered = $("#answered").is(':checked');
        $scope.busy = $("#busy").is(':checked');
        $scope.failed = $('#failed').is(':checked');
        $scope.noAnswer = $('#noAnswer').is(':checked');
        $scope.unknown = $('#unknown').is(':checked');

        $scope.getCalllogs();
        $scope.countPages();
    };

    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
            $scope.getCalllogs();
        }
    };

    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.pageCount.count - 1) {
            $scope.currentPage++;
            $scope.getCalllogs();
        }
    };

    $scope.setPage = function () {
        $scope.currentPage = this.selectedPage;
        $scope.getCalllogs();
    };

    $scope.range = function (start, end) {
        var ret = [];
        if (!end) {
            end = start;
            start = 0;
        }
        for (var i = start; i < end; i++) {
            ret.push(i);
        }
        return ret;
    };
}
