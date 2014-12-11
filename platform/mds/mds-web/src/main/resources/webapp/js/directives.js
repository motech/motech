(function () {

    'use strict';

    var directives = angular.module('mds.directives', []),
       getRelationshipId = function(object) {
            var val = '';
            if (object) {
                val = object.objectId;
                if (!val) {
                    val = object.id;
                }
            }
            return val;
        },
        relationshipFormatter = function(cellValue, options, rowObject) {
           var i, objects, val = cellValue;
           if (cellValue) {
               objects = [].concat(cellValue);
               val = '';
               for (i = 0; i < objects.length; i += 1) {
                   val += '#' + getRelationshipId(objects[i]);
                   if (i < objects.length - 1) {
                       val += ' ';
                   }
               }
           }
           return val;
        },
        textFormatter = function (cellValue, options, rowObject) {
            var val = cellValue,
            TEXTLENGTHLIMIT = 40; //limit of characters for display in jqgrid instances if field type is textarea

            if (cellValue !== null && cellValue !== undefined && cellValue.length > TEXTLENGTHLIMIT) {
                val = cellValue.substring(0, TEXTLENGTHLIMIT);
                val = val + '...';
            }
            return val;
        },
        stringEscapeFormatter = function (cellValue, options, rowObject) {
            var val = '';
                val = _.escape(cellValue);
            return val;
        },
        mapFormatter = function (cellValue, options, rowObject) {
            var result = '', val = cellValue;
            angular.forEach(cellValue,
                function (value, key) {
                    if (key) {
                        result = result.concat(key, ' : ', value,'\n');
                    }
                }, result);
            return result;
        };

    function findCurrentScope(startScope, functionName) {
        var parent = startScope;

        while (!parent[functionName]) {
            parent = parent.$parent;
        }

        return parent;
    }

    function buildGridColModel(colModel, fields, scope) {
        var i, cmd, field;

        for (i = 0; i < fields.length; i += 1) {
            field = fields[i];

            cmd = {
               label: field.basic.displayName,
               name: field.basic.name,
               index: field.basic.name,
               jsonmap: "fields." + i + ".value"
            };

            cmd.formatter = stringEscapeFormatter;

            if (scope.isDateField(field)) {
                cmd.formatter = 'date';
                cmd.formatoptions = { newformat: 'Y-m-d'};
            }

            if (scope.isRelationshipField(field)) {
                // append a formatter for relationships
                cmd.formatter = relationshipFormatter;
            }

            if (scope.isTextArea(field.settings)) {
                cmd.formatter = textFormatter;
                cmd.classes = 'text';
            }

            if (scope.isMapField(field)) {
                cmd.formatter = mapFormatter;
            }

            colModel.push(cmd);
        }
    }

    /**
    * Show/hide details about a field by clicking on caret icon in the first column in
    * the field table.
    */
    directives.directive('mdsExpandAccordion', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var target = angular.element($('#entityFieldsLists'));

                target.on('show.bs.collapse', function (e) {
                    $(e.target).siblings('.panel-heading').find('i.icon-caret-right')
                        .removeClass('icon-caret-right')
                        .addClass('icon-caret-down');
                });

                target.on('hide.bs.collapse', function (e) {
                    $(e.target).siblings('.panel-heading').find('i.icon-caret-down')
                        .removeClass('icon-caret-down')
                        .addClass('icon-caret-right');
                });
            }
        };
    });

    directives.directive('mdsHeaderAccordion', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var elem = angular.element(element),
                    target = elem.find(".panel-collapse");

                target.on({
                    'show.bs.collapse': function () {
                        elem.find('.panel-icon')
                            .removeClass('icon-caret-right')
                            .addClass('icon-caret-down');
                    },
                    'hide.bs.collapse': function () {
                        elem.find('.panel-icon')
                            .removeClass('icon-caret-down')
                            .addClass('icon-caret-right');
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
    directives.directive('mdsCamelCase', function (MDSUtils) {
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
                            field.basic.name = MDSUtils.camelCase(field.basic.displayName);
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
    directives.directive('mdsEditable', function () {
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

    directives.directive("fileread", function () {
        return {
            scope: {
                fileread: "="
            },
            link: function (scope, element, attributes) {
                element.bind("change", function (changeEvent) {
                    var reader = new FileReader();
                    reader.onload = function (loadEvent) {
                        scope.$apply(function () {
                            scope.fileread = loadEvent.target.result;
                        });
                    };
                    if(changeEvent.target.files[0] !== undefined) {
                        reader.readAsDataURL(changeEvent.target.files[0]);
                    }
                });
            }
        };
    });

    /**
    * Add a time picker (without date) to an element.
    */
    directives.directive('mdsTimePicker', function () {
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
    directives.directive('mdsDatetimePicker', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                angular.element(element).datetimepicker({
                    showTimezone: true,
                    changeYear: true,
                    useLocalTimezone: true,
                    dateFormat: 'yy-mm-dd',
                    timeFormat: 'HH:mm z',
                    onSelect: function (dateTex) {
                        scope.safeApply(function () {
                            ngModel.$setViewValue(dateTex);
                        });
                    },
                    onClose: function (year, month, inst) {
                        var viewValue = $(this).val();
                        scope.safeApply(function () {
                            ngModel.$setViewValue(viewValue);
                        });
                    },
                    onChangeMonthYear: function (year, month, inst) {
                        var curDate = $(this).datepicker("getDate");
                        if (curDate === null) {
                            return;
                        }
                        if (curDate.getYear() !== year || curDate.getMonth() !== month - 1) {
                            curDate.setYear(year);
                            curDate.setMonth(month - 1);
                            $(this).datepicker("setDate", curDate);
                        }
                    }
                });
            }
        };
    });

    /**
    * Add a date picker to an element.
    */
    directives.directive('mdsDatePicker', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                angular.element(element).datepicker({
                    changeYear: true,
                    showButtonPanel: true,
                    dateFormat: 'yy-mm-dd',
                    onSelect: function (dateTex) {
                        scope.safeApply(function () {
                            ngModel.$setViewValue(dateTex);
                        });
                    },
                    onChangeMonthYear: function (year, month, inst) {
                        var curDate = $(this).datepicker("getDate");
                        if (curDate === null) {
                            return;
                        }
                        if (curDate.getYear() !== year || curDate.getMonth() !== month - 1) {
                            curDate.setYear(year);
                            curDate.setMonth(month - 1);
                            $(this).datepicker("setDate", curDate);
                        }
                    }
                });
            }
        };
    });

    /**
    * Add "Item" functionality of "Connected Lists" control to the element. "Connected Lists Group"
    * is passed as a value of the attribute. If item is selected '.connected-list-item-selected-{group}
    * class is added.
    */
    directives.directive('connectedListTargetItem', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var jQelem = angular.element(element),
                    elem = element[0],
                    connectWith = jQelem.attr('connect-with'),
                    sourceContainer = $('.connected-list-source.' + connectWith),
                    targetContainer = $('.connected-list-target.' + connectWith),
                    condition = jQelem.attr('condition');

                if (typeof condition !== 'undefined' && condition !== false) {
                    if (!scope.$eval(condition)){
                        return;
                    }
                }

                jQelem.attr('draggable','true');

                jQelem.addClass(connectWith);
                jQelem.addClass("target-item");

                jQelem.click(function() {
                    $(this).toggleClass("selected");
                    scope.$apply();
                });

                jQelem.dblclick(function() {
                    var e = $(this),
                        source = scope[sourceContainer.attr('connected-list-source')],
                        target = scope[targetContainer.attr('connected-list-target')],
                        index = parseInt(e.attr('item-index'), 10),
                        item = target[index];
                    e.removeClass("selected");
                    scope.$apply(function() {
                        source.push(item);
                        target.splice(index, 1);
                        sourceContainer.trigger('contentChange', [source]);
                        targetContainer.trigger('contentChange', [target]);
                    });
                });

                elem.addEventListener('dragenter', function(e) {
                    $(this).addClass('over');
                    return false;
                }, false);

                elem.addEventListener('dragleave', function(e) {
                    $(this).removeClass('over');
                }, false);

                elem.addEventListener('dragover', function(e) {
                    e.preventDefault();
                    return false;
                }, false);

                elem.addEventListener('dragstart', function(e) {
                    var item = $(this);
                    item.addClass('selected');
                    item.fadeTo(100, 0.4);
                    e.dataTransfer.effectAllowed = 'move';
                    e.dataTransfer.setData('origin', 'target');
                    e.dataTransfer.setData('index', item.attr('item-index'));
                    return false;
                }, false);

                elem.addEventListener('dragend', function(e) {
                    var item = $(this);
                    item.removeClass('selected');
                    item.fadeTo(100, 1.0);
                    return false;
                }, false);

                elem.addEventListener('drop', function(e) {
                    e.stopPropagation();
                    var itemOriginContainer = e.dataTransfer.getData('origin'),
                        index = parseInt(e.dataTransfer.getData('index'), 10),
                        thisIndex = parseInt($(this).attr('item-index'), 10),
                        source, target, item;

                    $(this).removeClass('over');
                    $(this.parentNode).removeClass('over');
                    source = scope[sourceContainer.attr('connected-list-source')];
                    target = scope[targetContainer.attr('connected-list-target')];

                    if (itemOriginContainer === 'target') {
                        // movement inside one container
                        item = target[index];
                        if(thisIndex > index) {
                            thisIndex += 1;
                        }
                        scope.$apply(function() {
                            target[index] = 'null';
                            target.splice(thisIndex, 0, item);
                            target.splice(target.indexOf('null'), 1);
                            targetContainer.trigger('contentChange', [target]);
                        });
                    } else if (itemOriginContainer === 'source') {
                        item = source[index];
                        scope.$apply(function() {
                            target.splice(thisIndex, 0, item);
                            source.splice(index, 1);
                            sourceContainer.trigger('contentChange', [source]);
                            targetContainer.trigger('contentChange', [target]);
                        });
                    }
                    return false;
                }, false);
            }
        };
    });

    directives.directive('connectedListSourceItem', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var jQelem = angular.element(element),
                    elem = element[0],
                    connectWith = jQelem.attr('connect-with'),
                    sourceContainer = $('.connected-list-source.' + connectWith),
                    targetContainer = $('.connected-list-target.' + connectWith),
                    condition = jQelem.attr('condition');

                if (typeof condition !== 'undefined' && condition !== false) {
                    if (!scope.$eval(condition)){
                        return;
                    }
                }

                jQelem.attr('draggable','true');

                jQelem.addClass(connectWith);
                jQelem.addClass("source-item");

                jQelem.click(function() {
                    $(this).toggleClass("selected");
                    scope.$apply();
                });

                jQelem.dblclick(function() {
                    var e = $(this),
                        source = scope[sourceContainer.attr('connected-list-source')],
                        target = scope[targetContainer.attr('connected-list-target')],
                        index = parseInt(e.attr('item-index'), 10),
                        item = source[index];
                    e.removeClass("selected");
                    scope.$apply(function() {
                        target.push(item);
                        source.splice(index, 1);
                        sourceContainer.trigger('contentChange', [source]);
                        targetContainer.trigger('contentChange', [target]);
                    });
                });

                elem.addEventListener('dragenter', function(e) {
                    $(this).addClass('over');
                    return false;
                }, false);

                elem.addEventListener('dragleave', function(e) {
                    $(this).removeClass('over');
                    return false;
                }, false);

                elem.addEventListener('dragover', function(e) {
                    e.preventDefault();
                    return false;
                }, false);

                elem.addEventListener('dragstart', function(e) {
                    var item = $(this);
                    item.addClass('selected');
                    item.fadeTo(100, 0.4);
                    e.dataTransfer.effectAllowed = 'move';
                    e.dataTransfer.setData('origin', 'source');
                    e.dataTransfer.setData('index', item.attr('item-index'));
                    return false;
                }, false);

                elem.addEventListener('dragend', function(e) {
                    var item = $(this);
                    item.removeClass('selected');
                    item.fadeTo(100, 1.0);
                    return false;
                }, false);

                elem.addEventListener('drop', function(e) {
                    e.stopPropagation();
                    var itemOriginContainer = e.dataTransfer.getData('origin'),
                        index = parseInt(e.dataTransfer.getData('index'), 10),
                        thisIndex = parseInt($(this).attr('item-index'), 10),
                        source, target, item;

                    $(this).removeClass('over');
                    $(this.parentNode).removeClass('over');
                    source = scope[sourceContainer.attr('connected-list-source')];
                    target = scope[targetContainer.attr('connected-list-target')];
                    if (itemOriginContainer === 'source') {
                        // movement inside one container
                        item = source[index];
                        if(thisIndex > index) {
                            thisIndex += 1;
                        }
                        scope.$apply(function() {
                            source[index] = 'null';
                            source.splice(thisIndex, 0, item);
                            source.splice(source.indexOf('null'), 1);
                            sourceContainer.trigger('contentChange', [source]);
                        });
                    } else if (itemOriginContainer === 'target') {
                        item = target[index];
                        scope.$apply(function() {
                            source.splice(thisIndex, 0, item);
                            target.splice(index, 1);
                            sourceContainer.trigger('contentChange', [source]);
                            targetContainer.trigger('contentChange', [target]);
                        });
                    }
                    return false;
                }, false);
            }
        };
    });

    /**
    * Add "Source List" functionality of "Connected Lists" control to the element (container).
    * "Connected Lists Group" is passed as a value of the attribute. "onItemsAdd", "onItemsRemove"
    * and "onItemMove" callback functions are registered to handle items adding/removing/sorting.
    */
    directives.directive('connectedListSource', function (Entities) {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                var jQelem = angular.element(element), elem = element[0], connectWith = jQelem.attr('connect-with'),
                    onContentChange = jQelem.attr('on-content-change');

                jQelem.addClass('connected-list-source');
                jQelem.addClass(connectWith);

                if(typeof scope[onContentChange] === typeof Function) {
                    jQelem.on('contentChange', function(e, content) {
                        scope[onContentChange](content);
                    });
                }

                elem.addEventListener('dragenter', function(e) {
                    $(this).addClass('over');
                    return false;
                }, false);

                elem.addEventListener('dragleave', function(e) {
                    $(this).removeClass('over');
                    return false;
                }, false);

                elem.addEventListener('dragover', function(e) {
                    e.preventDefault();
                    return false;
                }, false);

                elem.addEventListener('drop', function(e) {
                    e.stopPropagation();

                    var itemOriginContainer = e.dataTransfer.getData('origin'),
                        index = parseInt(e.dataTransfer.getData('index'), 10),
                        sourceContainer = $('.connected-list-source.' + connectWith),
                        targetContainer = $('.connected-list-target.' + connectWith),
                        source, target, item;

                    $(this).removeClass('over');
                    source = scope[sourceContainer.attr('connected-list-source')];
                    target = scope[targetContainer.attr('connected-list-target')];
                    if (itemOriginContainer === 'source') {
                        // movement inside one container
                        item = source[index];
                        scope.$apply(function() {
                            source.splice(index, 1);
                            source.push(item);
                            sourceContainer.trigger('contentChange', [source]);
                        });
                    } else if (itemOriginContainer === 'target') {
                        item = target[index];
                        scope.$apply(function() {
                            source.push(item);
                            target.splice(index, 1);
                            sourceContainer.trigger('contentChange', [source]);
                            targetContainer.trigger('contentChange', [target]);
                        });
                    }
                    return false;
                }, false);

                jQelem.keyup(function(event) {
                    var sourceContainer = $('.connected-list-source.' + attr.connectWith),
                        targetContainer = $('.connected-list-target.' + attr.connectWith),
                        source = scope[sourceContainer.attr('connected-list-source')],
                        target = scope[targetContainer.attr('connected-list-target')],
                        selectedElements = sourceContainer.children('.selected'),
                        selectedIndices = [], selectedItems = [],
                        array = [];

                    if(event.which === 13) {
                        selectedElements.each(function() {
                             var that = $(this),
                                 index = parseInt(that.attr('item-index'), 10),
                                 item = source[index];

                             that.removeClass('selected');
                             selectedIndices.push(index);
                             selectedItems.push(item);
                        });

                        scope.safeApply(function () {
                            var viewScope = findCurrentScope(scope, 'draft');

                            angular.forEach(selectedIndices.reverse(), function(itemIndex) {
                                 source.splice(itemIndex, 1);
                            });

                            angular.forEach(selectedItems, function(item) {
                                target.push(item);
                            });

                            angular.forEach(target, function (item) {
                                array.push(item.id);
                            });

                            viewScope.draft({
                                edit: true,
                                values: {
                                    path: attr.mdsPath,
                                    advanced: true,
                                    value: [array]
                                }
                            });

                            sourceContainer.trigger('contentChange', [source]);
                            targetContainer.trigger('contentChange', [target]);
                        });
                    }
                });
            }
        };
    });

    /**
    * Add "Target List" functionality of "Connected Lists" control to the element (container).
    * "Connected Lists Group" is passed as a value of the attribute. "onItemsAdd", "onItemsRemove"
    * and "onItemMove" callback functions are registered to handle items adding/removing/sorting.
    */
    directives.directive('connectedListTarget', function (Entities) {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                var jQelem = angular.element(element), elem = element[0], connectWith = jQelem.attr('connect-with'),
                    onContentChange = jQelem.attr('on-content-change');

                jQelem.addClass('connected-list-target');
                jQelem.addClass(connectWith);

                if(typeof scope[onContentChange] === typeof Function) {
                    jQelem.on('contentChange', function(e, content) {
                        scope[onContentChange](content);
                    });
                }

                elem.addEventListener('dragenter', function(e) {
                    $(this).addClass('over');
                    return false;
                }, false);

                elem.addEventListener('dragleave', function(e) {
                    $(this).removeClass('over');
                    return false;
                }, false);

                elem.addEventListener('dragover', function(e) {
                    e.preventDefault();
                    return false;
                }, false);

                elem.addEventListener('drop', function(e) {
                    e.stopPropagation();

                    var itemOriginContainer = e.dataTransfer.getData('origin'),
                        index = parseInt(e.dataTransfer.getData('index'), 10),
                        sourceContainer = $('.connected-list-source.' + connectWith),
                        targetContainer = $('.connected-list-target.' + connectWith),
                        source, target, item;

                    $(this).removeClass('over');
                    source = scope[sourceContainer.attr('connected-list-source')];
                    target = scope[targetContainer.attr('connected-list-target')];
                    if (itemOriginContainer === 'target') {
                        // movement inside one container
                        item = target[index];
                        scope.$apply(function() {
                            target.splice(index, 1);
                            target.push(item);
                            targetContainer.trigger('contentChange', [target]);
                        });
                    } else if (itemOriginContainer === 'source') {
                        item = source[index];
                        scope.$apply(function() {
                            target.push(item);
                            source.splice(index, 1);
                            sourceContainer.trigger('contentChange', [source]);
                            targetContainer.trigger('contentChange', [target]);
                        });
                    }
                    return false;
                }, false);

                jQelem.keyup(function(event) {
                    var sourceContainer = $('.connected-list-source.' + attr.connectWith),
                        targetContainer = $('.connected-list-target.' + attr.connectWith),
                        source = scope[sourceContainer.attr('connected-list-source')],
                        target = scope[targetContainer.attr('connected-list-target')],
                        selectedElements = targetContainer.children('.selected'),
                        selectedIndices = [], selectedItems = [],
                        array = [];

                    if(event.which === 13) {
                        selectedElements.each(function() {
                             var that = $(this),
                                 index = parseInt(that.attr('item-index'), 10),
                                 item = target[index];

                             that.removeClass('selected');
                             selectedIndices.push(index);
                             selectedItems.push(item);
                        });

                        scope.safeApply(function () {
                            var viewScope = findCurrentScope(scope, 'draft');

                            angular.forEach(selectedIndices.reverse(), function(itemIndex) {
                                target.splice(itemIndex, 1);
                            });

                            angular.forEach(selectedItems, function(item) {
                                source.push(item);
                            });

                            angular.forEach(target, function (item) {
                                array.push(item.id);
                            });

                            viewScope.draft({
                                edit: true,
                                values: {
                                    path: attr.mdsPath,
                                    advanced: true,
                                    value: [array]
                                }
                            });

                            sourceContainer.trigger('contentChange', [source]);
                            targetContainer.trigger('contentChange', [target]);
                        });
                    }
                });
            }
        };
    });

    /**
    * Add "Move selected to target" functionality of "Connected Lists" control to the element (button).
    * "Connected Lists Group" is passed as a value of the 'connect-with' attribute.
    */
    directives.directive('connectedListBtnTo', function (Entities) {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                angular.element(element).click(function (e) {
                    var sourceContainer = $('.connected-list-source.' + attr.connectWith),
                        targetContainer = $('.connected-list-target.' + attr.connectWith),
                        source = scope[sourceContainer.attr('connected-list-source')],
                        target = scope[targetContainer.attr('connected-list-target')],
                        selectedElements = sourceContainer.children('.selected'),
                        selectedIndices = [], selectedItems = [],
                        array = [];

                    selectedElements.each(function() {
                         var that = $(this),
                             index = parseInt(that.attr('item-index'), 10),
                             item = source[index];

                         that.removeClass('selected');
                         selectedIndices.push(index);
                         selectedItems.push(item);
                    });

                    scope.safeApply(function () {
                        var viewScope = findCurrentScope(scope, 'draft');

                        angular.forEach(selectedIndices.reverse(), function(itemIndex) {
                             source.splice(itemIndex, 1);
                        });

                        angular.forEach(selectedItems, function(item) {
                            target.push(item);
                        });

                        angular.forEach(target, function (item) {
                            array.push(item.id);
                        });

                        viewScope.draft({
                            edit: true,
                            values: {
                                path: attr.mdsPath,
                                advanced: true,
                                value: [array]
                            }
                        });

                        sourceContainer.trigger('contentChange', [source]);
                        targetContainer.trigger('contentChange', [target]);
                    });
                });

                angular.element(element).disableSelection();
            }
        };
    });

    /**
    * Add "Move all to target" functionality of "Connected Lists" control to the element (button).
    * "Connected Lists Group" is passed as a value of the 'connect-with' attribute.
    */
    directives.directive('connectedListBtnToAll', function (Entities) {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                angular.element(element).click(function (e) {
                    var sourceContainer = $('.connected-list-source.' + attr.connectWith),
                        targetContainer = $('.connected-list-target.' + attr.connectWith),
                        source = scope[sourceContainer.attr('connected-list-source')],
                        target = scope[targetContainer.attr('connected-list-target')],
                        selectedItems = sourceContainer.children(),
                        viewScope = findCurrentScope(scope, 'draft'),
                        array = [];

                        angular.forEach(source, function (item) {
                            array.push(item.id);
                        });

                        viewScope.draft({
                            edit: true,
                            values: {
                                path: attr.mdsPath,
                                advanced: true,
                                value: [array]
                            }
                        }, function () {
                             scope.safeApply(function () {
                                angular.forEach(source, function(item) {
                                    target.push(item);
                                });

                                source.length = 0;

                                sourceContainer.trigger('contentChange', [source]);
                                targetContainer.trigger('contentChange', [target]);
                             });
                        });
                });
            }
        };
    });

    /**
    * Add "Move selected to source" functionality of "Connected Lists" control to the element (button).
    * "Connected Lists Group" is passed as a value of the 'connect-with' attribute.
    */
    directives.directive('connectedListBtnFrom', function (Entities) {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                angular.element(element).click(function (e) {
                    var sourceContainer = $('.connected-list-source.' + attr.connectWith),
                        targetContainer = $('.connected-list-target.' + attr.connectWith),
                        source = scope[sourceContainer.attr('connected-list-source')],
                        target = scope[targetContainer.attr('connected-list-target')],
                        selectedElements = targetContainer.children('.selected'),
                        selectedIndices = [], selectedItems = [],
                        array = [];

                    selectedElements.each(function() {
                         var that = $(this),
                             index = parseInt(that.attr('item-index'), 10),
                             item = target[index];

                         that.removeClass('selected');
                         selectedIndices.push(index);
                         selectedItems.push(item);
                    });

                    scope.safeApply(function () {
                        var viewScope = findCurrentScope(scope, 'draft');

                        angular.forEach(selectedIndices.reverse(), function(itemIndex) {
                            target.splice(itemIndex, 1);
                        });

                        angular.forEach(selectedItems, function(item) {
                            source.push(item);
                        });

                        angular.forEach(target, function (item) {
                            array.push(item.id);
                        });

                        viewScope.draft({
                            edit: true,
                            values: {
                                path: attr.mdsPath,
                                advanced: true,
                                value: [array]
                            }
                        });

                        sourceContainer.trigger('contentChange', [source]);
                        targetContainer.trigger('contentChange', [target]);
                    });
                });
            }
        };
    });

    /**
    * Add "Move all to source" functionality of "Connected Lists" control to the element (button).
    * "Connected Lists Group" is passed as a value of the 'connect-with' attribute.
    */
    directives.directive('connectedListBtnFromAll', function (Entities) {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                angular.element(element).click(function (e) {
                    var sourceContainer = $('.connected-list-source.' + attr.connectWith),
                        targetContainer = $('.connected-list-target.' + attr.connectWith),
                        source = scope[sourceContainer.attr('connected-list-source')],
                        target = scope[targetContainer.attr('connected-list-target')],
                        viewScope = findCurrentScope(scope, 'draft'),
                        selectedItems = targetContainer.children();

                        viewScope.draft({
                            edit: true,
                            values: {
                                path: attr.mdsPath,
                                advanced: true,
                                value: [[]]
                            }
                        }, function () {
                             scope.safeApply(function () {
                                angular.forEach(target, function(item) {
                                    source.push(item);
                                });

                                target.length = 0;

                                sourceContainer.trigger('contentChange', [source]);
                                targetContainer.trigger('contentChange', [target]);
                             });
                        });
                });
            }
        };
    });

    /**
    * Initializes filterable checkbox and sets a watch in the filterable scope to track changes
    * in "advancedSettings.browsing.filterableFields".
    */
    directives.directive('initFilterable', function () {
        return {
            restrict: 'A',
            link: function (scope) {
                scope.$watch('advancedSettings.browsing.filterableFields', function() {
                    if (!scope.advancedSettings.browsing) {
                        scope.checked = false;
                    } else {
                        scope.checked = (scope.advancedSettings.browsing.filterableFields.indexOf(scope.field.id) >= 0);
                    }
                });
            }
        };
    });

    /**
    * Filtering entity by selected filter.
    */
    directives.directive('clickfilter', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elm = angular.element(element);
                elm.click(function (e) {
                    if (elm.children().hasClass("icon-ok")) {
                        if (elm.text().trim() === 'ALL') {
                            elm.parent().children().each(function(i) {
                               $(elm.parent().children()[i]).children().removeClass('icon-ban-circle').addClass("icon-ok");
                               $(elm.parent().children()[i]).addClass('active');
                            });
                        } else {
                            elm.parent().children().each(function(i) {
                                $(elm.parent().children()[i]).children().removeClass('icon-ok').addClass("icon-ban-circle");
                                $(elm.parent().children()[i]).removeClass('active');
                            });
                            $(this).children().removeClass('icon-ban-circle').addClass('icon-ok');
                            $(this).addClass('active');
                        }
                    }
                    else {
                        if (elm.text().trim() === 'ALL') {
                            elm.parent().children().each(function(i) {
                               $(elm.parent().children()[i]).children().removeClass('icon-ban-circle').addClass("icon-ok");
                               $(elm.parent().children()[i]).addClass('active');
                            });
                        } else {
                            elm.parent().children().each(function(i) {
                               $(elm.parent().children()[i]).children().removeClass('icon-ok');
                               $(elm.parent().children()[i]).children().addClass("icon-ban-circle");
                               $(elm.parent().children()[i]).removeClass('active');
                            });
                            elm.children().addClass('icon-ok').removeClass('icon-ban-circle');
                            elm.addClass('active');
                        }
                    }
                });
            }
        };
    });

    /**
    * Displays entity instances data using jqGrid
    */
    directives.directive('entityInstancesGrid', function ($rootScope, $route) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element);

                $.ajax({
                    type: "GET",
                    url: "../mds/entities/" + scope.selectedEntity.id + "/entityFields",
                    dataType: "json",
                    success: function (result) {
                        var colMd, colModel = [], i, noSelectedFields = true, spanText,
                        noSelectedFieldsText = scope.msg('mds.dataBrowsing.noSelectedFieldsInfo');

                        buildGridColModel(colModel, result, scope);

                        elem.jqGrid({
                            url: "../mds/entities/" + scope.selectedEntity.id + "/instances",
                            headers: {
                                'Accept': 'application/x-www-form-urlencoded',
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            datatype: 'json',
                            mtype: "POST",
                            postData: {
                                fields: JSON.stringify(scope.lookupBy)
                            },
                            jsonReader: {
                                repeatitems: false
                            },
                            prmNames: {
                               sort: 'sortColumn',
                               order: 'sortDirection'
                            },
                            onSelectRow: function (id) {
                                scope.editInstance(id, scope.selectedEntity.module, scope.selectedEntity.name);
                            },
                            resizeStop: function() {
                                $('#entityInstancesTable .ui-jqgrid-htable').width('100%');
                                $('#entityInstancesTable .ui-jqgrid-btable').width('100%');
                            },
                            loadonce: false,
                            colModel: colModel,
                            pager: '#' + attrs.entityInstancesGrid,
                            viewrecords: true,
                            gridComplete: function () {
                                spanText = $('<span>').addClass('ui-jqgrid-status-label ui-jqgrid ui-widget hidden');
                                spanText.append(noSelectedFieldsText).css({padding: '3px 15px'});
                                $('#entityInstancesTable .ui-paging-info').append(spanText);
                                $('.ui-jqgrid-status-label').addClass('hidden');
                                $('#pageInstancesTable_center').addClass('page_instancesTable_center');
                                if ($('#instancesTable').getGridParam('records') !== 0) {
                                    noSelectedFields = true;
                                    $('#pageInstancesTable_center').show();
                                    angular.forEach($("select.multiselect")[0], function(field) {
                                        var name = scope.getFieldName(field.label);
                                        if (name) {
                                            if (field.selected || $rootScope.fieldSelected){
                                                $("#instancesTable").jqGrid('showCol', name);
                                                noSelectedFields = false;
                                            } else {
                                                $("#instancesTable").jqGrid('hideCol', name);
                                            }
                                        }
                                    });
                                    if (noSelectedFields && $rootScope.selectedField) {
                                        $('#pageInstancesTable_center').hide();
                                        $('#entityInstancesTable .ui-jqgrid-status-label').removeClass('hidden');
                                    }
                                    $('#entityInstancesTable').children().width('100%');
                                    $('#entityInstancesTable .ui-jqgrid-htable').addClass("table-lightblue");
                                    $('#entityInstancesTable .ui-jqgrid-btable').addClass("table-lightblue");
                                    $('#entityInstancesTable .ui-jqgrid-htable').width('100%');
                                    $('#entityInstancesTable .ui-jqgrid-btable').width('100%');
                                    $('#entityInstancesTable .ui-jqgrid-bdiv').width('100%');
                                    $('#entityInstancesTable .ui-jqgrid-hdiv').width('100%').show();
                                    $('#entityInstancesTable .ui-jqgrid-view').width('100%');
                                    $('#entityInstancesTable .ui-jqgrid-pager').width('100%');
                                    $('#entityInstancesTable .ui-jqgrid-hbox').css({'padding-right':'0'});
                                    $('#entityInstancesTable .ui-jqgrid-hbox').width('100%');
                                } else {
                                    noSelectedFields = true;
                                    angular.forEach($("select.multiselect")[0], function(field) {
                                        var name = scope.getFieldName(field.label);
                                        if (name && field.selected){
                                            noSelectedFields = false;
                                        }
                                    });
                                    $('#entityInstancesTable .ui-jqgrid-htable').addClass("table-lightblue");
                                    $('#entityInstancesTable .ui-jqgrid-btable').addClass("table-lightblue");
                                    if (noSelectedFields && $rootScope.selectedField) {
                                        $('#entityInstancesTable .ui-jqgrid-status-label').removeClass('hidden');
                                        $('#pageInstancesTable_center').hide();
                                        $('#entityInstancesTable .ui-jqgrid-hdiv').hide();
                                    }
                                }
                            }
                        });
                        scope.$watch("lookupRefresh", function () {
                            $('#' + attrs.id).jqGrid('setGridParam', {
                                page: 1,
                                postData: {
                                    fields: JSON.stringify(scope.lookupBy),
                                    lookup: (scope.selectedLookup) ? scope.selectedLookup.lookupName : "",
                                    filter: (scope.filterBy) ? JSON.stringify(scope.filterBy) : ""
                                }
                            }).trigger('reloadGrid');
                        });
                    }
                });
            }
        };
    });

    directives.directive('multiselectDropdown', function () {
            return {
                restrict: 'A',
                require : 'ngModel',
                link: function (scope, element, attrs) {
                    var selectAll = scope.msg('mds.btn.selectAll'), target = attrs.targetTable, noSelectedFields = true;

                    if (!target) {
                        target = 'instancesTable';
                    }

                    element.multiselect({
                        buttonClass : 'btn btn-default',
                        buttonWidth : 'auto',
                        buttonContainer : '<div class="btn-group" />',
                        maxHeight : false,
                        buttonText : function() {
                                return scope.msg('mds.btn.fields');
                        },
                        selectAllText: selectAll,
                        selectAllValue: 'multiselect-all',
                        includeSelectAllOption: true,
                        onChange: function (optionElement, checked) {
                            if (optionElement) {
                                optionElement.removeAttr('selected');
                                if (checked) {
                                    optionElement.attr('selected', 'selected');
                                }
                            }

                            element.change();

                            if (optionElement) {
                                var name = scope.getFieldName(optionElement.text());
                                // don't act for fields show automatically in trash and history
                                if (scope.autoDisplayFields.indexOf(name) === -1) {
                                    // set the cookie, users have their own browsing settings
                                    scope.markFieldForDataBrowser(name, checked);
                                }
                            }

                            noSelectedFields = true;
                            angular.forEach(element[0], function(field) {
                                var name = scope.getFieldName(field.label);
                                if (name) {
                                    if (field.selected){
                                        $("#" + target).jqGrid('showCol', name);
                                        noSelectedFields = false;
                                    } else {
                                        $("#" + target).jqGrid('hideCol', name);
                                    }
                                }
                            });
                            $('.ui-jqgrid.ui-widget.ui-widget-content').width('100%');
                            $('.ui-jqgrid-htable').width('100%');
                            $('.ui-jqgrid-btable').width('100%');
                            $('.ui-jqgrid-bdiv').width('100%');
                            $('.ui-jqgrid-hdiv').width('100%');
                            $('.ui-jqgrid-view').width('100%');
                            $('.ui-jqgrid-pager').width('100%');
                            $('.ui-jqgrid-hbox').css({'padding-right':'0'});
                            $('.ui-jqgrid-hbox').width('100%');

                            if (noSelectedFields) {
                                $('.page_' + target + '_center').hide();
                                $('.ui-jqgrid-status-label').removeClass('hidden');
                            } else {
                                $('.page_' + target + '_center').show();
                                $('.ui-jqgrid-status-label').addClass('hidden');
                            }
                        }
                   });

                   scope.$watch(function () {
                       return element[0].length;
                   }, function () {
                       element.multiselect('rebuild');
                   });

                   scope.$watch(attrs.ngModel, function () {
                       element.multiselect('refresh');
                   });
                }
            };
    });

    /**
    * Displays instance history data using jqGrid
    */
    directives.directive('instanceHistoryGrid', function($compile, $http, $templateCache) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element);

                $.ajax({
                    type: "GET",
                    url: "../mds/entities/" + scope.selectedEntity.id + "/entityFields",
                    dataType: "json",
                    success: function(result)
                    {
                        var colModel = [], i, noSelectedFields = true, spanText,
                            noSelectedFieldsText = scope.msg('mds.dataBrowsing.noSelectedFieldsInfo');

                        colModel.push({
                            name: "",
                            width: 28,
                            formatter: function () {
                                return "<a><i class='icon-refresh icon-large'></i></a>";
                            },
                            sortable: false
                        });

                        buildGridColModel(colModel, result, scope);

                        elem.jqGrid({
                            url: "../mds/instances/" + scope.selectedEntity.id + "/" + scope.instanceId + "/history",
                            datatype: 'json',
                            jsonReader:{
                                repeatitems:false
                            },
                            onSelectRow: function (id) {
                                var myGrid = $('#historyTable'),
                                cellValue = myGrid.jqGrid ('getCell', id, 'Changes');
                                if (cellValue === "Is Active") {
                                    scope.backToInstance();
                                } else {
                                    scope.historyInstance(id);
                                }
                            },
                            resizeStop: function() {
                                $('#instanceHistoryTable .ui-jqgrid-htable').width('100%');
                                $('#instanceHistoryTable .ui-jqgrid-btable').width('100%');
                            },
                            colModel: colModel,
                            pager: '#' + attrs.instanceHistoryGrid,
                            viewrecords: true,
                            gridComplete: function () {
                                spanText = $('<span>').addClass('ui-jqgrid-status-label ui-jqgrid ui-widget hidden');
                                spanText.append(noSelectedFieldsText).css({padding: '3px 15px'});
                                $('#instanceHistoryTable .ui-paging-info').append(spanText);
                                $('.ui-jqgrid-status-label').addClass('hidden');
                                $('#pageInstanceHistoryTable_center').addClass('page_historyTable_center');
                                if ($('#historyTable').getGridParam('records') !== 0) {
                                    noSelectedFields = true;
                                    $('#pageInstanceHistoryTable_center').show();
                                    angular.forEach($("select.multiselect")[0], function(field) {
                                        var name = scope.getFieldName(field.label);
                                        if (name) {
                                            if (field.selected){
                                                $("#historyTable").jqGrid('showCol', name);
                                                noSelectedFields = false;
                                            } else {
                                                $("#historyTable").jqGrid('hideCol', name);
                                            }
                                        }
                                    });
                                    if (noSelectedFields) {
                                        $('#pageInstanceHistoryTable_center').hide();
                                        $('#instanceHistoryTable .ui-jqgrid-status-label').removeClass('hidden');
                                    }
                                    $('#instanceHistoryTable').children().width('100%');
                                    $('#instanceHistoryTable .ui-jqgrid-htable').addClass('table-lightblue');
                                    $('#instanceHistoryTable .ui-jqgrid-btable').addClass("table-lightblue");
                                    $('#instanceHistoryTable .ui-jqgrid-htable').width('100%');
                                    $('#instanceHistoryTable .ui-jqgrid-btable').width('100%');
                                    $('#instanceHistoryTable .ui-jqgrid-bdiv').width('100%');
                                    $('#instanceHistoryTable .ui-jqgrid-hdiv').width('100%').show();
                                    $('#instanceHistoryTable .ui-jqgrid-hbox').css({'padding-right':'0'});
                                    $('#instanceHistoryTable .ui-jqgrid-hbox').width('100%');
                                    $('#instanceHistoryTable .ui-jqgrid-view').width('100%');
                                    $('#instanceHistoryTable .ui-jqgrid-pager').width('100%');
                                } else {
                                    noSelectedFields = true;
                                    angular.forEach($("select.multiselect")[0], function(field) {
                                        var name = scope.getFieldName(field.label);
                                        if (name && field.selected){
                                            noSelectedFields = false;
                                        }
                                    });
                                    $('#instanceHistoryTable .ui-jqgrid-htable').addClass("table-lightblue");
                                    $('#instanceHistoryTable .ui-jqgrid-btable').addClass("table-lightblue");
                                    if (noSelectedFields) {
                                        $('#instanceHistoryTable .ui-jqgrid-status-label').removeClass('hidden');
                                        $('#pageInstanceHistoryTable_center').hide();
                                        $('#instanceHistoryTable .ui-jqgrid-hdiv').hide();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        };
    });

    /**
    * Displays entity instance trash data using jqGrid
    */
    directives.directive('instanceTrashGrid', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element);

                $.ajax({
                    type: "GET",
                    url: "../mds/entities/" + scope.selectedEntity.id + "/entityFields",
                    dataType: "json",
                    success: function (result) {
                        var colModel = [], i, noSelectedFields = true, spanText,
                        noSelectedFieldsText = scope.msg('mds.dataBrowsing.noSelectedFieldsInfo');

                        buildGridColModel(colModel, result, scope);

                        elem.jqGrid({
                            url: "../mds/entities/" + scope.selectedEntity.id + "/trash",
                            headers: {
                                'Accept': 'application/x-www-form-urlencoded',
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            datatype: 'json',
                            mtype: "GET",
                            postData: {
                                fields: JSON.stringify(scope.lookupBy)
                            },
                            jsonReader: {
                                repeatitems: false
                            },
                            onSelectRow: function (id) {
                                scope.trashInstance(id);
                            },
                            resizeStop: function() {
                                $('#instanceTrashTable .ui-jqgrid-htable').width('100%');
                                $('#instanceTrashTable .ui-jqgrid-btable').width('100%');
                            },
                            loadonce: false,
                            colModel: colModel,
                            pager: '#' + attrs.instanceTrashGrid,
                            viewrecords: true,
                            gridComplete: function () {
                                spanText = $('<span>').addClass('ui-jqgrid-status-label ui-jqgrid ui-widget hidden');
                                spanText.append(noSelectedFieldsText).css({padding: '3px 15px'});
                                $('#instanceTrashTable .ui-paging-info').append(spanText);
                                $('.ui-jqgrid-status-label').addClass('hidden');
                                $('#pageInstanceTrashTable_center').addClass('page_trashTable_center');
                                if ($('#trashTable').getGridParam('records') !== 0) {
                                    noSelectedFields = true;
                                    $('#pageInstanceTrashTable_center').show();
                                    angular.forEach($("select.multiselect")[0], function(field) {
                                        var name = scope.getFieldName(field.label);
                                        if (name) {
                                            if (field.selected){
                                                $("#trashTable").jqGrid('showCol', name);
                                                noSelectedFields = false;
                                            } else {
                                                $("#trashTable").jqGrid('hideCol', name);
                                            }
                                        }
                                    });
                                    if (noSelectedFields) {
                                        $('#pageInstanceTrashTable_center').hide();
                                        $('#instanceTrashTable .ui-jqgrid-status-label').removeClass('hidden');
                                    }
                                    $('#instanceTrashTable').children().width('100%');
                                    $('#instanceTrashTable .ui-jqgrid-htable').addClass('table-lightblue');
                                    $('#instanceTrashTable .ui-jqgrid-btable').addClass("table-lightblue");
                                    $('#instanceTrashTable .ui-jqgrid-htable').width('100%');
                                    $('#instanceTrashTable .ui-jqgrid-btable').width('100%');
                                    $('#instanceTrashTable .ui-jqgrid-bdiv').width('100%');
                                    $('#instanceTrashTable .ui-jqgrid-hdiv').width('100%').show();
                                    $('#instanceTrashTable .ui-jqgrid-hbox').css({'padding-right':'0'});
                                    $('#instanceTrashTable .ui-jqgrid-hbox').width('100%');
                                    $('#instanceTrashTable .ui-jqgrid-view').width('100%');
                                    $('#instanceTrashTable .ui-jqgrid-pager').width('100%');
                                } else {
                                    noSelectedFields = true;
                                    angular.forEach($("select.multiselect")[0], function(field) {
                                        var name = scope.getFieldName(field.label);
                                        if (name && field.selected) {
                                            $("#trashTable").jqGrid('showCol', name);
                                            noSelectedFields = false;
                                        } else if (name) {
                                            $("#trashTable").jqGrid('hideCol', name);
                                        }
                                    });
                                    $('#instanceTrashTable .ui-jqgrid-htable').addClass("table-lightblue");
                                    $('#instanceTrashTable .ui-jqgrid-btable').addClass("table-lightblue");
                                    if (noSelectedFields) {
                                        $('#instanceTrashTable .ui-jqgrid-status-label').removeClass('hidden');
                                        $('#pageInstanceTrashTable_center').hide();
                                        $('#instanceTrashTable .ui-jqgrid-hdiv').hide();
                                    } else {
                                        $('#pageInstanceTrashTable_center').show();
                                        $('#instanceTrashTable').children().width('100%');
                                        $('#instanceTrashTable .ui-jqgrid-htable').addClass('table-lightblue');
                                        $('#instanceTrashTable .ui-jqgrid-btable').addClass("table-lightblue");
                                        $('#instanceTrashTable .ui-jqgrid-htable').width('100%');
                                        $('#instanceTrashTable .ui-jqgrid-btable').width('100%');
                                        $('#instanceTrashTable .ui-jqgrid-bdiv').width('100%');
                                        $('#instanceTrashTable .ui-jqgrid-hdiv').width('100%').show();
                                        $('#instanceTrashTable .ui-jqgrid-hbox').css({'padding-right':'0'});
                                        $('#instanceTrashTable .ui-jqgrid-hbox').width('100%');
                                        $('#instanceTrashTable .ui-jqgrid-view').width('100%');
                                        $('#instanceTrashTable .ui-jqgrid-pager').width('100%');
                                    }
                                }
                            }
                        });
                    }
                });
            }
        };
    });

    directives.directive('droppable', function () {
        return {
            scope: {
                drop: '&'
            },
            link: function (scope, element) {

                var el = element[0];
                el.addEventListener('dragover', function (e) {
                    e.dataTransfer.dropEffect = 'move';

                    if (e.preventDefault) {
                        e.preventDefault();
                    }

                    this.classList.add('over');

                    return false;
                }, false);

                el.addEventListener('dragenter', function () {
                    this.classList.add('over');
                    return false;
                }, false);

                el.addEventListener('dragleave', function () {
                    this.classList.remove('over');
                    return false;
                }, false);

                el.addEventListener('drop', function (e) {
                    var fieldId = e.dataTransfer.getData('Text'),
                        containerId = this.id;

                    if (e.stopPropagation) {
                        e.stopPropagation();
                    }

                    scope.$apply(function (scope) {
                        var fn = scope.drop();

                        if (_.isFunction(fn)) {
                            fn(fieldId, containerId);
                        }
                    });

                    return false;
                }, false);
            }
        };
    });

    /**
    * Add auto saving for field properties.
    */
    directives.directive('mdsAutoSaveFieldChange', function (Entities) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                var func = attr.mdsAutoSaveFieldChange || 'focusout';

                angular.element(element).on(func, function () {
                    var viewScope = findCurrentScope(scope, 'draft'),
                        fieldPath = attr.mdsPath,
                        fieldId = attr.mdsFieldId,
                        entity,
                        value;

                    if (fieldPath === undefined) {
                        fieldPath = attr.ngModel;
                        fieldPath = fieldPath.substring(fieldPath.indexOf('.') + 1);
                    }

                    value = _.isBoolean(ngModel.$modelValue)
                        ? !ngModel.$modelValue
                        : ngModel.$modelValue;

                    viewScope.draft({
                        edit: true,
                        values: {
                            path: fieldPath,
                            fieldId: fieldId,
                            value: [value]
                        }
                    });
                });
            }
        };
    });

    /*
    * Add auto saving for field properties.
    */
    directives.directive('mdsAutoSaveAdvancedChange', function (Entities) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                var func = attr.mdsAutoSaveAdvancedChange || 'focusout';

                angular.element(element).on(func, function () {
                    var viewScope = findCurrentScope(scope, 'draft'),
                        advancedPath = attr.mdsPath,
                        entity,
                        value;

                    if (advancedPath === undefined) {
                        advancedPath = attr.ngModel;
                        advancedPath = advancedPath.substring(advancedPath.indexOf('.') + 1);
                    }

                    value = _.isBoolean(ngModel.$modelValue)
                        ? !ngModel.$modelValue
                        : ngModel.$modelValue;

                    viewScope.draft({
                        edit: true,
                        values: {
                            path: advancedPath,
                            advanced: true,
                            value: [value]
                        }
                    });
                });
            }
        };
    });

    /**
    * Add auto saving for field properties.
    */
    directives.directive('mdsAutoSaveBtnSelectChange', function (Entities) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attrs, ngModel) {
                var elm = angular.element(element),
                viewScope = findCurrentScope(scope, 'draft'),
                fieldPath = attrs.mdsPath,
                fieldId = attrs.mdsFieldId,
                criterionId = attrs.mdsCriterionId,
                entity,
                value;

                elm.children('ul').on('click', function () {
                    value = scope.selectedRegexPattern;

                    if ((value !== null && value.length === 0) || value === null) {
                        value = "";
                    }

                    viewScope.draft({
                        edit: true,
                        values: {
                            path: fieldPath,
                            fieldId: fieldId,
                            value: [value]
                        }
                    });
                });

            }
        };
    });

    /**
    * Sets a callback function to select2 on('change') event.
    */
    directives.directive('select2NgChange', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element), callback = elem.attr('select2-ng-change');
                elem.on('change', scope[callback]);
            }
        };
    });

    directives.directive('multiselectList', function () {
        return {
            restrict: 'A',
            require : 'ngModel',
            link: function (scope, element, attrs) {
                element.multiselect({
                    buttonClass : 'btn btn-default',
                    buttonWidth : 'auto',
                    buttonContainer : '<div class="btn-group" />',
                    maxHeight : false,
                    numberDisplayed: 3,
                    buttonText : function(options) {
                        if (options.length === 0) {
                            return scope.msg('mds.form.label.select') + ' <b class="caret"></b>';
                        }
                        else {
                            if (options.length > this.numberDisplayed) {
                                return options.length + ' ' + scope.msg('mds.form.label.selected') + ' <b class="caret"></b>';
                            }
                            else {
                                var selected = '';
                                options.each(function() {
                                    var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
                                    selected += label + ', ';
                                });
                                return selected.substr(0, selected.length - 2) + ' <b class="caret"></b>';
                            }
                        }
                    },
                    onChange: function (optionElement, checked) {
                        optionElement.removeAttr('selected');
                        if (checked) {
                            optionElement.attr('selected', 'selected');
                        }
                        element.change();
                    }
                });

                $("#saveoption" + scope.field.id).on("click", function () {
                    element.multiselect('rebuild');
                });

                scope.$watch(function () {
                    return element[0].length;
                }, function () {
                    element.multiselect('rebuild');
                });

                scope.$watch(attrs.ngModel, function () {
                    element.multiselect('refresh');
                });
            }
        };
    });

    directives.directive('defaultMultiselectList', function (Entities) {
        return {
            restrict: 'A',
            require : 'ngModel',
            link: function (scope, element, attrs, ngModel) {
                var viewScope = findCurrentScope(scope, 'draft'),
                fieldPath = attrs.mdsPath,
                fieldId = attrs.mdsFieldId,
                entity,
                value;
                element.multiselect({
                    buttonClass : 'btn btn-default',
                    buttonWidth : 'auto',
                    buttonContainer : '<div class="btn-group" />',
                    maxHeight : false,
                    numberDisplayed: 3,
                    buttonText : function(options) {
                        if (options.length === 0) {
                            return scope.msg('mds.form.label.select') + ' <b class="caret"></b>';
                        }
                        else {
                            if (options.length > this.numberDisplayed) {
                                return options.length + ' ' + scope.msg('mds.form.label.selected') + ' <b class="caret"></b>';
                            }
                            else {
                                var selected = '';
                                options.each(function() {
                                    var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
                                    selected += label + ', ';
                                });
                                return selected.substr(0, selected.length - 2) + ' <b class="caret"></b>';
                            }
                        }
                    },
                    onChange: function (optionElement, checked) {
                        optionElement.removeAttr('selected');
                        if (checked) {
                            optionElement.attr('selected', 'selected');
                        }

                        if (fieldPath === undefined) {
                            fieldPath = attrs.ngModel;
                            fieldPath = fieldPath.substring(fieldPath.indexOf('.') + 1);
                        }

                        value = ngModel.$modelValue;
                        if ((value !== null && value.length === 0) || value === null) {
                            value = "";
                        }
                        viewScope.draft({
                            edit: true,
                            values: {
                                path: fieldPath,
                                fieldId: fieldId,
                                value: [value]
                            }
                        });

                        element.change();
                    }
                });

                scope.$watch(function () {
                    return element[0].length;
                }, function () {
                    element.multiselect('rebuild');
                });

                element.siblings('div').on('click', function () {
                   element.multiselect('rebuild');
                });

                scope.$watch(attrs.ngModel, function () {
                    element.multiselect('refresh');
                });
            }
        };
    });

    directives.directive('securityList', function () {
        return {
            restrict: 'A',
            require : 'ngModel',
            link: function (scope, element, attrs, ngModel) {

                element.multiselect({
                    buttonClass : 'btn btn-default',
                    buttonWidth : 'auto',
                    buttonContainer : '<div class="btn-group" />',
                    maxHeight : false,
                    numberDisplayed: 3,
                    buttonText : function(options) {
                        if (options.length === 0) {
                            return scope.msg('mds.form.label.select') + ' <b class="caret"></b>';
                        }
                        else {
                            if (options.length > this.numberDisplayed) {
                                return options.length + ' ' + scope.msg('mds.form.label.selected') + ' <b class="caret"></b>';
                            }
                            else {
                                var selected = '';
                                options.each(function() {
                                    var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
                                    selected += label + ', ';
                                });
                                return selected.substr(0, selected.length - 2) + ' <b class="caret"></b>';
                            }
                        }
                    },
                    onChange: function (optionElement, checked) {
                        optionElement.removeAttr('selected');
                        if (checked) {
                            optionElement.attr('selected', 'selected');
                        }

                        element.change();
                    }
                });

                scope.$watch(function () {
                    return element[0].length;
                }, function () {
                    element.multiselect('rebuild');
                });

                element.siblings('div').on('click', function () {
                   element.multiselect('rebuild');
                });

                scope.$watch(attrs.ngModel, function () {
                    element.multiselect('refresh');
                });
            }
        };
    });

    directives.directive('integerValidity', function() {
        var INTEGER_REGEXP = new RegExp('^([-][1-9])?(\\d)*$'),
        TWOZERO_REGEXP = new RegExp('^(0+\\d+)$');
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element), originalValue;
                ctrl.$parsers.unshift(function(viewValue) {
                    if (viewValue === '' || INTEGER_REGEXP.test(viewValue)) {
                        // it is valid
                        ctrl.$setValidity('integer', true);
                        originalValue = viewValue;
                        viewValue = parseFloat(viewValue);
                        if (isNaN(viewValue)) {
                            viewValue = '';
                        }
                        if (TWOZERO_REGEXP.test(originalValue)) {
                            setTimeout(function () {
                                elm.val(viewValue);
                            }, 1000);
                        }
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('integer', false);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('decimalValidity', function() {
        var DECIMAL_REGEXP = new RegExp('^[-]?\\d+(\\.\\d+)?$'),
        TWOZERO_REGEXP = new RegExp('^[-]?0+\\d+(\\.\\d+)?$');
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element), originalValue;
                ctrl.$parsers.unshift(function(viewValue) {
                    if (viewValue === '' || DECIMAL_REGEXP.test(viewValue)) {
                        // it is valid
                        ctrl.$setValidity('decimal', true);
                        originalValue = viewValue;
                        viewValue = parseFloat(viewValue);
                        if (isNaN(viewValue)) {
                            viewValue = '';
                        }
                        if (TWOZERO_REGEXP.test(originalValue)) {
                            setTimeout(function () {
                                elm.val(viewValue);
                            }, 1000);
                        }
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('decimal', false);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('insetValidity', function() {
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                    var inset = attrs.insetValidity,
                    checkInset = function (inset, viewValue) {
                        var result,
                        insetParameters = inset.split(' ');
                        if($.isArray(insetParameters)) {
                            $.each(insetParameters, function (i, val) {
                                if (parseFloat(val) === parseFloat(viewValue)) {
                                    result = true;
                                } else {
                                    result = false;
                                }
                            return (!result);
                            });
                        } else {
                            result = false;
                        }
                    return result;
                    };

                    if (ctrl.$viewValue === '' || inset === '' || checkInset(inset, ctrl.$viewValue)) {
                        ctrl.$setValidity('insetNum', true);
                        return viewValue;
                    } else {
                        ctrl.$setValidity('insetNum', false);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('outsetValidity', function() {
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                    var outset = attrs.outsetValidity,
                    checkOutset = function (outset, viewValue) {
                        var result,
                        outsetParameters = outset.split(' ');
                        if($.isArray(outsetParameters)) {
                            $.each(outsetParameters, function (i, val) {
                                if (parseFloat(val) === parseFloat(viewValue)) {
                                    result = true;
                                } else {
                                    result = false;
                                }
                            return (!result);
                            });
                        } else {
                            result = false;
                        }
                    return result;
                    };

                    if (ctrl.$viewValue === '' || outset === '' || !checkOutset(outset, ctrl.$viewValue)) {
                        ctrl.$setValidity('outsetNum', true);
                        return viewValue;
                    } else {
                        ctrl.$setValidity('outsetNum', false);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('maxValidity', function() {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                    var max = attrs.maxValidity;
                    if (ctrl.$viewValue === '' || max === '' || parseFloat(ctrl.$viewValue) <= parseFloat(max)) {
                        ctrl.$setValidity('max', true);
                        return viewValue;
                    } else {
                        ctrl.$setValidity('max', false);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('minValidity', function() {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                    var min = attrs.minValidity;
                    if (ctrl.$viewValue === '' || min === '' || parseFloat(ctrl.$viewValue) >= parseFloat(min)) {
                        ctrl.$setValidity('min', true);
                        return viewValue;
                    } else {
                        ctrl.$setValidity('min', false);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('periodAmount', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elem = angular.element(element),
                periodSliders = elem.parent().find("#period-slider > div"),
                periodSlider = elem.parent().find("#period-slider"),
                parent = elem.parent(),
                openPeriodModal,
                closePeriodModal,
                year = '0',
                month = '0',
                week = '0',
                day = '0',
                hour = '0',
                minute = '0',
                second = '0',
                sliderMax = {
                    year: 10,
                    month: 24,
                    week: 55,
                    day: 365,
                    hour: 125,
                    minute: 360,
                    second: 360
                },
                compileValueInputs = function (year, month, week, day, hour, minute, second) {
                    var valueInputs = [
                        year.toString( 10 ),
                        month.toString( 10 ),
                        week.toString( 10 ),
                        day.toString( 10 ),
                        hour.toString( 10 ),
                        minute.toString( 10 ),
                        second.toString( 10 )
                    ],
                    valueInputsName = ['Y', 'M', 'W', 'D', 'H', 'M', 'S'];

                    $.each( valueInputs, function( nr, val ) {
                        if (nr < 4 && val !== '0') {
                            valueInputs[ nr ] = val + valueInputsName[ nr ];
                        }
                        if ( (valueInputsName[ nr ] === 'H' || valueInputsName[ nr ] === 'M' || valueInputsName[ nr ] === 'S' ) &&  val !== '0' && nr > 3 ) {
                            valueInputs[ nr ] = val + valueInputsName[ nr ];
                            if (valueInputs[ 4 ].indexOf('T') === -1 && valueInputs[ 5 ].indexOf('T') === -1 && valueInputs[ 6 ].indexOf('T') === -1) {
                                valueInputs[ nr ] = 'T' + val + valueInputsName[ nr ];
                            }
                        }
                        if ( val === '0' ) {
                            valueInputs[ nr ] = '';
                        }
                    });
                    return 'P' + valueInputs.join( "" ).toUpperCase();
                },
                refreshPeriod = function () {
                    var fieldId = elem.attr('mds-field-id'),
                    year = periodSlider.children( "#period-year" ).slider( "value" ),
                    month = periodSlider.children( "#period-month" ).slider( "value" ),
                    week = periodSlider.children( "#period-week" ).slider( "value" ),
                    day = periodSlider.children( "#period-day" ).slider( "value" ),
                    hour = periodSlider.children( "#period-hour" ).slider( "value" ),
                    minute = periodSlider.children( "#period-minute" ).slider( "value" ),
                    second = periodSlider.children( "#period-second" ).slider( "value" ),

                    valueFromInputs = compileValueInputs(year, month, week, day, hour, minute, second);

                    periodSlider.children( "#amount-period-year" ).val( year );
                    periodSlider.children( "#amount-period-month" ).val( month );
                    periodSlider.children( "#amount-period-week" ).val( week );
                    periodSlider.children( "#amount-period-day" ).val( day );
                    periodSlider.children( "#amount-period-hour" ).val( hour );
                    periodSlider.children( "#amount-period-minute" ).val( minute );
                    periodSlider.children( "#amount-period-second" ).val( second );
                    elem.val( valueFromInputs );

                    scope.$apply(function() {
                       ctrl.$setViewValue(valueFromInputs);
                    });
                },
                setParsingPeriod = function () {
                    var valueElement = elem.val(), valueDate, valueTime, fieldId = elem.attr('mds-field-id'),
                    checkValue = function (param) {
                        if(isNaN(param) || param === null || param === '' || param === undefined) {
                            param = '0';
                            return param;
                        } else {
                            return param;
                        }
                    },
                    parseDate = function (valueDate) {
                        if (valueDate.indexOf('Y') !== -1) {
                            year = checkValue(valueDate.slice(0, valueDate.indexOf('Y')).toString( 10 ));
                            valueDate = valueDate.substring(valueDate.indexOf('Y') + 1, valueDate.length);
                        } else {
                            year = '0';
                        }
                        if (valueDate.indexOf('M') !== -1) {
                            month = checkValue(valueDate.slice(0, valueDate.indexOf('M')).toString( 10 ));
                            valueDate = valueDate.substring(valueDate.indexOf('M') + 1, valueDate.length);
                        } else {
                            month = '0';
                        }
                        if (valueDate.indexOf('W') !== -1) {
                            week = checkValue(valueDate.slice(0, valueDate.indexOf('W')).toString( 10 ));
                            valueDate = valueDate.substring(valueDate.indexOf('W') + 1, valueDate.length);
                        } else {
                            week = '0';
                        }
                        if (valueDate.indexOf('D') !== -1) {
                            day = checkValue(valueDate.slice(0, valueDate.indexOf('D')).toString( 10 ));
                        } else {
                            day = '0';
                        }
                    },
                    parseTime = function (valueTime) {
                        if (valueTime.indexOf('H') !== -1) {
                            hour = checkValue(valueTime.slice(0, valueTime.indexOf('H')));
                            valueTime = valueTime.substring(valueTime.indexOf('H') + 1, valueTime.length);
                        } else {
                            hour = '0';
                        }
                        if (valueTime.indexOf('M') !== -1) {
                            minute = checkValue(valueTime.slice(0, valueTime.indexOf('M')));
                            valueTime = valueTime.substring(valueTime.indexOf('M') + 1, valueTime.length);
                        } else {
                            minute = '0';
                        }
                        if (valueTime.indexOf('S') !== -1) {
                            second = checkValue(valueTime.slice(0, valueTime.indexOf('S')));
                            valueTime = valueTime.substring(valueTime.indexOf('S') + 1, valueTime.length);
                        } else {
                            second = '0';
                        }
                    };

                    if (valueElement.indexOf('T') > 0) {
                        valueTime = valueElement.slice(valueElement.indexOf('T') + 1, valueElement.length);
                        parseTime(valueTime);
                        valueDate = valueElement.slice(1, valueElement.indexOf('T'));
                        parseDate(valueDate);
                    } else {
                        valueDate = valueElement.slice(1, valueElement.length);
                        parseDate(valueDate);
                        hour = '0'; minute = '0'; second = '0';
                    }

                    periodSlider.children( "#amount-period-year" ).val( year );
                    periodSlider.children( "#amount-period-month" ).val( month );
                    periodSlider.children( "#amount-period-week" ).val( week );
                    periodSlider.children( "#amount-period-day" ).val( day );
                    periodSlider.children( "#amount-period-hour" ).val( hour );
                    periodSlider.children( "#amount-period-minute" ).val( minute );
                    periodSlider.children( "#amount-period-second" ).val( second );

                    periodSlider.children( "#period-year" ).slider( "value", year);
                    periodSlider.children( "#period-month" ).slider( "value", month);
                    periodSlider.children( "#period-week" ).slider( "value", week);
                    periodSlider.children( "#period-day" ).slider( "value", day);
                    periodSlider.children( "#period-hour" ).slider( "value", hour);
                    periodSlider.children( "#period-minute" ).slider( "value", minute);
                    periodSlider.children( "#period-second" ).slider( "value", second );
                };

                periodSliders.each(function(index) {
                    var getValueSettings, valueName = (this.id);
                    valueName = valueName.substring(valueName.lastIndexOf('-') + 1);
                    getValueSettings = function (param1, param2) {
                        var result, resultVal = '';
                        $.each( param1, function( key, value) {
                            if (key === param2){
                                result = true;
                                resultVal = value;
                            } else {
                                result = false;
                            }
                        return (!result);
                        });
                    return resultVal;
                    };

                    $( this ).empty().slider({
                        value: getValueSettings([year, month, week, day, hour, minute, second], valueName),
                        range: "min",
                        min: 0,
                        max: getValueSettings(sliderMax, valueName),
                        animate: true,
                        orientation: "horizontal",
                        slide: refreshPeriod,
                        change: refreshPeriod
                    });
                    periodSlider.children( "#amount-period-" + valueName ).val( $( this ).slider( "value" ) );
                });

                elem.siblings('button').on('click', function() {
                    setParsingPeriod();
                    parent.children("#periodModal").modal('show');
                });

            }
        };
    });

    directives.directive('periodValidity', function() {
        var PERIOD_REGEXP = /^P([0-9]+Y|)?([0-9]+M|)?([0-9]+W|)?([0-9]+D)?(T([0-9]+H)?([0-9]+M)([0-9]+S)|T([0-9]+H)?([0-9]+M)?([0-9]+S)|T([0-9]+H)?([0-9]+M)([0-9]+S)?|T([0-9]+H)([0-9]+M)([0-9]+S)|T([0-9]+H)([0-9]+M)?([0-9]+S)?|T([0-9]+H)?([0-9]+M)([0-9]+S)?|T([0-9]+H)([0-9]+M)([0-9]+S)|T([0-9]+H)([0-9]+M)([0-9]+S))?$/;
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                    if (viewValue === '' || PERIOD_REGEXP.test(viewValue)) {
                        // it is valid
                        ctrl.$setValidity('period', true);
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('period', false);
                        return undefined;
                    }
                });
            }
        };
    });

    directives.directive('showAddOptionInput', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element),
                showAddOptionInput = elm.siblings('span');

                elm.on('click', function () {
                    showAddOptionInput.removeClass('hidden');
                    showAddOptionInput.children('input').val('');
                });
            }
        };
    });

    directives.directive('addOptionCombobox', function() {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var distinct,
                elm = angular.element(element),
                fieldSettings = scope.field.settings,
                modelValueArray = scope.getComboboxValues(fieldSettings),
                parent = elm.parent();
                distinct = function(mvArray, inputValue) {
                   var result;
                   if ($.inArray(inputValue, mvArray) !== -1 && inputValue !== null) {
                       result = false;
                   } else {
                       result = true;
                   }
                   return result;
                };

                ctrl.$parsers.unshift(function(viewValue) {
                    if (viewValue === '' || distinct(modelValueArray, viewValue)) {
                        ctrl.$setValidity('uniqueValue', true);
                        return viewValue;
                    } else {
                        ctrl.$setValidity('uniqueValue', false);
                        return undefined;
                    }
                });

                elm.siblings('a').on('click', function () {
                    scope.fieldValue = [];
                    if (scope.field !== null && scope.newOptionValue !== undefined  && scope.newOptionValue !== '') {
                        if (scope.field.settings[2].value) { //if multiselect
                            if (scope.field.value !== null) {
                                if (!$.isArray(scope.field.value)) {
                                    scope.fieldValue = $.makeArray(scope.field.value);
                                } else {
                                    angular.forEach(scope.field.value, function(val) {
                                        scope.fieldValue.push(val);
                                    });
                                }
                            } else {
                                scope.fieldValue = [];
                            }
                            scope.fieldValue.push(scope.newOptionValue);
                            scope.field.value = scope.fieldValue;
                        } else {
                            scope.field.value = scope.newOptionValue;
                        }
                        scope.field.settings[0].value.push(scope.newOptionValue);
                        scope.newOptionValue = '';
                        parent.addClass('hidden');
                        elm.resetForm();
                    }
                });
            }
        };
    });

    directives.directive('mdsBasicUpdateMap', function () {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl, ngModel) {
                var elm = angular.element(element),
                viewScope = findCurrentScope(scope, 'draft'),
                fieldMapModel = attrs.mdsPath,
                fieldPath = fieldMapModel,
                fPath = fieldPath.substring(fieldPath.indexOf('.') + 1),
                fieldId = attrs.mdsFieldId,
                fieldMaps,
                value,
                entity,
                keyIndex;

                scope.$watch(attrs.ngModel, function (viewValue) {
                    fieldMaps = scope.getMap(fieldId);
                    value = scope.mapToString(fieldMaps.fieldMap);
                    keyIndex = parseInt(attrs.mdsBasicUpdateMap, 10);
                    var distinct = function(inputValue, mvArray) {
                       var result;
                       if ($.inArray(inputValue, mvArray) !== -1 && inputValue !== null) {
                           result = false;
                       } else {
                           result = true;
                       }
                       return result;
                    },
                    keysList = function () {
                        var resultKeysList = [];
                        angular.forEach(fieldMaps.fieldMap, function (map, index) {
                            if (map !== null && map.key !== undefined && map.key.toString() !== '') {
                                if (index !== keyIndex) {
                                    resultKeysList.push(map.key.toString());
                                }
                            }
                        }, resultKeysList);
                        return resultKeysList;
                    };
                    if ((!elm.parent().parent().find('.has-error').length && elm.hasClass('map-value')) || (viewValue === '' && elm.hasClass('map-key')) || (distinct(viewValue, keysList()) && elm.hasClass('map-key'))) {
                        if ((value !== null && value.length === 0) || value === null) {
                            value = "";
                        }
                        if (scope.field.basic.defaultValue !== value) {
                            scope.field.basic.defaultValue = value;
                            viewScope.draft({
                                edit: true,
                                values: {
                                    path: fPath,
                                    fieldId: fieldId,
                                    value: [value]
                                }
                            });
                        }
                    }
                });
            }
        };
    });

    directives.directive('mdsBasicDeleteMap', function () {
        return {
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element),
                viewScope = findCurrentScope(scope, 'draft'),
                fieldPath = attrs.mdsPath,
                fPath = fieldPath.substring(fieldPath.indexOf('.') + 1),
                fieldId = attrs.mdsFieldId,
                fieldMaps,
                value,
                entity,
                keyIndex;

                elm.on('click', function (viewValue) {
                    keyIndex = parseInt(attrs.mdsBasicDeleteMap, 10);
                    scope.deleteElementMap(fieldId, keyIndex);
                    fieldMaps = scope.getMap(fieldId);
                    value = scope.mapToString(fieldMaps.fieldMap);

                    if ((value !== null && value.length === 0) || value === null) {
                        value = "";
                    }
                    scope.safeApply(function () {
                        scope.field.basic.defaultValue = value;
                    });
                    viewScope.draft({
                        edit: true,
                        values: {
                            path: fPath,
                            fieldId: fieldId,
                            value: [value]
                        }
                    });
                });
            }
        };
    });

    directives.directive('mdsUpdateMap', function () {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl, ngModel) {
                var elm = angular.element(element),
                fieldId = attrs.mdsFieldId,
                fieldMaps,
                value,
                keyIndex,
                keysList,
                distinct;

                scope.$watch(attrs.ngModel, function (viewValue) {
                    keyIndex = parseInt(attrs.mdsUpdateMap, 10);
                    fieldMaps = scope.getMap(fieldId);
                    value = scope.mapToMapObject(fieldMaps.fieldMap);
                    var distinct = function(inputValue, mvArray) {
                       var result;
                       if ($.inArray(inputValue, mvArray) !== -1 && inputValue !== null) {
                           result = false;
                       } else {
                           result = true;
                       }
                       return result;
                    },
                    keysList = function () {
                        var resultKeysList = [];
                        angular.forEach(fieldMaps.fieldMap, function (map, index) {
                            if (map !== null && map.key !== undefined && map.key.toString() !== '') {
                                if (index !== keyIndex) {
                                    resultKeysList.push(map.key.toString());
                                }
                            }
                        }, resultKeysList);
                        return resultKeysList;
                    };
                    if ((elm.parent().parent().find('.has-error').length < 1 && elm.hasClass('map-value')) || (viewValue === '' && elm.hasClass('map-key')) || (distinct(viewValue, keysList()) && elm.hasClass('map-key'))) {
                        if ((value !== null && value.length === 0) || value === null) {
                            value = "";
                        }
                        scope.field.value = value;
                    }
                });

                elm.siblings('a').on('click', function () {
                    if (elm.hasClass('map-key')) {
                        keyIndex = parseInt(attrs.mdsUpdateMap, 10);
                        scope.deleteElementMap(fieldId, keyIndex);
                        fieldMaps = scope.getMap(fieldId);
                        value = scope.mapToMapObject(fieldMaps.fieldMap);
                        if ((value !== null && value.length === 0) || value === null) {
                            value = "";
                        }
                        scope.safeApply(function () {
                            scope.field.value = value;
                        });
                    }
                });
            }
        };
    });

    directives.directive('mapValidation', function () {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl, ngModel) {
                var required = attrs.mapValidation;
                scope.$watch(attrs.ngModel, function (viewValue) {
                    if (required.toString() === 'true') {
                        if (viewValue !== '' || viewValue.toString().trim().length > 0) {
                            ctrl.$setValidity('required', true);
                            return viewValue;
                        } else {
                            ctrl.$setValidity('required', false);
                            return viewValue;
                        }
                    } else {
                        ctrl.$setValidity('required', true);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('patternValidity', function() {
        var PATTERN_REGEXP;
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                PATTERN_REGEXP = new RegExp(attrs.patternValidity);
                    if (ctrl.$viewValue === '' || PATTERN_REGEXP.test(ctrl.$viewValue)) {
                        // it is valid
                        ctrl.$setValidity('pattern', true);
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('pattern', false);
                        return undefined;
                    }
                });
            }
        };
    });

    directives.directive('dateTimeValidity', function() {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element), valueDate, valueTime;

                ctrl.$parsers.unshift(function(viewValue) {
                    valueDate = ctrl.$viewValue.slice(0, 16);
                    if (ctrl.$viewValue.length > 10) {
                        valueTime = ctrl.$viewValue.slice(11, ctrl.$viewValue.length);
                    }
                    if (ctrl.$viewValue === '' || (moment(valueDate,'').isValid() && ($.datepicker.parseTime('HH:mm z', valueTime, '') !== false))) {
                        // it is valid
                        ctrl.$setValidity('datetime', true);
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('datetime', false);
                        return undefined;
                    }
                });
            }
        };
    });

    directives.directive('dateValidity', function() {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element);

                ctrl.$parsers.unshift(function(viewValue) {
                    if (ctrl.$viewValue === '' || moment(ctrl.$viewValue,'').isValid()) {
                        // it is valid
                        ctrl.$setValidity('date', true);
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('date', false);
                        return undefined;
                    }
                });
            }
        };
    });

    directives.directive('timeValidity', function() {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element), valueTime;

                ctrl.$parsers.unshift(function(viewValue) {
                    valueTime = ctrl.$viewValue;
                    if (ctrl.$viewValue === '' || $.datepicker.parseTime('HH:mm', valueTime, '') !== false) {
                        // it is valid
                        ctrl.$setValidity('time', true);
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('time', false);
                        return undefined;
                    }
                });
            }
        };
    });

    directives.directive('mdsBasicDeleteListValue', function () {
        return {
        require: 'ngModel',
            link: function(scope, element, attrs, ctrl, ngModel) {
                var elm = angular.element(element),
                viewScope = findCurrentScope(scope, 'draft'),
                fieldPath = elm.parent().parent().attr('mds-path'),
                fieldId = attrs.mdsFieldId,
                value,
                keyIndex;

                elm.on('click', function (e) {
                    keyIndex = parseInt(attrs.mdsBasicDeleteListValue, 10);
                    value = scope.deleteElementList(ctrl.$viewValue, keyIndex);

                    viewScope.draft({
                        edit: true,
                        values: {
                            path: fieldPath,
                            fieldId: fieldId,
                            value: [value]
                        }
                    });
                });
            }
        };
    });

    directives.directive('defaultFieldNameValid', function() {
        return {
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element),
                fieldName = attrs.defaultFieldNameValid;
                scope.defaultValueValid.push({
                    name: fieldName,
                    valid: true
                });

                scope.$watch(function () {
                    return element[0].classList.length;
                }, function () {
                    var fieldName = attrs.defaultFieldNameValid;
                    if (element.hasClass('has-error') || element.hasClass('ng-invalid')) {
                        scope.setBasicDefaultValueValid(false, fieldName);
                    } else {
                        scope.setBasicDefaultValueValid(true, fieldName);
                    }
                });
            }
        };
    });

    directives.directive('mdsUpdateCriterion', function () {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl, ngModel) {
                var elm = angular.element(element),
                viewScope = findCurrentScope(scope, 'draft'),
                fieldPath = attrs.mdsPath,
                fieldId = attrs.mdsFieldId,
                criterionId = attrs.mdsCriterionId,
                criterionName = attrs.mdsUpdateCriterion,
                value;

                scope.$watch(attrs.ngModel, function (viewValue) {
                    if ((!elm.parent().parent().find('.has-error').length || viewValue === '')
                        && (criterionName === 'mds.field.validation.cannotBeInSet' || criterionName === 'mds.field.validation.mustBeInSet')) {
                        value = scope.getCriterionValues(fieldId, criterionName);
                        if ((value !== null && value.length === 0) || value === null) {
                            value = "";
                        }

                        if (scope.field.validation.criteria[criterionId].value !== value) {
                            scope.field.validation.criteria[criterionId].value = value;
                            viewScope.draft({
                                edit: true,
                                values: {
                                    path: fieldPath,
                                    fieldId: fieldId,
                                    value: [value]
                                }
                            });
                        }
                    }

                });

                elm.siblings('a').on('click', function () {
                    value = scope.deleteValueList(fieldId, criterionName, parseInt(attrs.mdsValueIndex, 10));
                    if (scope.field.validation.criteria[criterionId].value !== value) {
                        scope.safeApply(function () {
                            scope.field.validation.criteria[criterionId].value = value;
                        });

                        viewScope.draft({
                            edit: true,
                            values: {
                                path: fieldPath,
                                fieldId: fieldId,
                                value: [value]
                            }
                        });
                    }
                });
            }
        };
    });

    directives.directive('mdsContentTooltip', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elm = angular.element(element),
                fieldId = attrs.mdsFieldId,
                fieldType = attrs.mdsContentTooltip;

                $(element).popover({
                    placement: 'bottom',
                    trigger: 'hover',
                    html: true,
                    title: scope.msg('mds.info.' + fieldType),
                    content: function () {
                        return $('#content' + fieldId).html();
                    }

                });
            }
        };
    });


}());
