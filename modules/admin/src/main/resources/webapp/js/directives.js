(function () {
    'use strict';

    /* Directives */

    var directives = angular.module('admin.directives', []);

    directives.directive('sidebar', function () {
       return function (scope, element, attrs) {
           $(element).sidebar({
               position:"right"
           });
       };
    });

    directives.directive('clearForm', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $(element).on('hidden', function () {
                    $('#' + attrs.clearForm).clearForm();
                });
            }
        };
    });

    directives.directive('messagesDatePickerFrom', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element),
                    endDateTextBox = angular.element('#dateTimeTo');

                elem.datetimepicker({
                    dateFormat: "yy-mm-dd",
                    changeMonth: true,
                    changeYear: true,
                    timeFormat: "HH:mm:ss",
                    onSelect: function (selectedDateTime){
                        endDateTextBox.datetimepicker('option', 'minDate', elem.datetimepicker('getDate') );
                        scope.setDateTimeFilter(selectedDateTime, null);
                    },
                    onChangeMonthYear: function (year, month, inst) {
                        var curDate = $(this).datepicker("getDate");
                        if (curDate === null) {
                            return;
                        }
                        if (curDate.getFullYear() !== year || curDate.getMonth() !== month - 1) {
                            curDate.setYear(year);
                            curDate.setMonth(month - 1);
                            $(this).datepicker("setDate", curDate);
                            scope.setDateTimeFilter(curDate, null);
                        }
                    },
                    onClose: function () {
                        var viewValue = $(this).val();
                        if (viewValue === '') {
                            endDateTextBox.datetimepicker('option', 'minDate', null);
                        } else {
                            endDateTextBox.datepicker('option', 'minDate', elem.datepicker('getDate'));
                        }
                        scope.setDateTimeFilter(viewValue, null);
                    }
                });
            }
        };
    });

    directives.directive('messagesDatePickerTo', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element),
                    startDateTextBox = angular.element('#dateTimeFrom');

                elem.datetimepicker({
                    dateFormat: "yy-mm-dd",
                    changeMonth: true,
                    changeYear: true,
                    timeFormat: "HH:mm:ss",
                    onSelect: function (selectedDateTime){
                        startDateTextBox.datetimepicker('option', 'maxDate', elem.datetimepicker('getDate') );
                        scope.setDateTimeFilter(null, selectedDateTime);
                    },
                    onChangeMonthYear: function (year, month, inst) {
                        var curDate = $(this).datepicker("getDate");
                        if (curDate === null) {
                            return;
                        }
                        if (curDate.getFullYear() !== year || curDate.getMonth() !== month - 1) {
                            curDate.setYear(year);
                            curDate.setMonth(month - 1);
                            $(this).datepicker("setDate", curDate);
                            scope.setDateTimeFilter(null, curDate);
                        }
                    },
                    onClose: function () {
                        var viewValue = $(this).val();
                        if (viewValue === '') {
                            startDateTextBox.datetimepicker('option', 'maxDate', null);
                        } else {
                            startDateTextBox.datepicker('option', 'maxDate', elem.datepicker('getDate'));
                        }
                        scope.setDateTimeFilter(null, viewValue);
                    }
                });
            }
        };
    });

    directives.directive('setLevelFilter', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elm = angular.element(element), attribute = attrs;
                elm.click(function (e) {
                    if (elm.children().hasClass("fa-check-square-o")) {
                        $(this).children().removeClass('fa-check-square-o').addClass('fa-square-o');
                        $(this).removeClass('active');
                    }
                    else {
                        elm.children().addClass('fa-check-square-o').removeClass('fa-square-o');
                        elm.addClass('active');
                    }
                });
            }
        };
    });

    directives.directive('filereader', function () {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.bind('change', function(e){
                   scope.$apply(function(){
                       scope[attrs.filereader](e.target.files[0]);
                   });
                });
            }
        };
    });

}());