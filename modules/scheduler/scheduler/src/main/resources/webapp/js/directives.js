(function () {
    'use strict';

    /* Directives */

    var directives = angular.module('scheduler.directives', []);

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

    directives.directive("daysOfWeek", function() {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, ngModel) {

                var days = [
                        { label: "Monday", value: "0" }, { label: "Tuesday", value: "1" },
                        { label: "Wednesday", value: "2" }, { label: "Thursday", value: "3" },
                        { label: "Friday", value: "4" }, {label: "Saturday", value: "5" },
                        { label: "Sunday", value: "6" }
                    ];

                scope.$watch(function() {
                    return ngModel.$modelValue;
                }, function(values) {
                    if (values) {
                        angular.forEach(days, function(day) {
                            day.selected = values.indexOf("" + day.value) != -1;
                        });
                        element.multiselect("dataprovider", days);
                    }
                });
            
                element.multiselect({
                    numberDisplayed: 7,
                    dataprovider: days
                });

                element.multiselect("dataprovider", days);
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
