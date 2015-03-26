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

}());