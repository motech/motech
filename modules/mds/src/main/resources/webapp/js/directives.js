(function () {

    'use strict';

    var mds = angular.module('mds');

    /**
    * Show/hide details about a field by clicking on chevron icon in the first column in
    * the field table.
    */
    mds.directive('mdsExpandAccordion', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var elem = angular.element(element),
                    target = angular.element('#field-tabs-{0}'.format(scope.$index));

                target.livequery(function () {
                    angular.element(this).on({
                        show: function () {
                            elem.find('i')
                                .removeClass('icon-chevron-right')
                                .addClass('icon-chevron-down');
                        },
                        hide: function () {
                            elem.find('i')
                                .removeClass('icon-chevron-down')
                                .addClass('icon-chevron-right');
                        }
                    });

                    target.expire();
                });
            }
        };
    });

    /**
    * Ensure that if no field name has been entered it should be filled in by generating a camel
    * case name from the field display name. If you pass a 'new' value to this directive then it
    * will be check name of new field. Otherwise you have to pass a index to find a existing field.
    */
    mds.directive('mdsCamelCase', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                angular.element(element).focusout(function () {
                    var attrValue = attrs.mdsCamelCase,
                        field;

                    if (_.isEqual(attrValue, 'new')) {
                        field = scope.newField;
                    } else if (_.isNumber(+attrValue)) {
                        field = scope.fields && scope.fields[+attrValue];
                    }

                    if (field && field.basic && isBlank(field.basic.name)) {
                        scope.safeApply(function () {
                            field.basic.name = camelCase(field.basic.displayName);
                        });
                    }
                });
            }
        };
    });

    /**
    * Add ability to change model property mode on UI from read to write and vice versa. For this
    * to work there should be two tags next to each other. First tag (span, div) should present
    * property in the read mode. Second tag (input) should present property in the write mode. By
    * default property should be presented in the read mode and the second tag should be hidden.
    */
    mds.directive('mdsEditable', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var elem = angular.element(element),
                    read = elem.find('span'),
                    write = elem.find('input');

                elem.click(function (e) {
                    e.stopPropagation();

                    read.hide();
                    write.show();
                    write.focus();
                });

                write.click(function (e) {
                    e.stopPropagation();
                });

                write.focusout(function () {
                    write.hide();
                    read.show();
                });
            }
        };
    });

    /**
    * Add a time picker (without date) to an element.
    */
    mds.directive('mdsTimePicker', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                angular.element(element).timepicker({
                    onSelect: function (timeTex) {
                        scope.safeApply(function () {
                            ngModel.$setViewValue(timeTex);
                        });
                    }
                });
            }
        };
    });

    /**
    * Add a datetime picker to an element.
    */
    mds.directive('mdsDatetimePicker', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                angular.element(element).datetimepicker({
                    showTimezone: true,
                    useLocalTimezone: true,
                    dateFormat: 'yy-mm-dd',
                    timeFormat: 'HH:mm z',
                    onSelect: function (dateTex) {
                        scope.safeApply(function () {
                            ngModel.$setViewValue(dateTex);
                        });
                    }
                });
            }
        };
    });

    /**
    * Add extra formating to textarea tag. ngModel have to be an array. Each element of array will
    * be splitted by new line on UI.
    */
    mds.directive('mdsSplitArray', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                ngModel.$parsers.push(function (text) {
                    return text.split("\n");
                });

                ngModel.$formatters.push(function (array) {
                    return array.join("\n");
                });
            }
        };
    });

}());
