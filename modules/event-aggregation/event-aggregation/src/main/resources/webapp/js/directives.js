(function () {
    'use strict';

    angular.module('event-aggregation').directive('tagsInput', function() {

        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {
                element.tagsInput();    // destroys scope, why?
            }
        };
    });

    angular.module('event-aggregation').directive('datetimePicker', function() {

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

    angular.module('event-aggregation').directive('cronMaker', function() {

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

    angular.module('event-aggregation').directive('tooltip', function() {

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

    angular.module('event-aggregation').directive('innerlayout', function() {
        return {
            restrict: 'EA',
            link: function(scope, elm, attrs) {
                var eastSelector;
                /*
                * Define options for inner layout
                */
                scope.innerLayoutOptions = {
                    name: 'innerLayout',
                    resizable: true,
                    slidable: true,
                    closable: true,
                    east__paneSelector: "#inner-east",
                    center__paneSelector: "#inner-center",
                    east__spacing_open: 6,
                    spacing_closed: 30,
                    center__showOverflowOnHover: true,
                    east__size: 300,
                    east__minSize: 200,
                    east__maxSize: 350,
                    showErrorMessages: true, // some panes do not have an inner layout
                    resizeWhileDragging: true,
                    center__minHeight: 100,
                    contentSelector: ".ui-layout-content",
                    togglerContent_open: '',
                    togglerContent_closed: '<div><i class="icon-caret-left button"></i></div>',
                    autoReopen: false, // auto-open panes that were previously auto-closed due to 'no room'
                    noRoom: true,
                    togglerAlign_closed: "top", // align to top of resizer
                    togglerAlign_open: "top",
                    togglerLength_open: 0,
                    togglerLength_closed: 35,
                    togglerTip_open: "Close This Pane",
                    togglerTip_closed: "Open This Pane",
                    east__initClosed: true,
                    initHidden: true
                };

                // create the page-layout, which will ALSO create the tabs-wrapper child-layout
                scope.innerLayout = elm.layout(scope.innerLayoutOptions);

            }
        };
    });

}());

