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
                        'show.bs.collapse': function () {
                            elem.find('i')
                                .removeClass('icon-chevron-right')
                                .addClass('icon-chevron-down');
                        },
                        'hide.bs.collapse': function () {
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

    mds.directive('mdsHeaderAccordion', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var elem = angular.element(element),
                    target = elem.find(".accordion-content");

                target.on({
                    show: function () {
                        elem.find('.accordion-icon')
                            .removeClass('icon-chevron-right')
                            .addClass('icon-chevron-down');
                    },
                    hide: function () {
                        elem.find('.accordion-icon')
                            .removeClass('icon-chevron-down')
                            .addClass('icon-chevron-right');
                    }
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

    mds.directive('restFieldsItem', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var elem = angular.element(element),
                    group = elem.attr('rest-fields-item');

                elem.click(function() {
                    $(this).toggleClass("rest-fields-item-selected");
                });

                elem.dblclick(function() {
                    var elem = angular.element(element),
                        id = elem.attr('fieldId'),
                        order = elem.attr('order'),
                        item;

                    if (typeof scope.findFieldById(id, scope.selectedEntityAdvancedAvailableFields) === "undefined") {
                        item = scope.findFieldById(id, scope.selectedEntityAdvancedFields);
                        scope.selectedEntityAdvancedFields.splice(order, 1);
                        scope.selectedEntityAdvancedAvailableFields.push(item);
                    } else {
                        item = scope.findFieldById(id, scope.selectedEntityAdvancedAvailableFields);
                        scope.selectedEntityAdvancedAvailableFields.splice(order, 1);
                        scope.selectedEntityAdvancedFields.push(item);
                    }
                    scope.safeApply();
                });

                elem.disableSelection();
            }
        };
    });

    mds.directive('restFieldsRight', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var elem = angular.element(element),
                    selectedItemsSelector = ".rest-fields-item-selected";

                elem.click(function (e) {
                    var source = $(".rest-fields-available"),
                        selected = source.children(selectedItemsSelector),
                        addItems = [], removeItems = [];

                    if (selected.size() !== 0) {
                        selected.each(function() {
                            var e = $(this),
                            id = e.attr('fieldId'),
                            item = scope.findFieldById(id, scope.selectedEntityAdvancedAvailableFields),
                            keepGoing = true;

                            $(this).toggleClass("rest-fields-item-selected");

                            angular.forEach(scope.selectedEntityAdvancedAvailableFields, function (field, index) {
                                if (field.id === id && keepGoing === true) {
                                    scope.selectedEntityAdvancedAvailableFields.splice(index, 1);
                                    keepGoing = false;
                                }
                            });
                            scope.selectedEntityAdvancedFields.push(item);
                        });
                    }
                    scope.safeApply();
                    e.preventDefault();
                });
            }
        };
    });

    mds.directive('restFieldsLeft', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var elem = angular.element(element),
                    selectedItemsSelector = ".rest-fields-item-selected";

                elem.click(function (e) {
                    var source = $(".rest-fields-selected"),
                        selected = source.children(selectedItemsSelector),
                        addItems = [], removeItems = [];

                    if (selected.size() !== 0) {
                        selected.each(function() {
                            var e = $(this),
                            id = e.attr('fieldId'),
                            item = scope.findFieldById(id, scope.selectedEntityAdvancedFields),
                            keepGoing = true;

                            $(this).toggleClass("rest-fields-item-selected");

                            angular.forEach(scope.selectedEntityAdvancedFields, function (field, index) {
                                if (field.id === id && keepGoing === true) {
                                    scope.selectedEntityAdvancedFields.splice(index, 1);
                                    keepGoing = false;
                                }
                            });
                            scope.selectedEntityAdvancedAvailableFields.push(item);
                        });
                    }
                    scope.safeApply();
                    e.preventDefault();
                });
            }
        };
    });

    /**
    * Displays entity instances data using jqGrid
    */
    mds.directive('entityInstancesGrid', function($compile, $http, $templateCache) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element);

                $.ajax({
                    type: "GET",
                    url: "../mds/entities/" + scope.selectedEntity.id + "/fields",
                    dataType: "json",
                    success: function(result)
                    {
                        var colModel = [], i;

                        for (i=0; i<result.length; i+=1) {
                            colModel.push({
                                name: result[i].basic.displayName,
                                index: result[i].basic.displayName,
                                jsonmap: "fields." + i + ".value"
                            });
                        }

                        elem.jqGrid({
                            url: "../mds/entities/" + scope.selectedEntity.id + "/instances",
                            datatype: 'json',
                            jsonReader:{
                                repeatitems:false
                            },
                            prmNames: {
                                sort: 'sortColumn',
                                order: 'sortDirection'
                            },
                            shrinkToFit: true,
                            autowidth: true,
                            rownumbers: true,
                            rowNum: 2,
                            rowList: [2, 5, 10, 20, 50],
                            colModel: colModel,
                            pager: '#' + attrs.entityInstancesGrid,
                            width: '100%',
                            height: 'auto',
                            viewrecords: true,
                            gridComplete: function () {
                                $('#entityInstancesTable').children('div').width('100%');
                                $('.ui-jqgrid-htable').addClass('table-lightblue');
                                $('.ui-jqgrid-btable').addClass("table-lightblue");
                                $('.ui-jqgrid-htable').addClass('table-lightblue');
                                $('.ui-jqgrid-bdiv').width('100%');
                                $('.ui-jqgrid-hdiv').width('100%');
                                $('.ui-jqgrid-hbox').width('100%');
                                $('.ui-jqgrid-view').width('100%');
                                $('#t_resourceTable').width('auto');
                                $('.ui-jqgrid-pager').width('100%');
                                $('#entityInstancesTable').children('div').each(function() {
                                    $('table', this).width('100%');
                                    $(this).find('#resourceTable').width('100%');
                                    $(this).find('table').width('100%');
                               });
                            }
                        });
                    }
                });
            }
        };
    });

    mds.directive('draggable', function() {
        return function(scope, element) {
            var el = element[0];

            el.draggable = true;
            el.addEventListener(
                'dragstart',
                function(e) {
                    e.dataTransfer.effectAllowed = 'move';
                    e.dataTransfer.setData('Text', this.attributes.fieldId.value);
                    this.classList.add('drag');
                    return false;
                },
                false
            );

            el.addEventListener(
                'dragend',
                function(e) {
                    this.classList.remove('drag');
                    return false;
                },
                false
            );
        };
    });

    mds.directive('draggable', function() {
        return function(scope, element) {
            var el = element[0];

            el.draggable = true;
            el.addEventListener(
                'dragstart',
                function(e) {
                    e.dataTransfer.effectAllowed = 'move';
                    e.dataTransfer.setData('Text', this.attributes.fieldId.value);
                    this.classList.add('drag');
                    return false;
                },
                false
            );

            el.addEventListener(
                'dragend',
                function(e) {
                    this.classList.remove('drag');
                    return false;
                },
                false
            );
        };
    });

    mds.directive('droppable', function() {
        return {
            scope: {
              drop: '&',
              container: '='
            },
            link: function(scope, element) {

                var el = element[0];
                el.addEventListener(
                    'dragover',
                    function(e) {
                        e.dataTransfer.dropEffect = 'move';
                        if (e.preventDefault) {
                            e.preventDefault();
                        }
                        this.classList.add('over');
                        return false;
                    },
                    false
                );

                el.addEventListener(
                    'dragenter',
                    function(e) {
                        this.classList.add('over');
                        return false;
                    },
                    false
                );

                el.addEventListener(
                    'dragleave',
                    function(e) {
                        this.classList.remove('over');
                        return false;
                    },
                    false
                );

                el.addEventListener(
                    'drop',
                    function(e) {
                        if (e.stopPropagation) {
                            e.stopPropagation();
                        }
                        var fieldId = e.dataTransfer.getData('Text'),
                            containerId = this.id;

                        scope.$apply(function(scope) {
                            var fn = scope.drop();
                            if ('undefined' !== typeof fn) {
                                fn(fieldId, containerId);
                            }
                        });

                        return false;
                    },
                    false
                );
            }
        };
    });
}());
