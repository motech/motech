(function () {

    'use strict';

    var mds = angular.module('mds');

    function findCorrentScope(startScope, functionName) {
        var parent = startScope;

        while (!parent[functionName]) {
            parent = parent.$parent;
        }

        return parent;
    }

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
                                .removeClass('icon-caret-right')
                                .addClass('icon-caret-down');
                        },
                        'hide.bs.collapse': function () {
                            elem.find('i')
                                .removeClass('icon-caret-down')
                                .addClass('icon-caret-right');
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
                    return text ? text.split("\n") : [];
                });

                ngModel.$formatters.push(function (array) {
                    return array.join("\n");
                });
            }
        };
    });

    /**
    * Add "Item" functionality of "Connected Lists" control to the element. "Connected Lists Group"
    * is passed as a value of the attribute. If item is selected '.connected-list-item-selected-{group}
    * class is added.
    */
    mds.directive('connectedListTargetItem', function () {
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

    mds.directive('connectedListSourceItem', function () {
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
    mds.directive('connectedListSource', function (Entities) {
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
                            var viewScope = findCorrentScope(scope, 'draft');

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

    /*
    * Add "Target List" functionality of "Connected Lists" control to the element (container).
    * "Connected Lists Group" is passed as a value of the attribute. "onItemsAdd", "onItemsRemove"
    * and "onItemMove" callback functions are registered to handle items adding/removing/sorting.
    */
    mds.directive('connectedListTarget', function (Entities) {
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
                            var viewScope = findCorrentScope(scope, 'draft');

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
    mds.directive('connectedListBtnTo', function (Entities) {
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
                        var viewScope = findCorrentScope(scope, 'draft');

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
    mds.directive('connectedListBtnToAll', function (Entities) {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                angular.element(element).click(function (e) {
                    var sourceContainer = $('.connected-list-source.' + attr.connectWith),
                        targetContainer = $('.connected-list-target.' + attr.connectWith),
                        source = scope[sourceContainer.attr('connected-list-source')],
                        target = scope[targetContainer.attr('connected-list-target')],
                        selectedItems = sourceContainer.children(),
                        viewScope = findCorrentScope(scope, 'draft'),
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
    mds.directive('connectedListBtnFrom', function (Entities) {
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
                        var viewScope = findCorrentScope(scope, 'draft');

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
    mds.directive('connectedListBtnFromAll', function (Entities) {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                angular.element(element).click(function (e) {
                    var sourceContainer = $('.connected-list-source.' + attr.connectWith),
                        targetContainer = $('.connected-list-target.' + attr.connectWith),
                        source = scope[sourceContainer.attr('connected-list-source')],
                        target = scope[targetContainer.attr('connected-list-target')],
                        viewScope = findCorrentScope(scope, 'draft'),
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
    mds.directive('initFilterable', function () {
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
    * Displays entity instances data using jqGrid
    */
    mds.directive('entityInstancesGrid', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element);

                $.ajax({
                    type: "GET",
                    url: "../mds/entities/" + scope.selectedEntity.id + "/entityFields",
                    dataType: "json",
                    success: function (result) {
                        var colModel = [], i;

                        for (i = 0; i < result.length; i += 1) {
                            colModel.push({
                                label: result[i].basic.displayName,
                                name: result[i].basic.name,
                                index: result[i].basic.name,
                                jsonmap: "fields." + i + ".value"
                            });
                        }
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
                                scope.editInstance(id);
                            },
                            shrinkToFit: true,
                            autowidth: true,
                            rownumbers: true,
                            loadonce: false,
                            rowNum: 10,
                            rowList: [10, 20, 50],
                            colModel: colModel,
                            pager: '#' + attrs.entityInstancesGrid,
                            width: '100%',
                            height: 'auto',
                            viewrecords: true,
                            gridComplete: function () {
                                $('#entityInstancesTable').children('div').each(function() {
                                    $(this).find('table').width('100%');
                                });
                                $('#entityInstancesTable').children('div').width('100%');
                                $('.ui-jqgrid-htable').addClass('table-lightblue');
                                $('.ui-jqgrid-btable').addClass("table-lightblue");
                                $('.ui-jqgrid-htable').width('100%');
                                $('.ui-jqgrid-bdiv').width('100%');
                                $('.ui-jqgrid-hdiv').width('100%');
                                $('div.ui-jqgrid-hbox').css({'padding-right':'0'});
                                $('.ui-jqgrid-hbox').width('100%');
                                $('.ui-jqgrid-view').width('100%');
                                $('#t_resourceTable').width('auto');
                                $('.ui-jqgrid-pager').width('100%');
                                $(".jqgfirstrow").addClass("ng-hide");
                                angular.forEach($("select.multiselect")[0], function(field) {
                                    var name = scope.getFieldName(field.label);
                                    if (name) {
                                        if (field.selected){
                                            $("th[id='resourceTable_" + name + "']").show();
                                            $("td[aria-describedby='resourceTable_" + name + "']").show();
                                        } else {
                                            $("th[id='resourceTable_" + name + "']").hide();
                                            $("td[aria-describedby='resourceTable_" + name + "']").hide();
                                        }
                                    }
                                });
                            }
                        });
                        scope.$watch("lookupRefresh", function () {
                            $('#' + attrs.id).jqGrid('setGridParam', {
                                postData: {
                                    fields: JSON.stringify(scope.lookupBy),
                                    lookup: (scope.selectedLookup) ? scope.selectedLookup.lookupName : ""
                                }
                            }).trigger('reloadGrid');
                        });
                    }
                });
            }
        };
    });

    mds.directive('multiselectDropdown', function () {
            return {
                restrict: 'A',
                require : 'ngModel',
                link: function (scope, element, attrs) {
                    var selectAll = scope.msg('mds.btn.selectAll');
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
                            optionElement.removeAttr('selected');
                            if (checked) {
                                optionElement.attr('selected', 'selected');
                            }
                            element.change();

                            $(".jqgfirstrow").addClass("ng-hide");
                            angular.forEach(element[0], function(field) {
                                var name = scope.getFieldName(field.label);
                                if (name) {
                                    if (field.selected){
                                        $("th[id='resourceTable_" + name + "']").show("fast");
                                        $("td[aria-describedby='resourceTable_" + name + "']").show("fast");
                                    } else {
                                        $("th[id='resourceTable_" + name + "']").hide("fast");
                                        $("td[aria-describedby='resourceTable_" + name + "']").hide("fast");
                                    }
                                }
                            });
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
    mds.directive('instanceHistoryGrid', function($compile, $http, $templateCache) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element);

                $.ajax({
                    type: "GET",
                    url: "../mds/instances/" +  scope.instanceId + "/fields",
                    dataType: "json",
                    success: function(result)
                    {
                        var colModel = [], i;

                        colModel.push({
                            name: "",
                            width: 15,
                            formatter: function () {
                                return "<a><i class='icon-refresh icon-large'></i></a>";
                            },
                            sortable: false
                        });

                        for (i=0; i<result.length; i+=1) {
                            if (result[i].basic.displayName === "Date") {
                                colModel.push({
                                    name: result[i].basic.displayName,
                                    index: result[i].basic.displayName,
                                    jsonmap: "fields." + i + ".value"
                                });
                            } else {
                                colModel.push({
                                    name: result[i].basic.displayName,
                                    index: result[i].basic.displayName,
                                    jsonmap: "fields." + i + ".value",
                                    sortable: false
                                });
                            }
                        }

                        elem.jqGrid({
                            url: "../mds/instances/" +  scope.instanceId + "/history",
                            datatype: 'json',
                            jsonReader:{
                                repeatitems:false
                            },
                            prmNames: {
                                sort: 'sortColumn',
                                order: 'sortDirection'
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
                            shrinkToFit: true,
                            autowidth: true,
                            rownumbers: true,
                            rowNum: 10,
                            rowList: [10, 20, 50],
                            colModel: colModel,
                            pager: '#' + attrs.instanceHistoryGrid,
                            width: '100%',
                            height: 'auto',
                            viewrecords: true,
                            gridComplete: function () {
                                $('#instanceHistoryTable').children('div').width('100%');
                                $('.ui-jqgrid-htable').addClass('table-lightblue');
                                $('.ui-jqgrid-btable').addClass("table-lightblue");
                                $('.ui-jqgrid-htable').width('100%');
                                $('.ui-jqgrid-bdiv').width('100%');
                                $('.ui-jqgrid-hdiv').width('100%');
                                $('.ui-jqgrid-hbox').width('100%');
                                $('.ui-jqgrid-view').width('100%');
                                $('#t_historyTable').width('auto');
                                $('.ui-jqgrid-pager').width('100%');
                            }
                        });
                    }
                });
            }
        };
    });

    mds.directive('droppable', function () {
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
    mds.directive('mdsAutoSaveFieldChange', function (Entities) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                var func = attr.mdsAutoSaveFieldChange || 'focusout';

                angular.element(element).on(func, function () {
                    var viewScope = findCorrentScope(scope, 'draft'),
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
    mds.directive('mdsAutoSaveAdvancedChange', function (Entities) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                var func = attr.mdsAutoSaveAdvancedChange || 'focusout';

                angular.element(element).on(func, function () {
                    var viewScope = findCorrentScope(scope, 'draft'),
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

    mds.directive('innerlayout', function() {
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

                // BIND events to hard-coded buttons
                scope.innerLayout.addCloseBtn( "#tbarCloseEast", "east" );
            }
        };
    });

    /**
    * Sets a callback function to select2 on('change') event.
    */
    mds.directive('select2NgChange', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element), callback = elem.attr('select2-ng-change');
                elem.on('change', scope[callback]);
            }
        };
    });

    mds.directive('multiselectList', function () {
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

}());
