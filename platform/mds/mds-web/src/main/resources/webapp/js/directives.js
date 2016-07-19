(function () {

    'use strict';

    var directives = angular.module('data-services.directives', []),
        relationshipFormatter = function(cellValue, options, rowObject) {
            var i, result = '', field;
            for (i = 0; i < rowObject.fields.length; i += 1) {
                if (rowObject.fields[i].name === options.colModel.name) {
                    field = rowObject.fields[i];
                    break;
               }
           }
           if (field && field.displayValue) {
               if (typeof(field.displayValue) === 'string' || field.displayValue instanceof String) {
                   result = field.displayValue;
               } else {
                   angular.forEach(field.displayValue,
                       function (value, key) {
                           if (key) {
                               result = result.concat(value, ", ");
                           }
                       }, result);
                   if (result) {
                       result = result.slice(0, -2);
                   }
               }
           }
           return result;
        },
        textFormatter = function (cellValue, options, rowObject) {
            var val = cellValue,
            TEXT_LENGTH_LIMIT = 40; //limit of characters for display in jqgrid instances if field type is textarea

            if (cellValue !== null && cellValue !== undefined && cellValue.length > TEXT_LENGTH_LIMIT) {
                val = cellValue.substring(0, TEXT_LENGTH_LIMIT);
                val = val + '...';
            }
            return val;
        },
        idFormatter = function (cellValue, options, rowObject) {
            var val = cellValue;
            if (cellValue !== undefined && !isNaN(cellValue) && parseInt(cellValue, 10) < 0) {
                val = '';
            }
            return val;
        },
        stringEscapeFormatter = function (cellValue, options, rowObject) {
            var val = '';
                val = _.escape(cellValue);
            return val;
        },
        mapFormatter = function (cellValue, options, rowObject) {
            var result = '', val = cellValue,
            STRING_LENGTH_LIMIT = 20; //limit of characters for display in jqgrid instances if field type is map
            angular.forEach(cellValue,
                function (value, key) {
                    if (key) {
                        if (key.length > STRING_LENGTH_LIMIT) {
                            key = key.substring(0, STRING_LENGTH_LIMIT);
                            key = key + '...';
                        }
                        if (value && value.length > STRING_LENGTH_LIMIT) {
                            value = value.substring(0, STRING_LENGTH_LIMIT);
                            value = value + '...';
                        }
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

    /*
    * This function checks if the field name is reserved for jqgrid (subgrid, cb, rn)
    * and if true temporarily changes that name.
    */
    function changeIfReservedFieldName(fieldName) {
        if (fieldName === 'cb' || fieldName === 'rn' || fieldName === 'subgrid') {
            return fieldName + '___';
        } else {
            return fieldName;
        }
    }

    /*
    * This function checks if the field name was changed
    * and if true changes this name to the original.
    */
    function backToReservedFieldName(fieldName) {
        if (fieldName === 'cb___' || fieldName === 'rn___' || fieldName === 'subgrid___') {
            var fieldNameLength = fieldName.length;
            return fieldName.substring(0, fieldNameLength - 3);
        } else {
            return fieldName;
        }
    }

    /*
    * This function calculates width parameters
    * for fit jqGrid on the screen.
    */
    function resizeGridWidth(gridId) {
        var intervalWidthResize, tableWidth;
        clearInterval(intervalWidthResize);
        intervalWidthResize = setInterval( function () {
            tableWidth = $('#gbox_' + gridId).parent().width();
            $('#' + gridId).jqGrid("setGridWidth", tableWidth);
            clearInterval(intervalWidthResize);
        }, 200);
    }

    /*
    * This function calculates height parameters
    * for fit jqGrid on the screen.
    */
    function resizeGridHeight(gridId) {
        var intervalHeightResize, gap, tableHeight;
        clearInterval(intervalHeightResize);
        intervalHeightResize = setInterval( function () {
            if ($('.overrideJqgridTable').offset() !== undefined) {
                gap = 1 + $('.overrideJqgridTable').offset().top - $('.inner-center .ui-layout-content').offset().top;
                tableHeight = Math.floor($('.inner-center .ui-layout-content').height() - gap - $('.ui-jqgrid-pager').outerHeight() - $('.ui-jqgrid-hdiv').outerHeight());
                $('#' + gridId).jqGrid("setGridHeight", tableHeight);
                resizeGridWidth(gridId);
            }
            clearInterval(intervalHeightResize);
       }, 250);
    }

    /*
    * This function checks grid width
    * and increase this width if possible.
    */
    function resizeIfNarrow(gridId) {
        var intervalIfNarrowResize;
        setTimeout(function() {
            clearInterval(intervalIfNarrowResize);
        }, 950);
        intervalIfNarrowResize = setInterval( function () {
            if (($('#' + gridId).jqGrid('getGridParam', 'width') - 20) > $('#gbox_' + gridId + ' .ui-jqgrid-btable').width()) {
                $('#' + gridId).jqGrid('setGridWidth', ($('#' + gridId).jqGrid('getGridParam', 'width') - 4), true);
                $('#' + gridId).jqGrid('setGridWidth', $('#inner-center.inner-center').width() - 2, false);
            }
        }, 550);
    }

    /*
    * This function checks the name of field
    * whether is selected for display in the jqGrid
    */
    function isSelectedField(name, selectedFields) {
        var i;
        if (selectedFields) {
            for (i = 0; i < selectedFields.length; i += 1) {
                if (name === selectedFields[i].basic.name) {
                    return true;
                }
            }
        }
        return false;
    }

    function handleGridPagination(pgButton, pager, scope) {
        var newPage = 1, last, newSize;
        if ("user" === pgButton) { //Handle changing page by the page input
            newPage = parseInt(pager.find('input:text').val(), 10); // get new page number
            last = parseInt($(this).getGridParam("lastpage"), 10); // get last page number
            if (newPage > last || newPage === 0) { // check range - if we cross range then stop
                return 'stop';
            }
        } else if ("records" === pgButton) { //Page size change, we must update scope value to avoid wrong page size in the trash screen
            newSize = parseInt(pager.find('select')[0].value, 10);
            scope.entityAdvanced.userPreferences.gridRowsNumber = newSize;
        }
    }

    function buildGridColModel(colModel, fields, scope, removeVersionField, ignoreHideFields) {
        var i, j, cmd, field, skip = false, widthTable;
        widthTable = scope.getColumnsWidth();

        for (i = 0; i < fields.length; i += 1) {
            field = fields[i];
            skip = false;

            // for history and trash we don't generate version field
            if (removeVersionField && field.metadata !== undefined && field.metadata.length > 0) {
                for (j = 0; j < field.metadata.length; j += 1) {
                    if (field.metadata[j].key === "version.field" && field.metadata[j].value === "true") {
                        skip = true;
                        break;
                    }
                }
            }

            if (!skip && !field.nonDisplayable) {
                //if name is reserved for jqgrid need to change field name
                field.basic.name = changeIfReservedFieldName(field.basic.name);

                cmd = {
                   label: field.basic.displayName,
                   name: field.basic.name,
                   index: field.basic.name,
                   jsonmap: "fields." + i + ".value",
                   width: widthTable[field.basic.name]? widthTable[field.basic.name] : 220,
                   hidden: ignoreHideFields? false : !isSelectedField(field.basic.name, scope.selectedFields)
                };

                cmd.formatter = stringEscapeFormatter;

                if (scope.isDateField(field)) {
                    cmd.formatter = 'date';
                    cmd.formatoptions = { newformat: 'Y-m-d'};
                }

                if (scope.isRelationshipField(field)) {
                    // append a formatter for relationships
                    cmd.formatter = relationshipFormatter;
                    cmd.sortable = false;
                }

                if (field.basic.name === 'id') {
                    cmd.formatter = idFormatter;
                }

                if (scope.isTextArea(field.settings)) {
                    cmd.formatter = textFormatter;
                    cmd.classes = 'text';
                }

                if (scope.isMapField(field)) {
                    cmd.formatter = mapFormatter;
                    cmd.sortable = false;
                }

                if (scope.isComboboxField(field)) {
                    cmd.jsonmap = "fields." + i + ".displayValue";
                    if (scope.isMultiSelectCombobox(field)) {
                        cmd.sortable = false;
                    }
                }

                colModel.push(cmd);
            }
        }
    }

    /*
    * This function checks if the next column is last of the jqgrid.
    */
    function isLastNextColumn(colModel, index) {
        var result;
        $.each(colModel, function (i, val) {
            if ((index + 1) < i) {
                if (colModel[i].hidden !== false) {
                    result = true;
                } else {
                    result = false;
                }
            }
            return (result);
        });
        return result;
    }

    function handleColumnResize(tableName, gridId, index, width, scope) {
        var tableWidth, widthNew, widthOrg, colModel = $('#' + gridId).jqGrid('getGridParam','colModel');
        if (colModel.length - 1 === index + 1 || (colModel[index + 1] !== undefined && isLastNextColumn(colModel, index))) {
            widthOrg = colModel[index].widthOrg;
            if (Math.floor(width) > Math.floor(widthOrg)) {
                widthNew = colModel[index + 1].width + Math.floor(width - widthOrg);
                colModel[index + 1].width = widthNew;
                scope.saveColumnWidth(colModel[index + 1].index, widthNew);

                $('.ui-jqgrid-labels > th:eq('+(index + 1)+')').css('width', widthNew);
                $('#' + gridId + ' .jqgfirstrow > td:eq('+(index + 1)+')').css('width', widthNew);
            }
            colModel[index].widthOrg = width;
        }

        scope.saveColumnWidth(colModel[index].index, width);
        tableWidth = $(tableName).width();
        $('#' + gridId).jqGrid("setGridWidth", tableWidth);
    }

    /*
    * This function expand input field if string length is long and when the cursor is focused on the text box,
    * and go back to default size when move out cursor
    */
    directives.directive('extendInput', function($timeout) {
        return {
            restrict : 'A',
            link : function(scope, element, attr) {
                var elem = angular.element(element),
                width = 210,
                duration = 300,
                extraWidth = 550,
                height = 22,
                elemValue,
                lines,
                eventTimer;

                function extendInput(elemInput, event) {
                    elemValue = elemInput[0].value;
                    lines = elemValue.split("\n");

                    if (20 <= elemValue.length || lines.length > 1 || event.keyCode === 13) {  //if more than 20 characters or more than 1 line
                        duration = (event.type !== "keyup") ? 300 : 30;
                        if (lines.length < 10) {
                            height = 34;
                        } else {
                            height = 24;
                        }
                        elemInput.animate({
                            width: extraWidth,
                            height: height * lines.length,
                            overflow: 'auto'
                        }, duration);
                    }
                }

                elem.on({
                    'focusout': function (e) {
                        clearTimeout(eventTimer);
                        eventTimer = setTimeout( function() {
                            elem.animate({
                                 width: width,
                                 height: 34,
                                 overflow: 'hidden'
                            }, duration);
                        }, 100);
                    },
                    'focus': function (e) {
                        clearTimeout(eventTimer);
                        extendInput($(this), e);
                    },
                    'keyup': function (e) {
                        extendInput($(this), e);
                    }
                });

            }
        };
    });

    /**
     * Bring focus on input-box while opening the new entity box
     */

    directives.directive('focusmodal', function() {
        return {
            restrict : 'A',
            link : function(scope, element, attr) {
                var elem = angular.element(element);

            elem.on({
                'shown.bs.modal': function () {
                    elem.find('#inputEntityName').focus();
                }
            });
            }
        };
    });

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
                    $(e.target).siblings('.panel-heading').find('i.fa-caret-right')
                        .removeClass('fa-caret-right')
                        .addClass('fa-caret-down');
                });

                target.on('hide.bs.collapse', function (e) {
                    $(e.target).siblings('.panel-heading').find('i.fa-caret-down')
                        .removeClass('fa-caret-down')
                        .addClass('fa-caret-right');
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
                            .removeClass('fa-caret-right')
                            .addClass('fa-caret-down');
                    },
                    'hide.bs.collapse': function () {
                        elem.find('.panel-icon')
                            .removeClass('fa-caret-down')
                            .addClass('fa-caret-right');
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
    * Add a datetime picker to an element.
    */
    directives.directive('mdsDatetimePicker', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                var isReadOnly = scope.$eval(attr.ngReadonly);
                if(!isReadOnly) {
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

    /**
    * Add a date picker to an element.
    */
    directives.directive('mdsDatePicker', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                var isReadOnly = scope.$eval(attr.ngReadonly);
                if(!isReadOnly) {
                    angular.element(element).datepicker({
                        changeYear: true,
                        showButtonPanel: true,
                        dateFormat: 'yy-mm-dd',
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
                        selectedIndices = [], selectedItems = [];

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
                        viewScope = findCurrentScope(scope, 'draft');

                        scope.safeApply(function () {
                            angular.forEach(source, function(item) {
                                target.push(item);
                            });

                            source.length = 0;

                            sourceContainer.trigger('contentChange', [source]);
                            targetContainer.trigger('contentChange', [target]);
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
                        selectedIndices = [], selectedItems = [];

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

                        scope.safeApply(function () {
                            angular.forEach(target, function(item) {
                                source.push(item);
                            });

                            target.length = 0;

                            sourceContainer.trigger('contentChange', [source]);
                            targetContainer.trigger('contentChange', [target]);
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
                var elm = angular.element(element),
                    singleSelect = (attrs.singleselect === "true");

                scope.wasAllSelected = function () {
                    var i;
                    for (i = 0; i < elm.parent().children().children.length; i += 1) {
                        if ($(elm.parent().children()[i]).children().context.lastChild.data.trim() === "ALL") {
                            if ($(elm.parent().children()[i]).hasClass('active')) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                    return false;
                };

                elm.click(function (e) {
                    if (elm.children().hasClass("fa-check-square-o")) {
                        if (elm.text().trim() !== 'ALL') {
                            if (scope.wasAllSelected()) {
                                elm.parent().children().each(function(i) {
                                    $(elm.parent().children()[i]).children().removeClass('fa-check-square-o');
                                    $(elm.parent().children()[i]).children().addClass("fa-square-o");
                                    $(elm.parent().children()[i]).removeClass('active');
                                });
                                $(this).children().addClass('fa-check-square-o').removeClass('fa-square-o');
                                $(this).addClass('active');
                            } else {
                                $(this).children().removeClass("fa-check-square-o").addClass("fa-square-o");
                                $(this).removeClass("active");
                            }
                        }
                    } else {
                        if (elm.text().trim() === 'ALL') {
                            elm.parent().children().each(function(i) {
                                $(elm.parent().children()[i]).children().removeClass('fa-square-o').addClass("fa-check-square-o");
                                $(elm.parent().children()[i]).addClass('active');
                            });
                        } else {
                            if (singleSelect === true) {
                                elm.parent().children().each(function(i) {
                                    $(elm.parent().children()[i]).children().removeClass('fa-check-square-o');
                                    $(elm.parent().children()[i]).children().addClass("fa-square-o");
                                    $(elm.parent().children()[i]).removeClass('active');
                                });
                            }
                            $(this).children().addClass("fa-check-square-o").removeClass("fa-square-o");
                            $(this).addClass("active");
                        }
                    }
                });
            }
        };
    });

    /**
    * Displays entity instances data using jqGrid
    */
    directives.directive('entityInstancesGrid', function ($rootScope, $timeout) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element), eventResize, eventChange,
                gridId = attrs.id,
                firstLoad = true;

                $.ajax({
                    type: "GET",
                    url: "../mds/entities/" + scope.selectedEntity.id + "/entityFields",
                    dataType: "json",
                    success: function (result) {
                        var colModel = [], i, noSelectedFields = true, spanText,
                        noSelectedFieldsText = scope.msg('mds.dataBrowsing.noSelectedFieldsInfo');

                        buildGridColModel(colModel, result, scope, false, false);

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
                            rowNum: scope.entityAdvanced.userPreferences.gridRowsNumber,
                            onPaging: function (pgButton) {
                                handleGridPagination(pgButton, $(this.p.pager), scope);
                            },
                            jsonReader: {
                                repeatitems: false
                            },
                            prmNames: {
                               sort: 'sortColumn',
                               order: 'sortDirection'
                            },
                            onSelectRow: function (id) {
                                firstLoad = true;
                                scope.editInstance(id, scope.selectedEntity.module, scope.selectedEntity.name);
                            },
                            resizeStop: function (width, index) {
                                handleColumnResize('#entityInstancesTable', gridId, index, width, scope);
                            },
                            loadonce: false,
                            headertitles: true,
                            colModel: colModel,
                            pager: '#' + attrs.entityInstancesGrid,
                            viewrecords: true,
                            autowidth: true,
                            shrinkToFit: false,
                            gridComplete: function () {
                                scope.setDataRetrievalError(false);
                                spanText = $('<span>').addClass('ui-jqgrid-status-label ui-jqgrid ui-widget hidden');
                                spanText.append(noSelectedFieldsText).css({padding: '3px 15px'});
                                $('#entityInstancesTable .ui-paging-info').append(spanText);
                                $('.ui-jqgrid-status-label').addClass('hidden');
                                $('#pageInstancesTable_center').addClass('page_instancesTable_center');
                                if (scope.selectedFields !== undefined && scope.selectedFields.length > 0) {
                                    noSelectedFields = false;
                                } else {
                                    noSelectedFields = true;
                                    $('#pageInstancesTable_center').hide();
                                    $('#entityInstancesTable .ui-jqgrid-status-label').removeClass('hidden');
                                }
                                if ($('#instancesTable').getGridParam('records') > 0) {
                                    $('#pageInstancesTable_center').show();
                                    $('#entityInstancesTable .ui-jqgrid-hdiv').show();
                                    $('#gbox_' + gridId + ' .jqgfirstrow').css('height','0');
                                } else {
                                    if (noSelectedFields) {
                                        $('#pageInstancesTable_center').hide();
                                        $('#entityInstancesTable .ui-jqgrid-hdiv').hide();
                                    }
                                    $('#gbox_' + gridId + ' .jqgfirstrow').css('height','1px');
                                }
                                $('#entityInstancesTable .ui-jqgrid-hdiv').addClass("table-lightblue");
                                $('#entityInstancesTable .ui-jqgrid-btable').addClass("table-lightblue");
                                $timeout(function() {
                                    resizeGridHeight(gridId);
                                    resizeGridWidth(gridId);
                                }, 550);
                                if (firstLoad) {
                                    resizeIfNarrow(gridId);
                                    firstLoad = false;
                                }
                            },
                            loadError: function(e) {
                                scope.setDataRetrievalError(true, e.responseText);
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

                        elem.on('jqGridSortCol', function (e, fieldName) {
                            // For correct sorting in jqgrid we need to convert back to the original name
                            e.target.p.sortname = backToReservedFieldName(fieldName);
                        });

                        $(window).on('resize', function() {
                            clearTimeout(eventResize);
                            eventResize = $timeout(function() {
                                $(".ui-layout-content").scrollTop(0);
                                resizeGridWidth(gridId);
                                resizeGridHeight(gridId);
                            }, 200);
                        }).trigger('resize');

                        $('#inner-center').on('change', function() {
                            clearTimeout(eventChange);
                            eventChange = $timeout(function() {
                                resizeGridHeight(gridId);
                                resizeGridWidth(gridId);
                            }, 200);
                        });
                    }
                });
            }
        };
    });

    /**
    * Displays related instances data using jqGrid
    */
    directives.directive('entityInstancesBrowserGrid', function ($timeout, $http, LoadingModal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var tableWidth, relatedEntityId, relatedClass, selectedEntityNested,
                elem = angular.element(element),
                gridId = attrs.id,
                showGrid = function () {
                    $.ajax({
                        type: "GET",
                        url: "../mds/entities/" + relatedEntityId + "/entityFields",
                        dataType: "json",
                        success: function (result) {
                            var colMd, colModel = [], i, spanText;

                            buildGridColModel(colModel, result, scope, false, true);

                            elem.jqGrid({
                                url: "../mds/entities/" + relatedEntityId + "/instances",
                                headers: {
                                    'Accept': 'application/x-www-form-urlencoded',
                                    'Content-Type': 'application/x-www-form-urlencoded'
                                },
                                datatype: 'json',
                                mtype: "POST",
                                postData: {
                                    fields: JSON.stringify(scope.lookupBy),
                                    filter: (scope.filterBy) ? JSON.stringify(scope.filterBy) : ""
                                },
                                jsonReader: {
                                    repeatitems: false
                                },
                                prmNames: {
                                   sort: 'sortColumn',
                                   order: 'sortDirection'
                                },
                                onSelectRow: function (id) {
                                    if (scope.relatedMode.isNested) {
                                        scope.addRelatedInstance(id, selectedEntityNested, scope.editedField);
                                    } else {
                                        scope.addRelatedInstance(id, scope.selectedEntity, scope.editedField);
                                    }
                                },
                                resizeStop: function (width, index) {
                                    var widthNew, widthOrg, colModel = $('#' + gridId).jqGrid('getGridParam','colModel');
                                    if (colModel.length - 1 === index + 1 || (colModel[index + 1] !== undefined && isLastNextColumn(colModel, index))) {
                                        widthOrg = colModel[index].widthOrg;
                                        if (Math.floor(width) > Math.floor(widthOrg)) {
                                            widthNew = colModel[index + 1].width + Math.floor(width - widthOrg);
                                            colModel[index + 1].width = widthNew;

                                            $('.ui-jqgrid-labels > th:eq('+(index + 1)+')').css('width', widthNew);
                                            $('#' + gridId + ' .jqgfirstrow > td:eq('+(index + 1)+')').css('width', widthNew);
                                        }
                                        colModel[index].widthOrg = width;
                                    }
                                    tableWidth = $('#instanceBrowserTable').width();
                                    $('#gview_' + gridId + ' .ui-jqgrid-htable').width(tableWidth);
                                    $('#gview_' + gridId + ' .ui-jqgrid-btable').width(tableWidth);
                                },
                                loadonce: false,
                                headertitles: true,
                                colModel: colModel,
                                pager: '#' + attrs.entityInstancesBrowserGrid,
                                viewrecords: true,
                                autowidth: true,
                                shrinkToFit: false,
                                gridComplete: function () {
                                    $('#' + attrs.entityInstancesBrowserGrid + '_center').addClass('page_instancesTable_center');
                                    if ($('#' + gridId).getGridParam('records') > 0) {
                                        $('#' + attrs.entityInstancesBrowserGrid + '_center').show();
                                        $('#gbox_' + gridId + ' .jqgfirstrow').css('height','0');
                                    } else {
                                        $('#gbox_' + gridId + ' .jqgfirstrow').css('height','1px');
                                    }
                                    tableWidth = $('#instanceBrowserTable').width();
                                    $('#gbox_' + gridId).css('width','100%');
                                    $('#gview_' + gridId).css('width','100%');
                                    $('#gview_' + gridId + ' .ui-jqgrid-htable').addClass("table-lightblue");
                                    $('#gview_' + gridId + ' .ui-jqgrid-btable').addClass("table-lightblue");
                                    $('#gview_' + gridId + ' .ui-jqgrid-htable').width(tableWidth);
                                    $('#gview_' + gridId + ' .ui-jqgrid-btable').width(tableWidth);
                                    $('#gview_' + gridId + ' .ui-jqgrid-bdiv').width('100%');
                                    $('#gview_' + gridId + ' .ui-jqgrid-hdiv').width('100%').show();
                                    $('#gview_' + gridId + ' .ui-jqgrid-view').width('100%');
                                    $('#gbox_' + gridId + ' .ui-jqgrid-pager').width('100%');
                                }
                            });
                        }
                    });
                };

                if (scope.relatedEntity !== undefined && scope.relatedMode.isNested !== true) {
                    relatedEntityId = scope.relatedEntity.id;
                    showGrid();
                } else if (scope.relatedMode.isNested) {
                    relatedClass = scope.getRelatedClass(scope.field);
                    if (relatedClass !== undefined && scope.relatedMode.isNested) {
                        LoadingModal.open();
                        $http.get('../mds/entities/getEntityByClassName?entityClassName=' + relatedClass).success(function (data) {
                            relatedEntityId = data.id;
                            LoadingModal.close();
                            showGrid();
                            if (scope.currentRelationRecord !== undefined) {
                                selectedEntityNested = {id: scope.currentRelationRecord.entitySchemaId};
                            }
                        });
                    }
                }

                scope.$watch("instanceBrowserRefresh", function () {
                    elem.jqGrid('setGridParam', {
                        page: 1,
                        postData: {
                            fields: JSON.stringify(scope.lookupBy),
                            lookup: (scope.selectedLookup) ? scope.selectedLookup.lookupName : "",
                            filter: (scope.filterBy) ? JSON.stringify(scope.filterBy) : ""
                        }
                    }).trigger('reloadGrid');
                });
                elem.on('jqGridSortCol', function (e, fieldName) {
                    // For correct sorting in jqgrid we need to convert back to the original name
                    e.target.p.sortname = backToReservedFieldName(fieldName);
                });
            }

        };
    });


    /**
    * Displays related instances data using jqGrid
    */
    directives.directive('entityRelationsGrid', function ($timeout, $http, MDSUtils, ModalFactory, LoadingModal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var tableWidth, isHiddenGrid, eventResize, eventChange, relatedClass, relatedEntityId, updatePostData, postdata,
                    filter = {removedIds: [], addedIds: [], addedNewRecords: []},
                    elem = angular.element(element),
                    gridId = attrs.id,
                    gridRecords = 0,
                    fieldId = attrs.fieldId,
                    gridHide = attrs.gridHide,
                    pagerHide = attrs.pagerHide,
                    selectedEntityId = scope.selectedEntity.id,
                    selectedInstance = (scope.selectedInstance !== undefined && angular.isNumber(parseInt(scope.selectedInstance, 10)))? parseInt(scope.selectedInstance, 10) : undefined;

                relatedClass = scope.getRelatedClass(scope.field);
                    LoadingModal.open();
                    $http.get('../mds/entities/getEntityByClassName?entityClassName=' + relatedClass).success(function (data) {
                        scope.relatedEntity = data;
                        relatedEntityId = data.id;
                        LoadingModal.close();
                        $.ajax({
                            type: "GET",
                            url: "../mds/entities/" + scope.relatedEntity.id + "/entityFields",
                            dataType: "json",
                            success: function (result) {
                                var colModel = [], i, spanText;

                                if (scope.relatedMode.isNested) {
                                    selectedInstance = scope.editedInstanceId;
                                    if (scope.currentRelationRecord !== undefined) {
                                        selectedEntityId = scope.currentRelationRecord.entitySchemaId;
                                    }
                                } else {
                                    colModel.push({
                                        name: scope.msg('mds.form.label.action').toUpperCase(),
                                        width: 150,
                                        align: 'center',
                                        title:  false,
                                        frozen: true,
                                        hidden:  scope.field.nonEditable,
                                        formatter: function (array, options, data) {
                                            return "<button class='btn btn-default btn-xs btn-danger-hover removeRelatedInstance' "
                                            + " title='" + scope.msg('mds.dataBrowsing.removeRelatedInstance')
                                            + "'><i class='fa fa-fw fa-trash-o'></i>"
                                            + scope.msg('mds.dataBrowsing.remove') + "</button>";
                                        },
                                        sortable: false
                                    });
                                    selectedEntityId = scope.selectedEntity.id;
                                }

                                buildGridColModel(colModel, result, scope, false, true);

                                elem.jqGrid({
                                    url: "../mds/instances/" + selectedEntityId + "/instance/" + ((selectedInstance !== undefined)? selectedInstance : "new") + "/" + scope.field.name,
                                    headers: {
                                        'Accept': 'application/x-www-form-urlencoded',
                                        'Content-Type': 'application/x-www-form-urlencoded'
                                    },
                                    datatype: 'json',
                                    mtype: "POST",
                                    postData: {
                                        filters: (filter.removedIds !== null) ? JSON.stringify(filter) : ""
                                    },
                                    jsonReader: {
                                        repeatitems: false
                                    },
                                    onSelectRow: function (id, status, e) {
                                        if (!scope.field.nonEditable &&  e.target.getAttribute('class') !== null && (e.target.getAttribute('class').indexOf('removeRelatedInstance') >= 0
                                            || (e.target.tagName ==='I' && e.target.parentElement.getAttribute('class').indexOf('removeRelatedInstance') >= 0))) {

                                            scope.relatedData.removeNewAdded(scope.field, parseInt(id, 10));
                                        } else if (!scope.field.nonEditable && e.target.tagName !=='I' && e.target.tagName !== 'BUTTON' && e.target.tagName ==='TD'
                                           && !(e.target.children[0] !== undefined && e.target.children[0].getAttribute('class').indexOf('removeRelatedInstance') >= 0)
                                           && scope.newRelatedFields === null && !scope.relatedMode.isNested) {
                                            selectedInstance = parseInt(id, 10);
                                            scope.currentRelationRecord = {entitySchemaId: relatedEntityId};
                                            if (scope.field.type.defaultName !== "manyToManyRelationship" && scope.field.type.defaultName !== "oneToManyRelationship" && scope.field.type.defaultName !== "oneToOneRelationship" && selectedInstance > 0) {
                                                scope.editRelatedInstanceOfEntity(scope.relatedData.getRelatedId(scope.field), undefined, scope.field);
                                            } else if (scope.field.type.defaultName !== "manyToManyRelationship" && scope.field.type.defaultName !== "oneToManyRelationship" && selectedInstance < 0) {
                                                scope.editRelatedInstanceOfEntity(selectedInstance, undefined, scope.field);
                                            } else {
                                                scope.editRelatedInstanceOfEntity(selectedInstance, relatedEntityId, scope.field);
                                            }
                                       }
                                    },
                                    ondblClickRow : function(id,iRow,iCol,e) {

                                    },
                                    resizeStop: function (width, index) {
                                        var widthNew, widthOrg, colModel = $('#' + gridId).jqGrid('getGridParam','colModel');
                                        if (colModel.length - 1 === index + 1 || (colModel[index + 1] !== undefined && isLastNextColumn(colModel, index))) {
                                            widthOrg = colModel[index].widthOrg;
                                            widthNew = colModel[index + 1].width + Math.abs(widthOrg - width);
                                            colModel[index + 1].width = widthNew;
                                            colModel[index].width = width;

                                            $('.ui-jqgrid-labels > th:eq('+(index + 1)+')').css('width', widthNew);
                                            $('#' + gridId + ' .jqgfirstrow > td:eq('+(index + 1)+')').css('width', widthNew);
                                        }
                                        tableWidth = $('#instance_' + gridId).width();
                                        $('#' + gridId).jqGrid("setGridWidth", tableWidth);
                                    },
                                    loadonce: false,
                                    headertitles: true,
                                    colModel: colModel,
                                    pager: '#' + attrs.entityRelationsGrid,
                                    viewrecords: true,
                                    autowidth: true,
                                    loadui: 'disable',
                                    shrinkToFit: false,
                                    gridComplete: function () {
                                        $('#' + attrs.entityRelationsGrid + '_center').addClass('page_' + gridId + '_center');
                                        gridRecords = $('#' + gridId).getGridParam('records');
                                        if (gridRecords > 0) {
                                            if (gridHide !== undefined && gridHide === "true") {
                                                $('body #instance_' + gridId).removeClass('hiddengrid');
                                            }
                                            $('#relationsTableTotalRecords_' + fieldId).text(gridRecords + '  ' + scope.msg('mds.field.items'));
                                            $('#' + attrs.entityRelationsGrid + '_center').show();
                                            $('#gbox_' + gridId + ' .jqgfirstrow').css("height","0");
                                        } else {
                                            $('#relationsTableTotalRecords_' + fieldId).text('0  ' + scope.msg('mds.field.items'));
                                            $('#' + attrs.entityRelationsGrid + '_center').hide();
                                            $('#gbox_' + gridId + ' .jqgfirstrow').css("height","1px");
                                            if (gridHide !== undefined && gridHide === "true") {
                                                $('body #instance_' + gridId).addClass('hiddengrid');
                                            }
                                        }
                                        $('#gview_' + gridId + ' .ui-jqgrid-hdiv').show();
                                        $('#gview_' + gridId + ' .ui-jqgrid-hdiv').addClass("table-lightblue");
                                        $('#gview_' + gridId + ' .ui-jqgrid-btable').addClass("table-lightblue");
                                        $timeout(function() {
                                            resizeGridWidth(gridId);
                                        }, 250);
                                    }
                                }).jqGrid('setFrozenColumns');
                                if (pagerHide === "true") {
                                    $('#' + attrs.entityRelationsGrid).addClass('hidden');
                                }
                                if (gridHide !== undefined && gridHide === "true") {
                                    $('body #instance_' + gridId).addClass('hiddengrid');
                                }
                            }
                        });
                    }).error(function (response) {
                        LoadingModal.close();
                        ModalFactory.showErrorAlertWithResponse('mds.error.cannotAddRelatedInstance', 'mds.error', response);
                    });

                elem.on('jqGridSortCol', function (e, fieldName) {
                    // For correct sorting in jqgrid we need to convert back to the original name
                    e.target.p.sortname = backToReservedFieldName(fieldName);
                });

                updatePostData = function (filter, postdata) {
                    $('#' + attrs.id).jqGrid('setGridParam', { postData: { filters: ''} });
                    postdata = $('#' + attrs.id).jqGrid('getGridParam','postData');
                    $.extend(postdata, { filters: angular.toJson(filter) });
                    $('#' + attrs.id).jqGrid('setGridParam', { search: true, postData: postdata });
                    $timeout(function() {
                        $('#' + attrs.id).jqGrid().trigger("reloadGrid");
                    }, 300);
                };

                scope.$watch('field.value.removedIds', function (newValue) {
                    postdata = $('#' + attrs.id).jqGrid('getGridParam','postData');

                    if (postdata !== undefined) {
                        if (postdata.filters !== undefined) {
                            filter = JSON.parse(postdata.filters);
                        }
                        if (newValue !== null) {
                            filter.removedIds = newValue;
                        }
                        updatePostData(filter, postdata);
                    }
                }, true);

                scope.$watch('field.value.addedIds', function (newValue) {
                    postdata = $('#' + attrs.id).jqGrid('getGridParam','postData');

                    if (postdata !== undefined) {
                        if (postdata.filters !== undefined) {
                            filter = JSON.parse(postdata.filters);
                        }
                        if (newValue !== null) {
                            filter.addedIds = newValue;
                        }
                        updatePostData(filter, postdata);
                    }
                }, true);

                scope.$watch('field.value.addedNewRecords', function (newValue) {
                    postdata = $('#' + attrs.id).jqGrid('getGridParam','postData');

                    if (postdata !== undefined) {
                        if (postdata.filters !== undefined) {
                            filter = JSON.parse(postdata.filters);
                        }
                        if (newValue !== null) {
                            filter.addedNewRecords = newValue;
                        }
                        updatePostData(filter, postdata);
                    }
                }, true);

                $(window).on('resize', function() {
                    clearTimeout(eventResize);
                    eventResize = $timeout(function() {
                        $(".ui-layout-content").scrollTop(0);
                        resizeGridWidth(gridId);
                    }, 200);
                }).trigger('resize');

                $('#inner-center').on('change', function() {
                    clearTimeout(eventChange);
                    eventChange = $timeout(function() {
                        resizeGridWidth(gridId);
                    }, 200);
                });
            }

        };
    });

    directives.directive('relatedfieldsform', function () {
        return {
            restrict: 'A',
            scope: false,
            require: 'ngModel',
            replace: true,
            link: function (scope, element, attrs, ngModel) {
                scope.$parent.$watch(attrs.ngModel, function (newValue, oldValue, scope) {
                    scope.fields = newValue;
                    if (scope.newRelatedFields !== undefined  && scope.newRelatedFields !== null) {
                        scope.fields = scope.newRelatedFields;
                    }
                });
            },
            templateUrl: '../mds/resources/partials/widgets/entityInstanceFieldsRelated.html'
        };
    });

    directives.directive('editrelatedfieldsform', function () {
        return {
            restrict: 'A',
            scope: false,
            require: 'ngModel',
            replace: true,
            link: function(scope, element, attrs, ngModel) {
                scope.$parent.$watch(attrs.ngModel, function () {
                    scope.fields = scope.editRelatedFields;
                    if (scope.editRelatedFields !== undefined  && scope.editRelatedFields !== null) {
                        scope.fields = scope.editRelatedFields;
                    }
                });
            },
            templateUrl: '../mds/resources/partials/widgets/entityInstanceFieldsRelated.html'
        };
    });

    directives.directive('showRelationsGrid', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var isHiddenGrid,
                elem = angular.element(element),
                gridId = attrs.showRelationsGrid,
                gridHide = attrs.gridHide,
                setHiddenGrid = function () {
                    elem.children().removeClass('fa-angle-double-up');
                    elem.children().addClass('fa-angle-double-down');
                    $('#' + gridId).jqGrid('setGridState','hidden');
                    $('body #instance_' + gridId).addClass('hiddengrid');
                },
                setVisibleGrid = function () {
                    elem.children().removeClass('fa-angle-double-down');
                    elem.children().addClass('fa-angle-double-up');
                    $('#' + gridId).jqGrid('setGridState','visible');
                    $('body #instance_' + gridId).removeClass('hiddengrid').delay(500).fadeIn("slow");
                };

                elem.on('click', function () {
                    isHiddenGrid = $('#' + gridId).jqGrid('getGridParam','hiddengrid');
                    $('#' + gridId).jqGrid('setGridParam', { hiddengrid: !isHiddenGrid });
                    if (isHiddenGrid) {
                        setVisibleGrid();
                    } else {
                        setHiddenGrid();
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
                                    optionElement.prop('selected', true);
                                }
                            }

                            element.change();

                            if (optionElement) {
                                var name = scope.getFieldName(optionElement.text());
                                // don't act for fields show automatically in trash and history
                                if (scope.autoDisplayFields.indexOf(name) === -1) {
                                    scope.addFieldForDataBrowser(name, checked);
                                }
                            } else {
                                scope.addFieldsForDataBrowser(checked);
                            }

                            noSelectedFields = true;
                            angular.forEach(element[0], function(field) {
                                var name = scope.getFieldName(field.label);
                                if (name) {
                                    // Change this name if it is reserved for jqgrid.
                                    name = changeIfReservedFieldName(name);
                                    if (field.selected){
                                        $("#" + target).jqGrid('showCol', name);
                                        noSelectedFields = false;
                                    } else {
                                        $("#" + target).jqGrid('hideCol', name);
                                    }
                                }
                            });

                            if (noSelectedFields) {
                                $('.page_' + target + '_center').hide();
                                $('.ui-jqgrid-status-label').removeClass('hidden');
                                $('.ui-jqgrid-hdiv').hide();
                            } else {
                                $('.page_' + target + '_center').show();
                                $('.ui-jqgrid-status-label').addClass('hidden');
                                $('.ui-jqgrid-hdiv').show();
                            }
                        },
                        onDropdownHide: function(event) {
                            $("#" + target).trigger("resize");
                        }
                    });

                    scope.$watch(function () {
                        return element[0].length;
                    }, function () {
                        element.multiselect('rebuild');
                    });

                    $(element).parent().on("click", function () {
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
    directives.directive('instanceHistoryGrid', function($compile, $http, $templateCache, $timeout) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), eventResize, eventChange,
                gridId = attrs.id,
                firstLoad = true;

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
                                return "<a><i class='fa fa-lg fa-refresh'></i></a>";
                            },
                            sortable: false
                        });

                        buildGridColModel(colModel, result, scope, true, false);

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
                            rowNum: scope.entityAdvanced.userPreferences.gridRowsNumber,
                            onPaging: function (pgButton) {
                                handleGridPagination(pgButton, $(this.p.pager), scope);
                            },
                            resizeStop: function (width, index) {
                                handleColumnResize('#instanceHistoryTable', gridId, index, width, scope);
                            },
                            headertitles: true,
                            colModel: colModel,
                            pager: '#' + attrs.instanceHistoryGrid,
                            viewrecords: true,
                            autowidth: true,
                            shrinkToFit: false,
                            gridComplete: function () {
                                spanText = $('<span>').addClass('ui-jqgrid-status-label ui-jqgrid ui-widget hidden');
                                spanText.append(noSelectedFieldsText).css({padding: '3px 15px'});
                                $('#instanceHistoryTable .ui-paging-info').append(spanText);
                                $('.ui-jqgrid-status-label').addClass('hidden');
                                $('#pageInstanceHistoryTable_center').addClass('page_historyTable_center');
                                if (scope.selectedFields !== undefined && scope.selectedFields.length > 0) {
                                    noSelectedFields = false;
                                } else {
                                    noSelectedFields = true;
                                    $('#pageInstanceHistoryTable_center').hide();
                                    $('#instanceHistoryTable .ui-jqgrid-status-label').removeClass('hidden');
                                }
                                if ($('#historyTable').getGridParam('records') > 0) {
                                    $('#pageInstanceHistoryTable_center').show();
                                    $('#instanceHistoryTable .ui-jqgrid-hdiv').show();
                                    $('#gbox_' + gridId + ' .jqgfirstrow').css('height','0');
                                } else {
                                    if (noSelectedFields) {
                                        $('#pageInstanceHistoryTable_center').hide();
                                        $('#instanceHistoryTable .ui-jqgrid-hdiv').hide();
                                    }
                                    $('#gbox_' + gridId + ' .jqgfirstrow').css('height','1px');
                                }
                                $('#instanceHistoryTable .ui-jqgrid-hdiv').addClass('table-lightblue');
                                $('#instanceHistoryTable .ui-jqgrid-btable').addClass("table-lightblue");
                                $timeout(function() {
                                    resizeGridHeight(gridId);
                                    resizeGridWidth(gridId);
                                }, 550);
                                if (firstLoad) {
                                    resizeIfNarrow(gridId);
                                    firstLoad = false;
                                }
                            }
                        });

                        elem.on('jqGridSortCol', function (e, fieldName) {
                            e.target.p.sortname = backToReservedFieldName(fieldName);
                        });

                        $(window).on('resize', function() {
                            clearTimeout(eventResize);
                            eventResize = $timeout(function() {
                                $(".ui-layout-content").scrollTop(0);
                                resizeGridWidth(gridId);
                                resizeGridHeight(gridId);
                            }, 200);
                        }).trigger('resize');

                        $('#inner-center').on('change', function() {
                            clearTimeout(eventChange);
                            eventChange = $timeout(function() {
                                resizeGridHeight(gridId);
                                resizeGridWidth(gridId);
                            }, 200);
                        });

                    }
                });
            }
        };
    });

    /**
    * Displays entity instance trash data using jqGrid
    */
    directives.directive('instanceTrashGrid', function ($timeout) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element), eventResize, eventChange,
                gridId = attrs.id,
                firstLoad = true;

                $.ajax({
                    type: "GET",
                    url: "../mds/entities/" + scope.selectedEntity.id + "/entityFields",
                    dataType: "json",
                    success: function (result) {
                        var colModel = [], i, noSelectedFields = true, spanText,
                        noSelectedFieldsText = scope.msg('mds.dataBrowsing.noSelectedFieldsInfo');

                        buildGridColModel(colModel, result, scope, true, false);

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
                            rowNum: scope.entityAdvanced.userPreferences.gridRowsNumber,
                            onPaging: function (pgButton) {
                                handleGridPagination(pgButton, $(this.p.pager), scope);
                            },
                            onSelectRow: function (id) {
                                firstLoad = true;
                                scope.trashInstance(id);
                            },
                            resizeStop: function (width, index) {
                                handleColumnResize('#instanceTrashTable', gridId, index, width, scope);
                            },
                            loadonce: false,
                            headertitles: true,
                            colModel: colModel,
                            pager: '#' + attrs.instanceTrashGrid,
                            viewrecords: true,
                            autowidth: true,
                            shrinkToFit: false,
                            gridComplete: function () {
                                spanText = $('<span>').addClass('ui-jqgrid-status-label ui-jqgrid ui-widget hidden');
                                spanText.append(noSelectedFieldsText).css({padding: '3px 15px'});
                                $('#instanceTrashTable .ui-paging-info').append(spanText);
                                $('.ui-jqgrid-status-label').addClass('hidden');
                                $('#pageInstanceTrashTable_center').addClass('page_trashTable_center');
                                if (scope.selectedFields !== undefined && scope.selectedFields.length > 0) {
                                    noSelectedFields = false;
                                } else {
                                    noSelectedFields = true;
                                    $('#pageInstanceTrashTable_center').hide();
                                    $('#instanceTrashTable .ui-jqgrid-status-label').removeClass('hidden');
                                }
                                if ($('#trashTable').getGridParam('records') > 0) {
                                    $('#pageInstanceTrashTable_center').show();
                                    $('#instanceTrashTable .ui-jqgrid-hdiv').show();
                                    $('#gbox_' + gridId + ' .jqgfirstrow').css('height','0');
                                } else {
                                    if (noSelectedFields) {
                                        $('#pageInstanceTrashTable_center').hide();
                                        $('#instanceTrashTable .ui-jqgrid-hdiv').hide();
                                    }
                                    $('#gbox_' + gridId + ' .jqgfirstrow').css('height','1px');
                                }
                                $('#instanceTrashTable .ui-jqgrid-hdiv').addClass("table-lightblue");
                                $('#instanceTrashTable .ui-jqgrid-btable').addClass("table-lightblue");
                                $timeout(function() {
                                    resizeGridHeight(gridId);
                                    resizeGridWidth(gridId);
                                }, 550);
                                if (firstLoad) {
                                    resizeIfNarrow(gridId);
                                    firstLoad = false;
                                }
                            }
                        });

                        elem.on('jqGridSortCol', function (e, fieldName) {
                            // For correct sorting in jqgrid we need to convert back to the original name
                            e.target.p.sortname = backToReservedFieldName(fieldName);
                        });

                        $(window).on('resize', function() {
                            clearTimeout(eventResize);
                            eventResize = $timeout(function() {
                                $(".ui-layout-content").scrollTop(0);
                                resizeGridWidth(gridId);
                                resizeGridHeight(gridId);
                            }, 200);
                        }).trigger('resize');

                        $('#inner-center').on('change', function() {
                            clearTimeout(eventChange);
                            eventChange = $timeout(function() {
                                resizeGridHeight(gridId);
                                resizeGridWidth(gridId);
                            }, 200);
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

                    value = ngModel.$modelValue;

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
                var fieldSettings = scope.field.settings,
                    comboboxValues = scope.getComboboxValues(fieldSettings),
                    typeField = attrs.multiselectList;
                element.multiselect({
                    buttonClass : 'btn btn-default',
                    buttonWidth : 'auto',
                    buttonContainer : '<div class="btn-group" />',
                    maxHeight : false,
                    numberDisplayed: 3,
                    buttonText : function(options) {
                        if (options.length === 0) {
                            return scope.msg('mds.form.label.select');
                        }
                        else {
                            if (options.length > this.numberDisplayed) {
                                return options.length + ' ' + scope.msg('mds.form.label.selected');
                            }
                            else {
                                var selected = '';
                                options.each(function() {
                                    var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
                                    selected += label + ', ';
                                });
                                selected = selected.substr(0, selected.length - 2);
                                return (selected === '') ? scope.msg('mds.form.label.select'): selected;
                            }
                        }
                    },
                    onChange: function (optionElement, checked) {
                        optionElement.removeAttr('selected');
                        if (checked) {
                            optionElement.prop('selected', true);
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
                    if (typeField === 'owner') {
                        element.multiselect('rebuild');
                    } else {
                        var comboboxValues = scope.getComboboxValues(scope.field.settings);
                        if (comboboxValues !== null && comboboxValues !== undefined) {
                            if (comboboxValues.length > 0 && comboboxValues[0] !== '') {
                                element.multiselect('enable');
                            } else {
                                element.multiselect('disable');
                            }
                            element.multiselect('rebuild');
                        }
                    }
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
                var entity, value, resetDefaultValue, checkIfNeedReset,
                viewScope = findCurrentScope(scope, 'draft'),
                fieldPath = attrs.mdsPath,
                fieldId = attrs.mdsFieldId,
                typeField = attrs.defaultMultiselectList;

                element.multiselect({
                    buttonClass : 'btn btn-default',
                    buttonWidth : 'auto',
                    buttonContainer : '<div class="btn-group" />',
                    maxHeight : false,
                    numberDisplayed: 3,
                    buttonText : function(options) {
                        if (options.length === 0) {
                            return scope.msg('mds.form.label.select');
                        }
                        else {
                            if (options.length > this.numberDisplayed) {
                                return options.length + ' ' + scope.msg('mds.form.label.selected');
                            }
                            else {
                                var selected = '';
                                options.each(function() {
                                    var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
                                    selected += label + ', ';
                                });
                                selected = selected.substr(0, selected.length - 2);
                                return (selected === '') ? scope.msg('mds.form.label.select')  : selected;
                            }
                        }
                    },
                    onChange: function (optionElement, checked) {
                        optionElement.removeAttr('selected');
                        if (checked) {
                            optionElement.prop('selected', true);
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

                scope.$watch("field.settings[0].value", function( newValue, oldValue ) {
                    if (newValue !== oldValue) {
                        var includeSelectedValues = function (newList, selectedValues) {
                            var result,
                            valueOnList = function (theList, val) {
                                if (_.contains(theList, val)) {
                                    result = true;
                                } else {
                                    result = false;
                                }
                                return (result);
                            };

                            if(selectedValues !== null && selectedValues !== undefined && $.isArray(selectedValues) && selectedValues.length > 0) {
                                $.each(selectedValues, function (i, val) {
                                    return (valueOnList(newList, val));
                                });
                            } else if ($.isArray(newList) && selectedValues !== null && selectedValues !== undefined && selectedValues.length > 0) {
                                return (valueOnList(newList, selectedValues));
                            } else {
                                result = true;
                            }
                            return result;
                        };

                        if (!includeSelectedValues(newValue, ngModel.$viewValue)) {
                            resetDefaultValue();
                        }

                        if (scope.field.settings[0].value !== null && (scope.field.settings[0].value.length > 0 && scope.field.settings[0].value[0].toString().trim().length > 0)) {
                            element.multiselect('enable');
                        } else {
                            element.multiselect('disable');
                        }
                    }
                }, true);

                resetDefaultValue = function () {

                    fieldId = attrs.mdsFieldId;
                    value = '';

                    viewScope.draft({
                        edit: true,
                        values: {
                            path: "basic.defaultValue",
                            fieldId: fieldId,
                            value: [value]
                        }
                    });

                    scope.field.basic.defaultValue = '';
                    $('#reset-default-value-combobox' + scope.field.id).fadeIn("slow");

                    setTimeout(function () {
                        $('#reset-default-value-combobox' + scope.field.id).fadeOut("slow");
                    }, 8000);

                    element.multiselect('updateButtonText');
                    element.children('option').each(function() {
                        $(this).prop('selected', false);
                    });
                    element.multiselect('refresh');

                };

                checkIfNeedReset = function () {
                    return scope.field.basic.defaultValue !== null
                        && scope.field.basic.defaultValue.length > 0
                        && scope.field.basic.defaultValue !== '';
                };

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

                $("#mdsfieldsettings_" + scope.field.id + '_1').on("click", function () {
                    if (checkIfNeedReset()) {
                        resetDefaultValue();
                    }
                });

                $("#mdsfieldsettings_" + scope.field.id + '_2').on("click", function () {
                    if (checkIfNeedReset()) {
                        resetDefaultValue();
                    }
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
                    buttonContainer : '<div class="btn-group pull-left" />',
                    maxHeight : false,
                    numberDisplayed: 3,
                    buttonText : function(options) {
                        if (options.length === 0) {
                            return scope.msg('mds.form.label.select');
                        }
                        else {
                            if (options.length > this.numberDisplayed) {
                                return options.length + ' ' + scope.msg('mds.form.label.selected');
                            }
                            else {
                                var selected = '';
                                options.each(function() {
                                    var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
                                    selected += label + ', ';
                                });
                                return selected.substr(0, selected.length - 2);
                            }
                        }
                    },
                    onChange: function (optionElement, checked) {
                        optionElement.removeAttr('selected');
                        if (checked) {
                            optionElement.prop('selected', true);
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
                        viewValue = parseInt(viewValue, 10);
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

    directives.directive('shortValidity', function() {
        var INTEGER_REGEXP = new RegExp('^([-][1-9])?(\\d)*$'),
        TWOZERO_REGEXP = new RegExp('^(0+\\d+)$');
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element), originalValue;
                ctrl.$parsers.unshift(function(viewValue) {
                    if (viewValue === '' || INTEGER_REGEXP.test(viewValue)) {
                        // it is valid
                        ctrl.$setValidity('short', true);
                        originalValue = viewValue;
                        viewValue = parseInt(viewValue, 10);
                        if (viewValue >= 32767 || viewValue <= -32768) {
                            ctrl.$setValidity('short', false);
                        }
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
                        ctrl.$setValidity('short', false);
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

    directives.directive('floatValidity', function() {
        var FLOAT_REGEXP = new RegExp('^[-]?\\d+(\\.\\d+)?$'),
        TWOZERO_REGEXP = new RegExp('^[-]?0+\\d+(\\.\\d+)?$');
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element), originalValue;
                ctrl.$parsers.unshift(function(viewValue) {
                    if (viewValue === '' || FLOAT_REGEXP.test(viewValue)) {
                        // it is valid
                        ctrl.$setValidity('float', true);
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
                        ctrl.$setValidity('float', false);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('charValidity', function() {
        var CHAR_REGEXP = new RegExp('^.$');
        return {
        require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element), originalValue;
                ctrl.$parsers.unshift(function(viewValue) {
                    if(viewValue === '' || CHAR_REGEXP.test(viewValue)) {
                        ctrl.$setValidity('char', true);
                        originalValue = viewValue;
                        return viewValue;
                    }
                    else {
                        ctrl.$setValidity('char', false);
                        return viewValue;
                    }
                });
            }
        };
    });
    
    directives.directive('uuidValidity', function() {
        var UUID_REGEXP = new RegExp('^[0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}$');
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elm = angular.element(element), originalValue;
                ctrl.$parsers.unshift(function(viewValue) {
                    if(viewValue === '' || UUID_REGEXP.test(viewValue)) {
                        ctrl.$setValidity('uuid', true);
                        return viewValue;
                    }
                    else {
                        ctrl.$setValidity('uuid', false);
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
                    var inset = '',
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
                    if (scope.field.validation.criteria[attrs.insetValidity] !== undefined && scope.field.validation.criteria[attrs.insetValidity].enabled) {
                        inset = scope.field.validation.criteria[attrs.insetValidity].value;
                    }

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
                    var outset = '',
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
                    if (scope.field.validation.criteria[attrs.outsetValidity] !== undefined && scope.field.validation.criteria[attrs.outsetValidity].enabled) {
                        outset = scope.field.validation.criteria[attrs.outsetValidity].value;
                    }
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
                    var max = '';
                    if (scope.field.validation.criteria[attrs.maxValidity] !== undefined && scope.field.validation.criteria[attrs.maxValidity].enabled) {
                        max = scope.field.validation.criteria[attrs.maxValidity].value;
                    }
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
                    var min = '';
                    if (scope.field.validation.criteria[attrs.minValidity] !== undefined && scope.field.validation.criteria[attrs.minValidity].enabled) {
                            min = scope.field.validation.criteria[attrs.minValidity].value;
                        }
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

    directives.directive('illegalValueValidity', function() {
        var RESERVED_WORDS = [
            'abstract',
            'assert',
            'boolean',
            'break',
            'byte',
            'case',
            'catch',
            'char',
            'class',
            'const*',
            'continue',
            'default',
            'do',
            'double',
            'else',
            'enum',
            'extends',
            'false',
            'final',
            'finally',
            'float',
            'for',
            'goto*',
            'if',
            'int',
            'interface',
            'instanceof',
            'implements',
            'import',
            'long',
            'native',
            'new',
            'null',
            'package',
            'private',
            'protected',
            'public',
            'return',
            'short',
            'static',
            'strictfp',
            'super',
            'synchronized',
            'switch',
            'synchronized',
            'this',
            'throw',
            'throws',
            'transient',
            'true',
            'try',
            'void',
            'volatile',
            'while'
        ],
        LEGAL_REGEXP = /^[\w]+$/;
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var validateReservedWords;

                validateReservedWords = function (viewValue) {
                    if (ctrl.$viewValue === '' || attrs.illegalValueValidity === 'true' || (LEGAL_REGEXP.test(ctrl.$viewValue) && $.inArray(ctrl.$viewValue, RESERVED_WORDS) === -1) ) {
                        // it is valid
                        ctrl.$setValidity('illegalvalue', true);
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('illegalvalue', false);
                        return '';
                    }
                };

                ctrl.$parsers.unshift(validateReservedWords);

                scope.$watch("field.settings[1].value", function(newValue, oldValue) {
                    if (newValue !== oldValue) {
                        ctrl.$setViewValue(ctrl.$viewValue);
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
                    if (attrs.patternValidity !== undefined && scope.field.validation.criteria[attrs.patternValidity].enabled) {
                        PATTERN_REGEXP = new RegExp(scope.field.validation.criteria[attrs.patternValidity].value);
                    } else {
                        PATTERN_REGEXP = new RegExp('');
                    }
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

    directives.directive('mdsIndeterminate', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attributes) {
                scope.$watch(attributes.mdsIndeterminate, function (value) {
                    element.prop('indeterminate', !!value);
                });
            }
        };
    });

    directives.directive('mdsVisitedInput', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl) {
                var elm = angular.element(element),
                fieldId = attrs.mdsFieldId,
                fieldName = attrs.mdsFieldName,
                typingTimer,
                ngFormNameAttrSuffix = "form";

                elm.on('keyup', function () {
                    scope.$apply(function () {
                        elm.siblings('#visited-hint-' + fieldId).addClass('hidden');
                        if (scope[fieldName + ngFormNameAttrSuffix] !== undefined) {
                            scope[fieldName + ngFormNameAttrSuffix].$dirty = false;
                        }
                    });
                    clearTimeout(typingTimer);
                    typingTimer = setTimeout( function() {
                        elm.siblings('#visited-hint-' + fieldId).removeClass('hidden');
                        scope.$apply(function () {
                            if (scope[fieldName + ngFormNameAttrSuffix] !== undefined) {
                                scope[fieldName + ngFormNameAttrSuffix].$dirty = true;
                            }
                        });
                    }, 1500);
                });

                elm.on("blur", function() {
                    scope.$apply(function () {
                        elm.siblings('#visited-hint-' + fieldId).removeClass('hidden');
                        if (scope[fieldName + ngFormNameAttrSuffix] !== undefined) {
                            scope[fieldName + ngFormNameAttrSuffix].$dirty = true;
                        }
                    });
                });
            }
        };
    });

    directives.directive('mdsFileChanged', function () {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.bind('change', function(e){
                    scope.$apply(function(){
                        scope[attrs.mdsFileChanged](e.target.files[0]);
                    });
                });
            }
        };
    });

    directives.directive('tabLayoutWithMdsGrid', ['$http', '$templateCache', '$compile', function($http, $templateCache, $compile) {
        return function(scope, element, attrs) {
            $http.get('../mds/resources/partials/tabLayoutWithMdsGrid.html', { cache: $templateCache }).success(function(response) {
                var contents = element.html(response).contents();
                element.replaceWith($compile(contents)(scope));
            });
        };
    }]);

    directives.directive('embeddedMdsFilters', function($http, $templateCache, $compile) {
        return function(scope, element, attrs) {
            $http.get('../mds/resources/partials/embeddedMdsFilters.html', { cache: $templateCache }).success(function(response) {
                var contents = element.html(response).contents();
                $compile(contents)(scope);
            });
        };
    });

    directives.directive('preventNameConflicts', function($compile) {
        return {
            restrict: 'A',
            priority: 10000,
            terminal: true,
            link: function(scope, element, attrs) {
                attrs.$set('name', attrs.name + 'form');
                attrs.$set('preventNameConflicts', null);
                $compile(element)(scope);
            }
        };
    });
}());

