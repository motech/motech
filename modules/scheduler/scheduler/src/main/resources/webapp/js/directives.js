(function () {
    'use strict';

    /* Directives */

    var directives = angular.module('scheduler.directives', []);

    /* General date time picker that can work as either date, time or datetime picker */
    directives.directive('dateTimePicker', function ($timeout) {
        return {
            restrict: 'A',
            require: 'ngModel',
            scope: {
                dateTimePicker: '@',
                timeFormat: '@'
            },
            link: function (scope, element, attr, ngModel) {
                var isReadOnly = scope.$eval(attr.ngReadonly),
                    dateFormat = "",
                    timeFormat = "",
                    timeOnly = false,
                    dateOnly = false,
                    showTimezone;

                if (scope.dateTimePicker === 'date') {
                    dateOnly = true;
                    dateFormat = 'yy-mm-dd';

                } else if (scope.dateTimePicker === 'time') {
                    timeOnly = true;

                    if (scope.timeFormat) {
                        timeFormat = scope.timeFormat;
                    } else {
                        timeFormat = 'HH:mm z';
                    }

                    showTimezone = timeFormat.indexOf("z") > -1;
                } else {
                    dateFormat = 'yy-mm-dd';
                    timeFormat = 'HH:mm z';
                }

                if(!isReadOnly) {
                    angular.element(element).datetimepicker({
                        showTimezone: showTimezone,
                        changeYear: true,
                        useLocalTimezone: true,
                        dateFormat: dateFormat,
                        timeFormat: timeFormat,
                        showTimepicker: !dateOnly,
                        timeOnly: timeOnly,
                        onSelect: function (dateTex) {
                            $timeout(function () {
                                ngModel.$setViewValue(dateTex);
                            });
                        },
                        onClose: function (year, month, inst) {
                            var viewValue = $(this).val();
                            $timeout(function () {
                                ngModel.$setViewValue(viewValue);
                            });
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
                            }
                        }
                    });
                }
            }
        };
    });

    directives.directive("collapser", function($compile) {
        return {
            restrict: 'C',
            scope: {
                collapser: '='
            },
            link: function (scope, element, attrs) {
                element.bind('click', function() {
                    var target = $(scope.collapser),
                        shown = target.hasClass('in');

                    if (shown) {
                        $(scope.collapser).collapse('toggle');
                        element.find('.fa-caret-down').removeClass("fa-caret-down").addClass("fa-caret-right");
                        target.find('.collapse').collapse('hide');
                        target.find('.fa-caret-down').removeClass("fa-caret-down").addClass("fa-caret-right");
                    } else {
                        element.find('.fa-caret-right').removeClass("fa-caret-right").addClass("fa-caret-down");
                        $(scope.collapser).collapse('toggle');
                    }
                });
            }
        };
    });

    directives.directive("mtInvalid", function() {
        return {
            restrict: 'A',
            scope: {
                mtInvalid: '='
            },
            link: function (scope, element, attrs) {
                scope.$watch("$parent." + scope.mtInvalid, function (value) {
                    if (value === true) {
                        element.addClass("text-danger");
                    } else {
                        element.removeClass("text-danger");
                    }
                });
            }
        };
    });

    directives.directive('schedulerFilter', ['JobsService', '$timeout', function (JobsService, $timeout) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element),
                    table = angular.element('#' + attrs.schedulerJqgridSearch),
                    eventType = elem.data('event-type'),
                    timeoutHnd,
                    filter = function (time) {
                        var field = elem.data('search-field'),
                            value = elem.data('search-value'),
                            type = elem.data('field-type') || 'string',
                            array = [],
                            values = {},
                            prop;

                        values['activity'] = 'ACTIVE,FINISHED,NOTSTARTED';
                        values['status'] = 'OK,PAUSED,BLOCKED,ERROR';

                        if (type === 'array') {
                            if (elem.children().hasClass("fa-check-square-o")) {
                                elem.children().removeClass("fa-check-square-o").addClass("fa-square-o");
                            } else if (elem.children().hasClass("fa-square-o")) {
                                elem.children().removeClass("fa-square-o").addClass("fa-check-square-o");
                            }
                            angular.forEach(values[field].split(','), function (val) {
                                if (angular.element('#' + val).children().hasClass("fa-check-square-o")) {
                                    array.push(val);
                                }
                            });
                            JobsService.setParam(field, array.join(','));
                        } else {
                            JobsService.setParam(field, elem.val());
                        }
                        JobsService.fetchJobs();
                    };
                switch (eventType) {
                    case 'keyup':
                        elem.keyup(function () {
                            filter(500);
                        });
                        break;
                    case 'change':
                        elem.change(filter);
                        break;
                    default:
                        elem.click(filter);
                }
            }
        };
    }]);
}());
