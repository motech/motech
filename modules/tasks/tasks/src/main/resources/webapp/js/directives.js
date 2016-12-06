(function () {
    'use strict';

    /* Directives */

    var directives = angular.module('tasks.directives', []);

    directives.directive('taskHistoryGrid', function($compile, $http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                try {
                    if (typeof($('#outsideTaskHistoryTable')[0].grid) !== 'undefined') {
                        return;
                    }
                }
                catch (e) {
                    return;
                }

                var elem = angular.element(element), k, rows, activity, message, date, stackTraceElement, fields, messageToShow,
                activityId, url, taskId, taskExists;

                if (scope.taskId) {
                    url = '../tasks/api/activity/' + scope.taskId;
                } else {
                    url = '../tasks/api/activity/all';
                }

                elem.jqGrid({
                    url: url,
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false
                    },
                    colModel: [{
                        name: 'task',
                        index: 'task',
                        sortable: true,
                        sorttype: 'text',
                        width: 25,
                        align: 'center'
                    }, {
                        name: 'date',
                        formatter: function (value) {
                            return moment(parseInt(value, 10)).fromNow();
                        },
                        index: 'date',
                        sortable: true,
                        width: 35
                    }, {
                        name: 'activityType',
                        index: 'activityType',
                        sortable: false,
                        width: 15,
                        align: 'center',
                        title: false
                    }, {
                        name: 'message',
                        index: 'message',
                        sortable: false,
                        width: 155
                    }, {
                        name: 'retry',
                        index: 'retry',
                        sortable: false,
                        align: 'center',
                        width: 15
                    }, {
                       name: 'stackTraceElement',
                       index: 'stackTraceElement',
                       sortable: false,
                       hidden: true
                    }, {
                       name: 'fields',
                       index: 'fields',
                       sortable: false,
                       hidden: true
                    }, {
                       name: 'id',
                       index: 'id',
                       sortable: false,
                       hidden: true
                    }],
                    pager: '#' + attrs.taskHistoryGrid,
                    viewrecords: true,
                    gridComplete: function () {
                        elem.jqGrid('setLabel', 'task', scope.msg('task.subsection.taskName'));
                        elem.jqGrid('setLabel', 'date', scope.msg('task.subsection.information'));
                        elem.jqGrid('setLabel', 'activityType', scope.msg('task.subsection.status'));
                        elem.jqGrid('setLabel', 'message', scope.msg('task.subsection.message'));
                        elem.jqGrid('setLabel', 'retry', 'Retry');

                        $('#outsideTaskHistoryTable').children('div').css('width','100%');
                        $('.ui-jqgrid-htable').addClass("table-lightblue");
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        $('.ui-jqgrid-htable').width('100%');
                        $('.ui-jqgrid-bdiv').width('100%');
                        $('.ui-jqgrid-hdiv').width('100%');
                        $('.ui-jqgrid-view').width('100%');
                        $('#t_taskHistoryTable').width('auto');
                        $('.ui-jqgrid-pager').width('100%');
                        $('.ui-jqgrid-hbox').css({'padding-right':'0'});
                        $('.ui-jqgrid-hbox').width('100%');
                        $('#outsideTaskHistoryTable').children('div').each(function() {
                            $(this).find('table').width('100%');
                        });
                        rows = $("#taskHistoryTable").getDataIDs();
                        scope.failedTasks = [];
                        scope.failedActivitiesWithTaskId = {};
                        for (k = 0; k < rows.length; k+=1) {
                            activity = $("#taskHistoryTable").getCell(rows[k],"activityType").toLowerCase();
                            message = $("#taskHistoryTable").getCell(rows[k],"message");
                            taskId = parseInt($("#taskHistoryTable").getCell(rows[k], "task"), 10);
                            if (activity !== undefined) {
                                if (activity === 'success') {
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType','<div class="recent-activity-icon fa icon-green fa-check-circle fa-2x"></div>','ok',{ },'');
                                } else if (activity === 'warning') {
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType','<div class="recent-activity-task-img fa warning-type fa-question-circle fa-2x"></div>','ok',{ },'');
                                } else if (activity === 'in progress') {
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType','<div class="recent-activity-task-img fa icon-blue fa-info-circle fa-2x"></div>','ok',{ },'');
                                } else if (activity === 'filtered') {
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType','<div class="recent-activity-task-img fa icon-blue fa-info-circle fa-2x"></div>','ok',{ },'');
                                } else if (activity === 'error') {
                                    activityId = $("#taskHistoryTable").getCell(rows[k],"id");
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType',
                                        '<div class="recent-activity-task-img fa icon-red fa-exclamation-circle fa-2x"/>','ok',{ },'');
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'retry',
                                        '&nbsp;&nbsp;<span type="button" class="btn btn-primary btn-xs grid-ng-clickable" ng-click="retryTask(' + activityId + ')">Retry</span>',
                                        'ok',{ },'');
                                    if (!scope.doesTaskExist(taskId)) {
                                        scope.failedActivitiesWithTaskId[activityId] = taskId;
                                        scope.failedTasks.push(activityId);
                                    }
                                }
                            }

                            stackTraceElement = $("#taskHistoryTable").getCell(rows[k],"stackTraceElement");
                            scope.stackTraceEl[k] = stackTraceElement;
                            fields = $("#taskHistoryTable").getCell(rows[k], "fields").split(",");
                            messageToShow = [message].concat(fields);
                            if (message !== undefined && activity === 'error' && stackTraceElement !== undefined && stackTraceElement !== null) {
                                $("#taskHistoryTable").jqGrid('setCell',rows[k],'message',
                                    '<p class="wrap-paragraph">' + scope.msg(messageToShow) + '&nbsp;&nbsp; <span type="button" class="btn btn-primary btn-xs grid-ng-clickable" ng-click="showStackTrace('+ k +')" data-target="#stackTraceElement' +
                                    k + '">'+ scope.msg('task.button.showStackTrace') + '</span></p>',
                                    'ok',{ },'');
                            } else if (message !== undefined) {
                                $("#taskHistoryTable").jqGrid('setCell',rows[k],'message',scope.msg(messageToShow),'ok',{ },'');
                            }
                            $("#taskHistoryTable").jqGrid('setCell', rows[k], 'task', scope.getTaskNameFromId(taskId));
                        }
                    },
                    loadComplete: function() {
                        $compile($('.grid-ng-clickable'))(scope);
                    },
                    sortcolumn: 'task',
                    sortdirection: 'asc'
                });
            }
        };
    });

    directives.directive('taskPanelsResize', function ($window, $timeout) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var windowDimensions = angular.element($window), setTableSize, setListSize, widthList, widthTBody, widthTaskPanel, widthTdRecentInfo, widthListHistory;

                setTableSize = function () {
                    widthTBody = $('#table-activity').width();
                    widthTdRecentInfo = Math.floor(widthTBody - (36 * 4));
                    $('.table-recent-activity  tbody td.recent-info').css({'text-overflow':'ellipsis', 'max-width': widthTdRecentInfo, 'min-width': 30});
                };

                setListSize = function () {
                    widthList = $('#task-list').width();
                    widthTaskPanel = Math.floor(widthList - 388);
                    $('#task-list .task-element.task-long-name').css({'text-overflow':'ellipsis', 'max-width': widthTaskPanel, 'min-width': 100});
                    widthListHistory = $('.history').width();
                    widthTaskPanel = Math.floor(widthListHistory - 300);
                    $('.history .task-element.task-long-name').css({'text-overflow':'ellipsis', 'max-width': widthTaskPanel, 'min-width': 100});
                };

                scope.getWindowDimensions = function () {
                    return {
                        'h': windowDimensions.height(),
                        'w': windowDimensions.width()
                    };
                };

                scope.$watch(scope.getWindowDimensions, function () {
                     $timeout(function() {
                         setTableSize();
                         setListSize();
                     }, 500);
                }, true);

                windowDimensions.on('resize', function () {
                    scope.$apply();
                });

                $('#inner-center').on('change', function() {
                    $timeout(function() {
                        setTableSize();
                        setListSize();
                    }, 250);
                });

                $('#inner-center').trigger("change");
            }
        };
    });


    directives.directive('overflowChange', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $(element).find('.overflowChange').livequery(function () {
                    $(this).on({
                        shown: function (e) {
                            if (!e.target.classList.contains("help-inline")) {
                                $(this).css('overflow', 'visible');
                            }
                        },
                        hide: function (e) {
                            if (!e.target.classList.contains("help-inline")) {
                                $(this).css('overflow', 'hidden');
                            }
                        }
                    });
                });
            }
        };
    });

    directives.directive('expandAccordion', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $('.panel-group').on('show.bs.collapse', function (e) {
                    $(e.target).siblings('.panel-heading').find('.accordion-toggle i.fa-caret-right').removeClass('fa-caret-right').addClass('fa-caret-down');
                    scope.$broadcast('show.task.actions');
                });

                $('.tasks-list').on('show.bs.collapse', function (e) {
                    $(e.target).siblings('.panel-heading').find('.accordion-toggle i.fa-caret-right').removeClass('fa-caret-right').addClass('fa-caret-down');
                });

                $('.panel-group').on('hide.bs.collapse', function (e) {
                    $(e.target).siblings('.panel-heading').find('.accordion-toggle i.fa-caret-down').removeClass('fa-caret-down').addClass('fa-caret-right');
                });

                $('.tasks-list').on('hide.bs.collapse', function (e) {
                    $(e.target).siblings('.panel-heading').find('.accordion-toggle i.fa-caret-down').removeClass('fa-caret-down').addClass('fa-caret-right');
                });
            }
        };
    });

    directives.directive('field', ['ManageTaskUtils', function (ManageTaskUtils) {
        return {
            restrict: 'E',
            replace: false,
            scope:{
                field: "=?",
                editable: "=?",
                boolean: "@?"
            },
            link: function (scope, element, attrs) {
                scope.msg = scope.$parent.taskMsg || scope.$parent.msg;

                if(!scope.field) {
                    scope.field = {};
                }
                if(!scope.field.manipulations || !Array.isArray(scope.field.manipulations)){
                    scope.field.manipulations = [];
                }

                if(scope.field.prefix === ManageTaskUtils.DATA_SOURCE_PREFIX) {
                    if (!scope.field.specifiedParentName) {
                         scope.displayName = "{0}.{1}#{2}.{3}".format(
                             scope.msg(scope.field.providerName),
                             scope.msg(scope.field.serviceName),
                             scope.field.objectId,
                             scope.msg(scope.field.displayName)
                         );
                    } else {
                        scope.displayName = "{0}.{1}".format(
                             scope.msg(scope.field.specifiedParentName),
                             scope.msg(scope.field.displayName)
                        );
                    }

                } else if(scope.field.prefix === ManageTaskUtils.POST_ACTION_PREFIX){
                    if(!scope.field.specifiedParentName) {
                        scope.displayName = "{0}.{1}#{2}.{3}".format(
                            scope.msg(scope.field.channelName),
                            scope.msg(scope.field.actionName),
                            scope.field.objectId,
                            scope.msg(scope.field.displayName)
                        );
                    } else {
                        scope.displayName = "{0}.{1}".format(
                             scope.msg(scope.field.specifiedParentName),
                             scope.msg(scope.field.displayName)
                        );
                    }
                } else if (scope.boolean) {
                    if(scope.boolean === 'true') {
                        scope.displayName = scope.msg("yes");
                        scope.field.prefix = 'boolean';
                    } else if (scope.boolean === 'false') {
                        scope.displayName = scope.msg("no");
                        scope.field.prefix = 'boolean';
                    }
                } else {
                    scope.displayName = scope.msg(scope.field.displayName);
                }
                if(scope.boolean) {
                    element.data('value', scope.boolean);
                } else {
                    element.data('value', scope.field);
                }
                element.attr('contenteditable', false);

                element.click(function (event) {
                    if($(event.target).hasClass("field-remove")){
                        element.remove();
                        scope.$emit('field.changed');
                    }
                });

                if(attrs.draggable !== undefined) {
                    element.draggable({
                        revert: true,
                        start: function () {
                            if (element.hasClass('draggable')) {
                                element.find("div:first-child").popover('hide');
                            }
                        }
                    });
                }
            }
            ,
            templateUrl: '../tasks/partials/field.html'
        };
    }]);

    directives.directive('copyable', ['ManageTaskUtils', function (ManageTaskUtils) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var ctrlDown = false, ctrlKey = 17, cmdKey = 91, cKey = 67;

                function copyFieldKeyToClipboard(field) {
                    var text = "{{" + ManageTaskUtils.formatField(field)[0] + "}}", elementToCopy, selector;

                     elementToCopy = document.createElement('textarea');
                     elementToCopy.id = 'elementToCopy';

                     document.body.appendChild(elementToCopy);

                     elementToCopy.value = text;

                     selector = document.querySelector('#elementToCopy');
                     selector.select();
                     document.execCommand('copy');
                     document.body.removeChild(elementToCopy);
                }

                element.click(function (event) {
                    if ($(event.target).hasClass("field-bubble")) {
                        scope.$parent.setLastSelectedField(element.data("value"));
                        scope.$apply();
                    }
                });


                $(".inside.task.form-horizontal").keydown(function(event) {
                    if (event.keyCode === ctrlKey || event.keyCode === cmdKey) {
                        ctrlDown = true;
                    }
                }).keyup(function(event) {
                    if (event.keyCode === ctrlKey || event.keyCode === cmdKey) {
                        ctrlDown = false;
                    }
                });

                $(".inside.task.form-horizontal").keydown(function(event) {
                    if (ctrlDown && event.keyCode === cKey) {
                        copyFieldKeyToClipboard(scope.$parent.getLastSelectedField());
                    }
                });
            }
        };
    }]);

    directives.directive('integer', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.keypress(function (evt) {
                    var charCode = evt.which || evt.keyCode,
                        allow = [8, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57]; // char code: <Backspace> 0 1 2 3 4 5 6 7 8 9

                    return allow.indexOf(charCode) >= 0;
                });
            }
        };
    });

    directives.directive('double', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.keypress(function (evt) {
                    var charCode = evt.which || evt.keyCode,
                        allow = [8, 46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57]; // char code: <Backspace> . 0 1 2 3 4 5 6 7 8 9

                    return allow.indexOf(charCode) >= 0;
                });
            }
        };
    });

    directives.directive('readOnly', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.keypress(function (evt) {
                    return false;
                });
            }
        };
    });

    directives.directive('contenteditable', function ($compile, $http, $templateCache, ManageTaskUtils, $timeout) {
        var intervalAdjustText;

        function formatField (field) {
            return '{{{0}}}'.format(
                ManageTaskUtils.formatField(field)[0]
                );
        }

        function formatToKey (str, scope) {
            var fetchedField;
            if(str.substring(0,2) === "{{" && str.substring(str.length-2, str.length) === "}}") {
                str = str.substring(2, str.length-2);
                fetchedField = ManageTaskUtils.parseField(str, scope.$parent.getAvailableFields());
                str = formatField(fetchedField);
            }
            return str;
        }

        function isTagForPlaceholder (tag) {
            return (tag.tagName && tag.tagName.toLowerCase() === 'em');
        }
        function readContent (element) {
            var container = $('<div></div>');
            element.contents().each(function() {
                var field, ele = $(this);
                if(this.tagName && this.tagName.toLowerCase() === 'field'){
                    if(ele.attr("boolean")){
                        container.append(ele.attr("boolean"));
                    }else{
                        field = ele.data('value');
                        container.append(formatField(field));
                    }
                } else if (this.tagName && this.tagName.toLowerCase() === 'br') {
                    container.append('\n');
                } else if (!isTagForPlaceholder(this)) {
                    container.append(ele.text());
                }
            });
            return container.text();
        }
        function findField (str){
            var index, startIndex, endIndex;
            startIndex = str.indexOf("{{");
            if(startIndex > -1){
                index = startIndex + 2;
            }
            while(index && (-1 < index < str.length)){
                endIndex = str.substring(index, str.length).indexOf("}}");
                if(endIndex === -1) {
                    return false;
                }
                if(str.substring(index, index + endIndex).indexOf("{{") > -1){
                    index = index + endIndex+2;
                } else {
                    return str.substring(startIndex, index + endIndex + 2);
                }
            }
            return false;
        }
        function parseField (originalStr) {
            var fieldStr, valueArr = [], str = originalStr;
            while(originalStr !== valueArr.join('')){
                if(str.substring(0,2) === "{{"){
                    fieldStr = findField(str);
                    if(!fieldStr){
                        return false;
                    }
                    valueArr.push(fieldStr);
                    str = str.substring(fieldStr.length, str.length);
                } else if (str.indexOf("{{") > -1) {
                    valueArr.push(str.substring(0, str.indexOf("{{")));
                    str = str.substring(str.indexOf("{{"), str.length);
                } else {
                    valueArr.push(str);
                }
            }
            return valueArr;
        }
        function makeFieldElement (str, scope) {
            var fieldScope = scope.$parent.$new(false, scope);
            if(str.substring(0,2) === "{{" && str.substring(str.length-2, str.length) === "}}") {
                str = str.substring(2, str.length-2);
            }
            fieldScope.field = ManageTaskUtils.parseField(str, scope.$parent.getAvailableFields());
            fieldScope.$on('field.changed', function (event) { // Added because scope.$on wasn't catching field.changed event (this might be a 1.2x bug)
                scope.$emit('field.changed');
            });
            return $compile('<field field="field" editable="true" />')(fieldScope);
        }
        function makeBooleanFieldElement (str, scope) {
            var fieldScope = scope.$parent.$new(false, scope);
            fieldScope.$on('field.changed', function (event) { // Added because scope.$on wasn't catching field.changed event (this might be a 1.2x bug)
                scope.$emit('field.changed');
            });
            return $compile('<field boolean="'+str+'" />')(fieldScope);
        }

        function adjustText() {
            var textElements, spanElement, inputElement, i;

            clearInterval(intervalAdjustText);

            intervalAdjustText = setInterval( function () {
                textElements = angular.element(document.getElementsByClassName('text-field-marker'));

                for (i = 0; i < textElements.length; i += 1) {
                    spanElement = $(textElements[i]).parent();
                    inputElement = spanElement.parent().parent();

                    if (inputElement.hasClass('map-input')) {

                        if ($(textElements[i]).hasClass('field-text')) {
                            $(textElements[i]).removeClass('field-text');
                        }

                        if ($(textElements[i]).hasClass('field-text-short')) {
                            $(textElements[i]).removeClass('field-text-short');
                        }

                        if (spanElement.width()>inputElement.width()*0.9) {
                            if ($(textElements[i]).width() > spanElement.width()*0.84) {
                                if ($(textElements[i]).next().is('.badge')) {
                                    $(textElements[i]).addClass('field-text-short');
                                } else if ($(textElements[i]).width() > spanElement.width()*0.9) {
                                    $(textElements[i]).addClass('field-text');
                                }
                            }
                        }
                    }
                }

                clearInterval(intervalAdjustText);
            }, 50);
        }

        function prepareField(field, fullName) {
            switch (field.prefix) {
                case ManageTaskUtils.TRIGGER_PREFIX:
                    return "{{{0}.{1}}}".format(
                        ManageTaskUtils.TRIGGER_PREFIX,
                        field.eventKey
                    );
                case ManageTaskUtils.DATA_SOURCE_PREFIX:
                    if (!field.specifiedParentName || fullName) {
                        return "{{{0}.{1}.{2}#{3}.{4}}}".format(
                            ManageTaskUtils.DATA_SOURCE_PREFIX,
                            field.providerName,
                            field.providerType,
                            field.objectId,
                            field.fieldKey
                        );
                    } else {
                        return "{{{0}.{1}}}".format(
                            field.specifiedParentName,
                            field.fieldKey
                        );
                    }

                case ManageTaskUtils.POST_ACTION_PREFIX:
                    if(!field.specifiedParentName || fullName) {
                        return "{{{0}.{1}.{2}}}".format(
                            ManageTaskUtils.POST_ACTION_PREFIX,
                            field.objectId,
                            field.key
                        );
                    } else {
                        return "{{{0}.{1}}}".format(
                            field.specifiedParentName,
                            field.fieldKey
                        );
                    }
            }
        }

        function getFilteredItems(choices, searchText) {
            var filteredList = [];
            if (searchText.length < 2) {
                return filteredList;
            }
            choices.filter(function (choice) {
                if (prepareField(choice, false).indexOf(searchText) > 0 || prepareField(choice, true).indexOf(searchText) > 0) {
                    filteredList.push(choice);
                }
            });
            return filteredList;
        }

        function findStartIndex(value, endIndex) {
            value = value.substring(0, endIndex);
            return (value.lastIndexOf("}}") > value.lastIndexOf(" ")) ? value.lastIndexOf("}}") + 2: value.lastIndexOf(" ");
        }

        function stopPropagation(evt) {
            evt.cancelBubble = true;
            evt.returnValue = false;
            if (evt.stopPropagation) {
                evt.stopPropagation();
                evt.preventDefault();
            }
        }

        function addNewLine() {
            var selection = window.getSelection(),
                range = selection.getRangeAt(0),
                brNode = document.createElement("br"),
                textNode = document.createTextNode("\u00a0");

            range.deleteContents();
            range.insertNode(brNode);
            range.collapse(false);
            range.insertNode(textNode);
            range.selectNodeContents(textNode);

            selection.removeAllRanges();
            selection.addRange(range);
            return false;
        }

        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, element, attrs, ngModel) {
                scope.debug = scope.$parent.debugging;
                var eventResize,
                    items = [],
                    fetchedAvailableFields = [],
                    fieldType = scope.data.type,
                    url = '../tasks/partials/widgets/autocomplete-fields.html';
                    scope.filteredItems = [];
                    scope.selPos = 0;

                if (!ngModel) {
                    return false;
                }

                if (element.attr('field-autocomplete') === "true") {
                    $http.get(url, {cache: $templateCache})
                    .success(function (html) {
                        var compiledContent = $compile(html)(scope);
                        $(compiledContent).insertBefore(element);
                    });
                }

                scope.$watch(function () {
                    return ngModel.$viewValue;
                }, function() {
                    if (scope.debug) {
                        scope.changeBubble(ngModel, element);
                    } else {
                        adjustText();
                        ngModel.$render();
                    }
                });

                // Disallow enter key being pressed, except on certain data types
                element.on('keypress', function (event) {
                    var type = $(this).data('type');
                    if (type !== 'TEXTAREA' && type !== 'MAP' && type !== 'LIST' && type !== 'PERIOD') {
                        return event.which !== 13;
                    }
                });

                scope.$on('debugging', function(event, args) {
                    scope.debug = args.debug;
                    scope.changeBubble(ngModel, element);
                });

                scope.changeBubble = function (ngModel, element) {
                    if (scope.data && scope.data.type !== 'MAP' && !scope.data.value) {
                        return;
                    }
                    if (scope.data && scope.data.type === 'MAP') {
                        if (element.attr('ng-model') === "pair.value") {
                            element.html(scope.pair.value);
                        }
                        if (element.attr('ng-model') === "pair.key") {
                            element.html(scope.pair.key);
                        }
                    } else {
                         element.html(scope.data.value);
                    }

                    if (!scope.debug) {
                         ngModel.$setViewValue(readContent(element));
                         ngModel.$rollbackViewValue();

                         $timeout(function() {
                            adjustText();
                         }, 0);
                    }
                };

                element.bind('blur', function (event) {
                    event.stopPropagation();
                    if(element[0] !== event.target){
                        return;
                    }
                    ngModel.$setViewValue(readContent(element));
                    scope.$apply();
                    if(scope.debug) {
                        scope.changeBubble(ngModel, element);
                    }
                });

                scope.$on('field.changed', function (event) {
                    event.stopPropagation();
                    ngModel.$setViewValue(readContent(element));
                    scope.$apply();
                    if (scope.debug) {
                        scope.changeBubble(ngModel, element);
                    } else {
                        adjustText();
                    }
                });

                element.on('focus', function () {
                    if (ngModel) {
                        scope.selectedElement = element;
                        scope.selectedModel = ngModel;
                    }
                });

                ngModel.$render = function () {
                    var parsedValue = "", viewValueStr, matches;
                    element.html("");
                    if (!ngModel.$viewValue) {
                        if (scope.debug) {
                            scope.changeBubble(ngModel, element);
                        }
                        return false;
                    }
                    parseField(ngModel.$viewValue).forEach(function(str){
                        if (findField(str)) {
                            str = formatToKey(str, scope);
                            element.append(makeFieldElement(str, scope));

                            $timeout(function() {
                                adjustText();
                             }, 100);

                        } else if (element.data('type') === 'BOOLEAN' && (str === 'true' || str === 'false')) {
                            element.append(makeBooleanFieldElement(str, scope));
                        } else {
                            if (fieldType === 'TEXTAREA' && str.includes('\n')) {
                                var stringValue = str, val;
                                while (stringValue.includes('\n')) {
                                    if(stringValue.indexOf('\n') === 0) {
                                        stringValue = stringValue.substring(stringValue.indexOf('\n')+1);
                                        val = stringValue.substring(0, stringValue.indexOf('\n'));
                                        if (val === '') {
                                            val = stringValue;
                                        }
                                        element.append('<br>');
                                        element.append(val);
                                        stringValue = stringValue.substring(val.length, stringValue.length);
                                    } else if (stringValue.indexOf('\n') > 0) {
                                        val = stringValue.substring(0, stringValue.indexOf('\n'));
                                        element.append(val);
                                        stringValue = stringValue.substring(stringValue.indexOf('\n'), stringValue.length);
                                    } else {
                                        element.append(stringValue);
                                    }
                                }
                            } else {
                                element.append(str);
                            }
                        }
                        parsedValue = parsedValue.concat(str);
                    });

                    if (parsedValue && parsedValue !== scope.data.value && scope.data.type !== 'MAP') {
                        scope.data.value = parsedValue;
                    }
                };

                if (scope.fields) {
                    fetchedAvailableFields = scope.fields; // fields for action
                } else {
                    fetchedAvailableFields = scope.$parent.fields; // fields for datasource
                }

                angular.forEach(fetchedAvailableFields, function (field) {
                   items.push(field);
                });

                scope.getPreparedName = function (fieldToPrepare, fullName) {
                    return prepareField(fieldToPrepare, fullName);
                };

                scope.refreshListItems = function (event) {
                    scope.filteredItems = [];
                    if(event.keyCode === 8) {event.key = "";scope.rangySelection.anchorOffset = scope.rangySelection.anchorOffset -1;}
                    if (scope.rangySelection.anchorNode && scope.rangySelection.anchorNode.nodeValue) {
                        var value = scope.rangySelection.anchorNode.nodeValue;
                        scope.searchText = value.substring(findStartIndex(value.trimRight(), scope.rangySelection.anchorOffset), scope.rangySelection.anchorOffset) + event.key;
                        scope.filteredItems = getFilteredItems(items, scope.searchText.trim());
                        if (scope.selectedElement.parent().find('ul').is('ul.dropdown-menu.open-list')
                            && (scope.selectedElement.attr('ng-model') === "pair.value" || scope.selectedElement.attr('ng-model') === "pair.key")) {
                            scope.selectedElement.parent().find('ul.dropdown-menu.open-list')
                            .css({'margin-left': scope.selectedElement.position().left, 'top': scope.selectedElement.position().top + scope.selectedElement.outerHeight()});
                        }
                    }
                };

                scope.addItem = function (item) {
                    if (!item) {
                        return;
                    }
                    if (scope.rangySelection && scope.rangySelection.anchorNode) {
                        var anchorValue = scope.rangySelection.anchorNode.nodeValue;
                        if (scope.selectedElement[0].contains(scope.rangySelection.anchorNode) && anchorValue) {
                            $(scope.rangySelection.anchorNode).before(
                                anchorValue.substring(0, findStartIndex(anchorValue, scope.rangySelection.anchorOffset))
                                + prepareField(item, scope.debug) + anchorValue.substring(scope.rangySelection.anchorOffset + 1)
                            ).remove();
                        } else {
                            scope.selectedElement.append(prepareField(item, scope.debug));
                        }
                    }
                    scope.selPos = -1;
                    scope.filteredItems = [];
                    scope.searchText = "";
                    scope.selectedModel.$setViewValue(readContent(scope.selectedElement));
                };

                scope.keyPress = function(evt) {

                    switch (evt.keyCode) {
                        case 27: // esc
                            scope.filteredItems = [];
                            break;
                        case 13: // enter
                            if(scope.selPos > -1 && scope.filteredItems.length > 0) {
                                stopPropagation(evt);
                                scope.addItem(scope.filteredItems[scope.selPos]);
                            } else {
                                // Disallow enter key being pressed, except on certain data types
                                if (fieldType !== 'TEXTAREA' && fieldType !== 'MAP' && fieldType !== 'LIST' && fieldType !== 'PERIOD') {
                                        stopPropagation(evt);
                                        return false;
                                    } else {
                                        if (fieldType === 'TEXTAREA') {
                                            evt.preventDefault();
                                            if (window.getSelection) {
                                                addNewLine();
                                            }

                                        }
                                    }
                            }
                            break;
                        case 8: // backspace
                            scope.rangySelection = rangy.getSelection();
                            scope.selPos = 0;
                            scope.refreshListItems(evt);
                            break;
                        case 186: // semicolon
                            if(scope.selPos > -1 && scope.filteredItems.length > 0) {
                                stopPropagation(evt);
                                scope.addItem(scope.filteredItems[scope.selPos]);
                                return;
                            }
                            break;
                        case 188: // coma
                            if(scope.selPos > -1 && scope.filteredItems.length > 0) {
                                stopPropagation(evt);
                                scope.addItem(scope.filteredItems[scope.selPos]);
                            }
                            break;
                        case 38: // up
                            if (scope.selPos > 0) {
                                scope.selPos = scope.selPos - 1;
                                stopPropagation(evt);
                            }
                            break;
                        case 40: // down
                            if (scope.selPos < scope.filteredItems.length-1) {
                                scope.selPos = scope.selPos + 1;
                                stopPropagation(evt);
                            }
                            break;
                        default:
                            scope.rangySelection = rangy.getSelection();
                            scope.selPos = 0;
                            scope.refreshListItems(evt);
                    }
                };

                if(attrs.droppable !== undefined) {
                    element.droppable({
                        drop: function (event, ui) {
                            var field = ui.draggable.data('value'),
                            offset,
                            selection = rangy.getSelection(),
                            anchorText = false;
                            event.stopPropagation();

                            // Test anchorNode existance, because it might not exist
                            if (selection && selection.anchorNode) {
                                anchorText = selection.anchorNode.wholeText;
                            }

                            if (!field) {
                                return false;
                            }
                            field = formatField(field);
                            if (element[0].contains(selection.anchorNode) && anchorText) {
                                $(selection.anchorNode).before(
                                    anchorText.substring(0, selection.anchorOffset)
                                    + field
                                    + anchorText.substring(selection.anchorOffset)
                                ).remove();
                            } else if (!ngModel.$viewValue || ngModel.$viewValue==="") {
                                element.html(field);
                            } else {
                                element.append(field);
                            }
                            ngModel.$setViewValue(readContent(element));
                            scope.$apply();
                            if (scope.debug) {
                                scope.changeBubble(ngModel, element);
                            } else {
                                adjustText();
                            }
                        }
                    });
                }

                $('#inner-center').on('change', function() {
                    clearTimeout(eventResize);
                    eventResize = $timeout(function() {
                        clearInterval(intervalAdjustText);
                        adjustText();
                    }, 100);
                });

                scope.$on('show.task.actions', function () {
                    adjustText();
                });
            }
        };
    });

    directives.directive('editableContent', function ($compile, $timeout, $http, $templateCache) {
        var templateLoader;

        return {
            restrict: 'E',
            replace : true,
            transclude: true,
            scope: {
                'data': '=',
                'index': '@',
                'action': '@',
                'fields': '='
            },
            compile: function (tElement, tAttrs, scope) {
                var url = '../tasks/partials/widgets/content-editable-' + tAttrs.type.toLowerCase() + '.html',

                templateLoader = $http.get(url, {cache: $templateCache})
                    .success(function (html) {
                        tElement.html(html);
                    });

                return function (scope, element, attrs) {
                    templateLoader.then(function () {
                        element.html($compile(tElement.html())(scope));
                    });

                    $timeout(function () {
                        element.find('div').focusout();
                    });
                };
            }
        };
    });

    directives.directive('manipulationModal', function ($compile, BootstrapDialogManager, HelpStringManipulation) {
        return {
            restrict: 'A',
            scope: {
                manipulations: "=",
                manipulationType: "=",
                displayName: "=",
                name: "@"
            },
            link: function (scope, element, attrs) {
                var name = scope.name,
                    displayNameOnly = scope.displayNameOnly;

                if(!scope.manipulationType){
                    return false;
                }
                if (['UNICODE', 'TEXTAREA', 'DATE'].indexOf(scope.manipulationType) === -1){
                    return false;
                }
                if(!scope.manipulations){
                    return false;
                }

                element.on('click', function (event) {
                    var modalScope,
                        parseManipulationsFunction;
                    if (!$(event.target).hasClass('field-remove')) {
                        window.getSelection().removeAllRanges(); // Make sure no text is selected...

                        modalScope = scope.$new(true, scope);
                        modalScope.msg = scope.$parent.msg;
                        modalScope.manipulationType = scope.manipulationType;
                        modalScope.manipulations = [];

                        if(scope.manipulations && Array.isArray(scope.manipulations)) {
                            modalScope.manipulations = jQuery.extend(true, [], scope.manipulations);
                        }
                        scope.importDialog = new BootstrapDialog({
                            closable: false,
                            autodestroy: false,
                            title: function () {
                                switch(scope.manipulationType){
                                    case 'UNICODE':
                                    case 'STRING':
                                        return modalScope.msg('task.stringManipulation');
                                    case 'DATE':
                                    case 'DATE2DATE':
                                        return modalScope.msg('task.dateManipulation');
                                }
                                return null;
                            },
                            message: $compile('<manipulation-sorter type="manipulationType" manipulations="manipulations" />')(modalScope),
                            buttons: [{
                                label: scope.$parent.msg('task.button.save'),
                                cssClass: 'btn-primary',
                                action: function(dialogRef) {
                                    scope.manipulations = jQuery.extend(true, [], modalScope.manipulations);
                                    scope.$emit('field.changed');
                                    BootstrapDialogManager.close(dialogRef);
                                }
                            }, {
                                id: 'task-form-help-modifications',
                                label: scope.$parent.msg('task.help.modifications'),
                                cssClass: 'btn-default pull-left',
                                action: function(dialogRef) {
                                   HelpStringManipulation.open(scope.$parent);
                                }
                            }]
                        });
                        BootstrapDialogManager.open(scope.importDialog);
                    }});
            }
        };
    });

    directives.directive('manipulationSorter', function ($compile, $http, ManageTaskUtils) {
        return {
            restrict: 'EA',
            templateUrl: '../tasks/partials/manipulation-sorter.html',
            scope: {
                type: '=',
                manipulations: '='
            },
            link: function (scope, element, attrs) {
                $('.sortable', element).sortable({
                    placeholder: "ui-state-highlight",
                    update: function (event, ui) {
                        var sorted = $(event.target), manipulations = [];
                        $('.manipulation', sorted).each(function () {
                            manipulations.push({
                                type: $(this).attr('type'),
                                argument: $(this).data('argument')
                            });
                        });
                        scope.manipulations = manipulations;
                        scope.$apply();
                    }
                });
            },
            controller: ['$scope', function ($scope) {
                var manipulationType = $scope.type.toLowerCase();
                $scope.manipulationTypes = [];
                if(['unicode', 'string'].indexOf(manipulationType) > -1) {
                    $scope.manipulationTypes = ['toUpper', 'toLower', 'capitalize', 'URLEncode', 'join', 'split', 'substring', 'format', 'parseDate'];
                }
                if('date' === manipulationType) {
                    $scope.manipulationTypes.push('dateTime');
                }
                if(['date', 'date2date'].indexOf(manipulationType) > -1) {
                    $scope.manipulationTypes = $scope.manipulationTypes.concat(['beginningOfMonth','endOfMonth','plusMonths','minusMonths','plusDays', 'minusDays', 'plusHours', 'minusHours', 'plusMinutes', 'minusMinutes', 'quarter']);
                }

                this.addManipulation = function (type, argument) {
                    if(!argument){
                        argument = "";
                    }
                    $scope.manipulations.push({
                        type: type,
                        argument: argument
                    });
                    $scope.$apply();
                };
                this.removeManipulation = function (manipulationStr) {
                    var obj, manipulations = [], returnVal = false;
                    $scope.manipulations.forEach( function (obj) {
                        if (obj.type !== manipulationStr) {
                            manipulations.push(obj);
                        }
                        if (obj.type === manipulationStr) {
                            returnVal = true;
                        }
                    });
                    $scope.manipulations = manipulations;
                    $scope.$apply();
                    return returnVal;
                };
                this.isActive = function (manipulationStr) {
                    var index;
                    for (index in $scope.manipulations) {
                        if($scope.manipulations.hasOwnProperty(index)){
                            if ($scope.manipulations[index].type === manipulationStr){
                                return true;
                            }
                        }
                    }
                    return false;
                };
            }]
        };
    });

    directives.directive('manipulation', ['$compile', 'ManageTaskUtils', function ($compile, ManageTaskUtils) {
        return {
            restrict : 'EA',
            require: '^manipulationSorter',
            transclude: true,
            replace: true,
            templateUrl: '../tasks/partials/manipulation.html',
            scope: {
                argument: '=?'
            },
            link : function (scope, element, attrs, manipulationSorter) {
                var attributeFieldTemplate, manipulationSettings = {};
                ManageTaskUtils.MANIPULATION_SETTINGS.forEach(function (manipulation) {
                    if (attrs.type === manipulation.name) {
                        manipulationSettings = manipulation;
                    }
                });

                scope.changeArgument = function (newVal) {
                    scope.$apply(function () {
                        scope.argument = newVal;
                    });
                };

                scope.msg = scope.$parent.$parent.$parent.msg;
                scope.type = attrs.type;
                if(attrs.active){
                    scope.active = true;
                    attributeFieldTemplate = false;
                    if ((manipulationSettings.input && manipulationSettings.input !== '') || manipulationSettings.name === 'format') {
                        attributeFieldTemplate = '<input type="text" ng-model="argument" />';
                        if (manipulationSettings.name === 'format') {
                            attributeFieldTemplate = '<format-manipulation-button />';
                        }
                        if (!scope.argument) {
                            scope.argument = "";
                        }
                        element.append($compile(attributeFieldTemplate)(scope));
                    }
                    scope.$watch('argument', function (newValue) {
                        element.data('argument', newValue);
                    });
                    element.on("click", ".remove", function () {
                        manipulationSorter.removeManipulation(scope.type);
                    });
                } else {
                    scope.$watch(function () {
                        return manipulationSorter.isActive(scope.type);
                    }, function (active) {
                        if(active){
                            element.hide();
                        } else {
                            element.show();
                        }
                    });
                    element.on('click', function () {
                        manipulationSorter.addManipulation(scope.type);
                    });
                }
            }
        };
    }]);

    directives.directive('formatManipulationButton', ['$compile', function ($compile) {
        return {
            restrict: 'EA',
            templateUrl: '../tasks/partials/widgets/string-manipulation-format-button.html',
            link: function (scope, element, attrs) {
                var modalScope = scope.$new(),
                    parseFormatInput = function (value) {
                        var arr = [];
                        value.forEach(function (obj) {
                            if (obj.value){
                                arr.push(obj.value);
                            }
                        });
                        return arr.join(",");
                    };
                scope.formatInput = [];

                modalScope.getAvailableFields = scope.$parent.$parent.$parent.$parent.$parent.$parent.$parent.getAvailableFields;
                modalScope.availableFields = scope.$parent.$parent.$parent.$parent.$parent.$parent.$parent.fields;
                modalScope.msg = scope.$parent.$parent.$parent.$parent.$parent.$parent.$parent.taskMsg;
                modalScope.argument = scope.argument;
                scope.importDialog = new BootstrapDialog({
                    size: 'size-wide',
                    title: scope.msg('task.format.set.header'),
                    closable: true,
                    closeByBackdrop: false,
                    closeByKeyboard: false,
                    autodestroy: false,
                    message: $compile('<format-manipulation />')(modalScope),
                    buttons: [{
                        label: scope.msg('task.button.save'),
                        cssClass: 'btn-primary',
                        action: function (dialogItself) {
                            modalScope.argument = parseFormatInput(modalScope.formatInput);
                            scope.changeArgument(modalScope.argument);
                            dialogItself.close();
                        }
                    }]
                });
                element.on('click', function (event) {
                    event.preventDefault();
                    scope.importDialog.open();
                });
            }
        };
    }]);

    directives.directive('formatManipulation', function () {
        return {
            restrict: 'EA',
            templateUrl: '../tasks/partials/widgets/string-manipulation-format.html',
            link: function (scope, el, attrs) {
                var formatContent = function (value) {
                    var parsed = [];
                    value.split(",").forEach(function (str) {
                        parsed.push({ value: str });
                    });
                    return parsed;
                };

                scope.formatInput = formatContent(scope.argument);

                scope.deleteFormatInput = function (index) {
                    scope.formatInput.splice(index, 1);
                };
                scope.addFormatInput = function () {
                    scope.formatInput.push({value: ''});
                };
            }
        };
    });

    directives.directive('datetimePicker', function () {
            return {
                restrict: 'A',
                link: function(scope, element, attrs) {
                    element.click(function () {
                        $(this).prev('input').datetimepicker('show');
                    });
                }
            };
        });

        directives.directive('datetimePickerInput', function () {
            return {
                restrict: 'A',
                link: function (scope, element, attrs) {
                    var parent = scope;

                    while (parent.selectedAction === undefined) {
                        parent = parent.$parent;
                    }

                    element.datetimepicker({
                        showTimezone: true,
                        useLocalTimezone: true,
                        changeMonth: true,
                        changeYear: true,
                        dateFormat: 'yy-mm-dd',
                        timeFormat: 'HH:mm z',
                        yearRange: '-100:+10',
                        showOn: true,
                        constrainInput: false,
                        onSelect: function (dateTex) {
                            parent.filter(parent.selectedAction[$(this).data('action')].actionParameters, {hidden: false})[$(this).data('index')].value = dateTex;
                            parent.$apply();
                        }
                    });
                }
            };
        });

        directives.directive('timePickerInput', function () {
            return {
                restrict: 'A',
                link: function (scope, element, attrs) {
                    var parent = scope;

                    while (parent.selectedAction === undefined) {
                        parent = parent.$parent;
                    }

                    element.datetimepicker({
                        showTimezone: true,
                        timeOnly: true,
                        useLocalTimezone: true,
                        timeFormat: 'HH:mm z',
                        onSelect: function (dateTex) {
                            parent.filter(parent.selectedAction[$(this).data('action')].actionParameters, {hidden: false})[$(this).data('index')].value = dateTex;
                            parent.$apply();
                        }
                    });
                }
            };
        });

        directives.directive('startTimePicker', function() {
            return {
                restrict: 'A',
                require : 'ngModel',
                link : function (scope, element, attrs, ngModelCtrl) {
                    $(function() {
                        element.timepicker({
                            showTimezone: true,
                            useLocalTimezone: true,
                            timeFormat: 'HH:mm z',
                            onSelect: function (time) {
                                ngModelCtrl.$setViewValue(time);
                                scope.$apply(function() {
                                    scope.$parent.startTime = time.toString();
                                });
                            }
                        });
                    });
                }
            };
        });

        directives.directive('endTimePicker', function() {
            return {
                restrict: 'A',
                require : 'ngModel',
                link : function (scope, element, attrs, ngModelCtrl) {
                    $(function() {
                        element.timepicker({
                            showTimezone: true,
                            useLocalTimezone: true,
                            timeFormat: 'HH:mm z',
                            onSelect: function (time) {
                                ngModelCtrl.$setViewValue(time);
                                scope.$apply(function() {
                                    scope.$parent.endTime = time.toString();
                                });
                            }
                        });
                    });
                }
            };
        });

    directives.directive('helpPopover', function($compile, $http) {
        return function(scope, element, attrs) {
            var msgScope = scope;

            while (msgScope.msg === undefined) {
                msgScope = msgScope.$parent;
            }

            $http.get('../tasks/partials/help/' + attrs.helpPopover + '.html').success(function (html) {
                $(element).popover({
                    placement: 'top',
                    trigger: 'hover',
                    html: true,
                    content: function() {
                        var elem = angular.element(html);

                        $compile(elem)(msgScope);
                        msgScope.$apply(elem);

                        return $compile(elem)(msgScope);
                    }
                });
            });
        };
    });

    directives.directive('divPlaceholder', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var parent = scope, curText;

                while (parent.msg === undefined) {
                    parent = parent.$parent;
                }

                curText = parent.msg(attrs.divPlaceholder);

                if (!element.text().trim().length) {
                    element.html('<em style="color: gray;">' + curText + '</em>');
                }

                element.focusin(function() {
                    if ($(this).text().toLowerCase() === curText.toLowerCase() || !$(this).text().length) {
                        $(this).empty();
                    }
                });

                element.focusout(function() {
                    if ($(this).text().toLowerCase() === curText.toLowerCase() || !$(this).text().length) {
                        $(this).html('<em style="color: gray;">' + curText + '</em>');
                    }
                });
            }
        };
    });

    directives.directive('actionSortableCursor', function () {
       return {
           restrict: 'A',
           link: function (scope, element, attrs) {
                angular.element(element).on({
                    mousedown: function () {
                        $(this).css('cursor', 'move');
                    },
                    mouseup: function () {
                        $(this).css('cursor', 'auto');
                    }
                });
           }
       };
    });

    directives.directive('actionsPopover', function () {
       return {
           restrict: 'A',
           link: function (scope, element, attrs) {
                angular.element(element).popover({
                    placement: 'right',
                    trigger: 'hover',
                    html: true,
                    content: function () {
                        var html = angular.element('<div style="text-align: left" />'),
                            actions = (scope.item && scope.item.task && scope.item.task.actions) || scope.actions || [];

                        angular.forEach(actions, function (action) {
                            var div = angular.element('<div />'),
                                img = angular.element('<img />'),
                                name = angular.element('<span style="margin-left: 5px" />');

                            img.attr('src', '../server/module/icon?bundleName=' + action.moduleName + '&defaultIcon=iconTaskChannel.png');
                            img.addClass('task-list-img');

                            name.text(scope.msg(action.channelName) + ": " + scope.msg(action.displayName));

                            div.append(img);
                            div.append(name);

                            html.append(div);
                        });

                        return html;
                    }
                });
           }
       };
    });

    directives.directive('triggerPopover', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                angular.element(element).popover({
                    placement: 'right',
                    trigger: 'hover',
                    html: true,
                    content: function () {
                        var html = angular.element('<div style="text-align: left" />'),
                            div = angular.element('<div />'),
                            img = angular.element('<img />'),
                            name = angular.element('<span style="margin-left: 5px" />');

                        img.attr('src', '../server/module/icon?bundleName=' + scope.item.task.trigger.moduleName + '&defaultIcon=iconTaskChannel.png');
                        img.addClass('task-list-img');
                        name.text(scope.msg(scope.item.task.trigger.channelName) + ": " + scope.msg(scope.item.task.trigger.displayName));
                        div.append(img);
                        div.append(name);
                        html.append(div);

                        return html;
                    }
                });
            }
        };
    });

    directives.directive('taskStopPropagation', function () {
        return function(scope, elem, attrs) {
            elem.on('click', function (e) {
               e.stopPropagation();
            });
        };
    });

    directives.directive('popoverLoader', function ($compile) {
        return {
            restrict: 'A',
            scope: {
                popoverLoader: '=',
                twoLists: '='
            },
            link: function (scope, element, attrs) {
                var maxLength = !scope.twoLists ? 75 : 55;
                if (scope.popoverLoader.length > maxLength) {
                    element.attr('data-trigger', 'hover');
                    element.attr('data-placement', 'bottom');
                    element.attr('bs-popover', scope.popoverLoader);
                    element.html(scope.popoverLoader.substring(0, maxLength - 3) + "...");
                    $(element).popover({
                        content: function () {
                            return scope.popoverLoader;
                        }
                    });
                } else {
                    element.html(scope.popoverLoader);
                }
            }
        };
    });

    directives.directive('periodAmountTasks', function() {
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
                    var year = periodSlider.children( "#period-year" ).slider( "value" ),
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
                    var valueElement = elem.val(), valueDate, valueTime,
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

    directives.directive('importTaskModal', ['$compile', '$http', '$templateCache', 'BootstrapDialogManager', 'LoadingModal', 'ModalFactory',
        function ($compile, $http, $templateCache, BootstrapDialogManager, LoadingModal, ModalFactory) {

        function loadTemplate (scope) {
            var url = '../tasks/partials/widgets/import-modal.html';

            $http.get(url, {cache: $templateCache})
                .success(function (html) {
                    var compiledMessage = $compile(html)(scope);
                    scope.importDialog.setMessage(compiledMessage);
                });
        }

        return {
            restrict: 'A',
            replace: true,
            name: 'ctrl',
            controller: '@',
            scope: {
                getTasks: '=',
                msg: '='
            },
            link: function (scope, element, attrs) {
                loadTemplate(scope);
                scope.importDialog = new BootstrapDialog({
                    title: scope.msg('task.import'),
                    closable: true,
                    closeByBackdrop: false,
                    closeByKeyboard: false,
                    draggable: false,
                    cssClass: 'tasks',
                    buttons: [{
                        label: scope.msg('task.button.import'),
                        cssClass: 'btn btn-primary',
                        action: function (dialogItself) {
                            LoadingModal.open();
                            $('#importTaskForm').ajaxSubmit({
                                success: function () {
                                    scope.getTasks();
                                    $('#importTaskForm').resetForm();
                                    BootstrapDialogManager.close(dialogItself);
                                    LoadingModal.close();
                                },
                                error: function (response) {
                                    LoadingModal.close();
                                    ModalFactory.showErrorAlertWithResponse('task.error.import', 'task.header.error', response);
                                }
                            });
                        }
                    }, {
                        label: scope.msg('task.close'),
                        cssClass: 'btn btn-default',
                        action: function (dialogItself) {
                            $('#importTaskForm').resetForm();
                            BootstrapDialogManager.close(dialogItself);
                        }
                    }]
                });
                element.on('click', scope.openImportTaskModal);
            }
        };
    }]);

    directives.directive('triggersModal', ['$compile', '$http', '$templateCache', 'BootstrapDialogManager',
        function ($compile, $http, $templateCache, BootstrapDialogManager) {

        function loadTemplate (scope) {
            var url = '../tasks/partials/modals/triggersModal.html';

            $http.get(url, {cache: $templateCache})
                .success(function (html) {
                    scope.messageBody = html;
                });
        }

        return {
            restrict: 'A',
            replace: true,
            name: 'ctrl',
            controller: '@',
            scope: {
                channel: '=',
                task: '=',
                util: '=',
                msg: '='
            },
            link: function (scope, element, attrs) {
                loadTemplate(scope);
                scope.triggersDialog = new BootstrapDialog({
                    title: scope.msg('task.tooltip.availableTriggers'),
                    closable: true,
                    closeByBackdrop: false,
                    closeByKeyboard: false,
                    draggable: false,
                    cssClass: 'tasks',
                    autodestroy: false,
                    buttons: [{
                        label: scope.msg('task.close'),
                        cssClass: 'btn btn-default',
                        action: function (dialogItself) {
                            BootstrapDialogManager.close(dialogItself);
                        }
                    }]
                 });
                 element.on('click', function () {
                    var compiledMessage = $compile(scope.messageBody)(scope);
                    scope.triggersDialog.setMessage(compiledMessage);
                    scope.openTriggersModal();
                 });
            }
        };
    }]);

    directives.directive('moduleNameImg', function() {
        var imgSrc = {
            link: function loadImage(scope, element, attrs) {
                var url = attrs.moduleNameImg;
                element.css({
                    'background-image': 'url(' + url +')',
                    'background-size' : 'cover'
                });
            }
        };
        return imgSrc;
    });
}());
