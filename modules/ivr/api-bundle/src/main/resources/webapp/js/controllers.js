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

function CalllogController($scope, CalllogSearch, CalllogCount, CalllogPhoneNumber, CalllogMaxDuration, $http) {
    $scope.phoneNumber = "";
    $scope.sortColumn = "";
    $scope.currentPage = 0;
    $scope.sortReverse = false;
    $scope.pageCount = {'count':0 };
    var pagesWindowSize = 10;


    $scope.countPages = function () {
        $scope.pageCount = CalllogCount.query(
            { 'phoneNumber':$scope.phoneNumber,
                'minDuration':$scope.min,
                'maxDuration':$scope.max,
                'fromDate':$scope.from,
                'toDate':$scope.to,
                'answered':$scope.answered,
                'busy':$scope.busy,
                'failed':$scope.failed,
                'noAnswer':$scope.noAnswer,
                'unknown':$scope.unknown,
                'page':0,
                'sortColumn':"",
                'sortReverse':false
            });
    };

    $scope.getCalllogs = function () {

        $scope.calllogs = CalllogSearch.query(
            {'phoneNumber':$scope.phoneNumber,
                'minDuration':$scope.min,
                'maxDuration':$scope.max,
                'fromDate':$scope.from,
                'toDate':$scope.to,
                'answered':$scope.answered,
                'busy':$scope.busy,
                'failed':$scope.failed,
                'noAnswer':$scope.noAnswer,
                'unknown':$scope.unknown,
                'page':$scope.currentPage,
                'sortColumn':$scope.sortColumn,
                'sortReverse':$scope.sortReverse
            });
    };

    $scope.sort = function (column) {
        if ($scope.sortColumn == column) {
            $scope.sortReverse = !$scope.sortReverse;
        } else {
            $scope.sortReverse = false;
        }
        $scope.sortColumn = column;

        $('th img').each(function(){
            $(this).removeClass().addClass('sorting-no');
        });

        if ($scope.sortReverse)
            $('th.'+$scope.sortColumn+' img').removeClass('sorting-no').addClass('sorting-desc');
        else
            $('th.'+$scope.sortColumn+' img').removeClass('sorting-no').addClass('sorting-asc');

        $scope.getCalllogs();
    }

    $scope.search = function () {
        $scope.currentPage = 0;
        $scope.sortColumn = "";
        $scope.sortReverse = false;

        $scope.phoneNumber = $('#phoneNumber').val();
        $scope.min = $("#slider").slider("values", 0);
        $scope.max = $("#slider").slider("values", 1);
        $scope.from = $("#from").val();
        $scope.to = $("#to").val();
        $scope.answered = $("#answered").is('button.active');
        $scope.busy = $("#busy").is('button.active');
        $scope.failed = $('#failed').is('button.active');
        $scope.noAnswer = $('#noAnswer').is('button.active');
        $scope.unknown = $('#unknown').is('button.active');

        $scope.getCalllogs();
        $scope.countPages();

        setFilterTitle();
    };

    var setFilterTitle = function() {
        var filters = [];
        if ($scope.phoneNumber != '') {
            filters.push("Phone number " + $scope.phoneNumber);
        }
        if ($scope.from != '') {
            filters.push("Start date " + $scope.from);
        }
        if ($scope.to != '') {
            filters.push("End date " + $scope.to);
        }
        var statuses = [];
        $.each(["answered", "busy", "failed", "noAnswer", "unknown"], function(i, s) {
            if (eval("$scope." + s) === true) {
                statuses.push(s);
            }
        });
        if ($scope.min > 0 || $scope.max < $scope.maxDuration) {
            filters.push("Call Duration " + $scope.min + " - " + $scope.max + " seconds");
        }
        if (statuses.length > 0) {
            filters.push("Disposition " + toCsv(statuses, " | ", " | "));
        }
        var s;
        if (filters.length > 0) {
            s = "<b>Filtered by</b> " + toCsv(filters, ", ", " and ");
        } else {
            s = "Filter by";
        }
        $('#filter-title').html(s);
    };

    var toCsv = function(list, separator, terminalSeparator) {
        var s = "";
        $.each(list, function(i, item) {
            s += item;
            if (i < list.length - 2) {
                s += separator;
            } else if (list.length > 1 && i == list.length - 2) {
                s += terminalSeparator;
            }
        });
        return s;
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
        if ($scope.currentPage + pagesWindowSize <= $scope.pageCount.count) {
            start = $scope.currentPage - pagesWindowSize / 2;
        } else {
            start = $scope.pageCount.count - pagesWindowSize;
        }
        if (start < 0) {
            start = 0;
        }
        end = start + pagesWindowSize;
        if (end > $scope.pageCount.count) {
            end = $scope.pageCount.count;
        }
        for (var i = start; i < end; i++) {
            ret.push(i);
        }
        return ret;
    };

    $scope.isEmpty = function (obj) {
        return angular.equals({}, obj);
    };

    $scope.setActive = function(active){
        if($('#'+active+'').is('button.active') === true) {
            $scope.active = false;
            $('#'+active+'').find('i').removeClass("icon-ok").addClass('icon-ban-circle');
        } else {
            $scope.active = true;
            $('#'+active+'').find('i').removeClass("icon-ban-circle").addClass('icon-ok');
        }
    }
}

