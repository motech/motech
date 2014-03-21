(function () {
    'use strict';

    var directives = angular.module('eventAggregation.directives', []);

    directives.directive('tagsInput', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {
                element.tagsInput();    // destroys scope, why?
            }
        };
    });

    directives.directive('datetimePicker', function() {
        var momentDateFormat = 'DD MMM YYYY HH:mm Z';

        function fromFormattedDate(formattedDate) {
            return moment(formattedDate, momentDateFormat).valueOf().toString();
        }

        function toFormattedDate(startTimeInMillis) {
            if (typeof(startTimeInMillis) === 'undefined') {
                startTimeInMillis = moment().valueOf().toString();
            }
            return moment(parseInt(startTimeInMillis, 10)).format(momentDateFormat);
        }

        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {
                ngModel.$parsers.push(fromFormattedDate);
                ngModel.$formatters.push(toFormattedDate);
                element.datetimepicker({
                    showTimezone: true,
                    useLocalTimezone: true,
                    dateFormat: 'dd M yy',
                    timeFormat: 'HH:mm z',
                    onSelect: function(formattedDate) {
                        ngModel.$setViewValue(formattedDate);
                    }
                });
            }
        };
    });

    directives.directive('cronMaker', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {
                $(attrs.cronMaker).cron({
                    initial: scope.rule.aggregationSchedule.cronExpression,
                    onChange: function() {
                        ngModel.$setViewValue($(this).cron("value") + " ?");
                    }
                });
            }
        };
    });

    directives.directive('tooltip', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {
                element.popover({
                    trigger: 'focus',
                    placement: 'right',
                    html: true,
                    title: $($(attrs.tooltip).children()[0]),
                    content: $($(attrs.tooltip).children()[1])
                });
            }
        };
    });
}());

