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
                activityId;

                elem.jqGrid({
                    url: '../tasks/api/activity/' + scope.taskId,
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false
                    },
                    colModel: [{
                        name: 'date',
                        formatter: function (value) {
                            return moment(parseInt(value, 10)).fromNow();
                        },
                        index: 'date',
                        sortable: false,
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
                        width: 180
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
                        for (k = 0; k < rows.length; k+=1) {
                            activity = $("#taskHistoryTable").getCell(rows[k],"activityType").toLowerCase();
                            message = $("#taskHistoryTable").getCell(rows[k],"message");
                            if (activity !== undefined) {
                                if (activity === 'success') {
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType','<div class="recent-activity-icon fa icon-green fa-check-circle fa-2x"></div>','ok',{ },'');
                                } else if (activity === 'warning') {
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType','<div class="recent-activity-task-img fa warning-type fa-question-circle fa-2x"></div>','ok',{ },'');
                                } else if (activity === 'in progress') {
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType','<div class="recent-activity-task-img fa icon-blue fa-info-circle fa-2x"></div>','ok',{ },'');
                                } else if (activity === 'error') {
                                    activityId = $("#taskHistoryTable").getCell(rows[k],"id");
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType',
                                        '<div class="recent-activity-task-img fa icon-red fa-exclamation-circle fa-2x"/>','ok',{ },'');
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'retry',
                                        '&nbsp;&nbsp;<span type="button" class="btn btn-primary btn-xs grid-ng-clickable" ng-click="retryTask(' + activityId + ')">Retry</span>',
                                        'ok',{ },'');
                                }
                            }

                            stackTraceElement = $("#taskHistoryTable").getCell(rows[k],"stackTraceElement");
                            scope.stackTraceEl = stackTraceElement;
                            fields = $("#taskHistoryTable").getCell(rows[k], "fields").split(",");
                            messageToShow = [message].concat(fields);
                            if (message !== undefined && activity === 'error' && stackTraceElement !== undefined && stackTraceElement !== null) {
                                $("#taskHistoryTable").jqGrid('setCell',rows[k],'message',
                                    '<p class="wrap-paragraph">' + scope.msg(messageToShow) + '&nbsp;&nbsp; <span type="button" class="btn btn-primary btn-xs grid-ng-clickable" ng-click="showStackTrace()" data-target="#stackTraceElement' +
                                    k + '">'+ scope.msg('task.button.showStackTrace') + '</span></p>',
                                    'ok',{ },'');
                            } else if (message !== undefined) {
                                $("#taskHistoryTable").jqGrid('setCell',rows[k],'message',scope.msg(messageToShow),'ok',{ },'');
                            }
                        }
                    },
                    loadComplete: function() {
                        $compile($('.grid-ng-clickable'))(scope);
                    }
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

                if(scope.field.prefix === ManageTaskUtils.DATA_SOURCE_PREFIX){
                    scope.displayName = "{0}.{1}#{2}.{3}".format(
                        scope.msg(scope.field.providerName),
                        scope.msg(scope.field.serviceName),
                        scope.field.objectId,
                        scope.msg(scope.field.displayName)
                    );
                } else if(scope.field.prefix === ManageTaskUtils.POST_ACTION_PREFIX){
                    scope.displayName = "{0}.{1}#{2}.{3}".format(
                        scope.msg(scope.field.channelName),
                        scope.msg(scope.field.actionName),
                        scope.field.objectId,
                        scope.msg(scope.field.displayName)
                    );
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
            },
            templateUrl: '../tasks/partials/field.html'
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

    directives.directive('contenteditable', function ($compile, ManageTaskUtils) {
        function formatField (field) {
            return '{{{0}}}'.format(
                ManageTaskUtils.formatField(field)
                );
        }
        function isTagForPlaceholder (tag) {
            return (tag.tagName && tag.tagName.toLowerCase() === 'em');
        }
        function readContent (element) {
            var container = $('<div></div>');
            element.contents().each(function(){
                var field, ele = $(this);
                if(this.tagName && this.tagName.toLowerCase() === 'field'){
                    if(ele.attr("boolean")){
                        container.append(ele.attr("boolean"));
                    }else{
                        field = ele.data('value');
                        container.append(formatField(field));
                    }
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

        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, element, attrs, ngModel) {
                if (!ngModel){
                    return false;
                }

                scope.$watch(function () {
                    return ngModel.$viewValue;
                }, function(){
                    ngModel.$render();
                });

                // Disallow enter key being pressed, except on certain data types
                element.on('keypress', function (event) {
                    var type = $(this).data('type');
                    if (type !== 'TEXTAREA' && type !== 'MAP' && type !== 'LIST' && type !== 'PERIOD') {
                        return event.which !== 13;
                    }
                });

                element.bind('blur', function (event) {
                    event.stopPropagation();
                    if(element[0] !== event.target){
                        return;
                    }
                    ngModel.$setViewValue(readContent(element));
                    scope.$apply();
                });

                scope.$on('field.changed', function (event) {
                    event.stopPropagation();
                    ngModel.$setViewValue(readContent(element));
                    scope.$apply();
                });

                ngModel.$render = function () {
                    var parsedValue, viewValueStr, matches;
                    element.html("");
                    if(!ngModel.$viewValue){
                        return false;
                    }
                    parseField(ngModel.$viewValue).forEach(function(str){
                        if(findField(str)){
                            element.append(makeFieldElement(str, scope));
                        } else if (element.data('type') === 'BOOLEAN' && (str === 'true' || str === 'false')){
                            element.append(makeBooleanFieldElement(str, scope));
                        } else {
                            element.append(str);
                        }
                    });
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

                            if(!field){
                                return false;
                            }
                            field = formatField(field);
                            if(element[0].contains(selection.anchorNode) && anchorText){
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
                        }
                    });
                }
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
                'action': '@'
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

    directives.directive('manipulationModal', function ($compile, BootstrapDialogManager) {
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
                    displayNameOnly = scope.displayNameOnly,
                    horizontalPosition =1,
                    timeInterval = null,
                    timeOfMouseOut=0;

                if(!scope.manipulationType){
                    return false;
                }
                if (['UNICODE', 'TEXTAREA', 'DATE'].indexOf(scope.manipulationType) === -1){
                    return false;
                }
                if(!scope.manipulations){
                    return false;
                }

                element.on({
                    mouseover: function(){
                        var timeOfMouseOver = new Date().getTime();

                        /*Changing between the span and remove button triggers the mouseout event.
                        This prevents from reseting the position of the field in case of such situation.*/
                        if(timeOfMouseOver-timeOfMouseOut>5){
                            horizontalPosition = 1;
                        }

                        /*If the field was removed, the timeout has not been cleared.*/
                        if(timeInterval!==null){
                            clearInterval(timeInterval);
                        }

                        timeInterval = setInterval(function(){
                            element.scrollLeft(horizontalPosition);
                            horizontalPosition = horizontalPosition + 1;
                        }, 10);
                    },
                    mouseout: function(){
                        clearInterval(timeInterval);
                        timeInterval=null;
                        timeOfMouseOut = new Date().getTime();
                    }
                });

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
                            closeByBackdrop: false,
                            closeByKeyboard: false,
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
                            }],
                            onhide: function(dialog){
                                BootstrapDialogManager.onhide(dialog);
                            }
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
                    $scope.manipulationTypes = $scope.manipulationTypes.concat(['plusDays', 'minusDays', 'plusHours', 'minusHours', 'plusMinutes', 'minusMinutes']);
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
