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

                var elem = angular.element(element), k, rows, activity, message, date, stackTraceElement;

                elem.jqGrid({
                    url: '../tasks/api/activity/' + scope.taskId,
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false
                    },
                    colModel: [{
                        name: 'activityType',
                        index: 'activityType',
                        sortable: false,
                        width: 50
                    }, {
                        name: 'message',
                        index: 'message',
                        sortable: false,
                        width: 220
                    }, {
                        name: 'date',
                        formatter: function (value) {
                            return moment(parseInt(value, 10)).fromNow();
                        },
                        index: 'date',
                        sortable: false,
                        width: 80
                    }, {
                       name: 'stackTraceElement',
                       index: 'stackTraceElement',
                       sortable: false,
                       hidden: true
                   }],
                    pager: '#' + attrs.taskHistoryGrid,
                    viewrecords: true,
                    gridComplete: function () {
                        elem.jqGrid('setLabel', 'activityType', scope.msg('task.subsection.status'));
                        elem.jqGrid('setLabel', 'message', scope.msg('task.subsection.message'));
                        elem.jqGrid('setLabel', 'date', scope.msg('task.subsection.information'));

                        $('#outsideTaskHistoryTable').children('div').width('100%');
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
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType','<img src="../tasks/img/icon-ok.png" class="recent-activity-task-img"/>','ok',{ },'');
                                } else if (activity === 'warning') {
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType','<img src="../tasks/img/icon-question.png" class="recent-activity-task-img"/>','ok',{ },'');
                                } else if (activity === 'error') {
                                    $("#taskHistoryTable").jqGrid('setCell',rows[k],'activityType','<img src="../tasks/img/icon-exclamation.png" class="recent-activity-task-img"/>','ok',{ },'');
                                }
                            }

                            stackTraceElement = $("#taskHistoryTable").getCell(rows[k],"stackTraceElement");
                            if (message !== undefined && activity === 'error' && stackTraceElement !== undefined && stackTraceElement !== null) {
                                $("#taskHistoryTable").jqGrid('setCell',rows[k],'message',
                                    '<p class="wrap-paragraph">' + scope.msg(message) +
                                    '&nbsp;&nbsp;<span class="label label-danger pointer" data-toggle="collapse" data-target="#stackTraceElement' + k + '">' +
                                    scope.msg('task.button.showStackTrace') + '</span></p>' +
                                    '<pre id="stackTraceElement' + k + '" class="collapse">' + stackTraceElement + '</pre>'
                                    ,'ok',{ },'');
                            } else if (message !== undefined) {
                                $("#taskHistoryTable").jqGrid('setCell',rows[k],'message',scope.msg(message),'ok',{ },'');
                            }
                        }
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

    directives.directive('draggable', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.draggable({
                    revert: true,
                    start: function () {
                        if (element.hasClass('draggable')) {
                            element.find("div:first-child").popover('hide');
                        }
                    }
                });
            }
        };
    });


    directives.directive('droppable', function (ManageTaskUtils, $compile) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.droppable({
                    drop: function (event, ui) {
                        var parent = scope, value, pos, eventKey, dragElement, browser, emText, dataSource,
                            position = function (dropElement, dragElement) {
                                var sel, range, space = document.createTextNode(''), el, frag, node, lastNode, rangeInDropElem;

                                if (window.getSelection) {
                                    sel = window.getSelection();

                                    if (sel.getRangeAt && sel.rangeCount && sel.anchorNode.parentNode.tagName.toLowerCase() !== 'span') {
                                        range = sel.getRangeAt(0);
                                        rangeInDropElem = range.commonAncestorContainer.parentNode === dropElement[0] || range.commonAncestorContainer === dropElement[0] || range.commonAncestorContainer.parentNode.parentNode === dropElement[0];
                                    } else {
                                        rangeInDropElem = false;
                                    }

                                    if (rangeInDropElem) {
                                        el = document.createElement("div");
                                        el.innerHTML = dragElement[0].outerHTML;

                                        frag = document.createDocumentFragment();

                                        while ((node = el.firstChild) !== null) {
                                            lastNode = frag.appendChild(node);
                                        }

                                        $compile(frag)(scope);
                                        range.insertNode(frag);
                                        range.insertNode(space);

                                        if (lastNode) {
                                            range = range.cloneRange();
                                            range.setStartAfter(lastNode);
                                            range.collapse(true);
                                            sel.removeAllRanges();
                                            sel.addRange(range);
                                        }
                                    } else {
                                        $compile(dragElement)(scope);
                                        dropElement.append(dragElement);
                                        dropElement.append(space);
                                    }
                                } else if (document.selection && document.selection.type !== "Control") {
                                    document.selection.createRange().pasteHTML($compile(dragElement[0].outerHTML)(scope));
                                }
                            };

                        while (parent.task === undefined) {
                            parent = parent.$parent;
                        }

                        dragElement = angular.element(ui.draggable);
                        browser = parent.BrowserDetect.browser;

                        if (dragElement.hasClass('triggerField')) {
                            switch (element.data('type')) {
                            case 'DATE': emText = 'task.placeholder.dateOnly'; break;
                            case 'TIME': emText = 'task.placeholder.timeOnly'; break;
                            case 'BOOLEAN': emText = 'task.placeholder.booleanOnly'; break;
                            default:
                            }

                            if (browser !== 'Chrome' && browser !== 'Explorer') {
                                if (element.hasClass('dataSourceField')) {
                                    dataSource = ManageTaskUtils.find({
                                        where: parent.task.taskConfig.steps,
                                        by: [{
                                            what: 'providerId',
                                            equalTo: element.data('index')
                                        }, {
                                            what: 'objectId',
                                            equalTo: element.data('object-id')
                                        }]
                                    });
                                }

                                if (emText !== undefined) {
                                    if (element.hasClass('actionField')) {
                                        delete parent.filter(parent.selectedAction[element.data('action')].actionParameters, {hidden: false})[element.data('index')].value;
                                    } else if (element.hasClass('dataSourceField')) {
                                        if (dataSource) {
                                            delete scope.lookupField.value;
                                        }
                                    }
                                }

                                if (dragElement.data('prefix') === 'trigger') {
                                    eventKey = '{{trigger.' + parent.selectedTrigger.eventParameters[dragElement.data('index')].eventKey + '}}';
                                } else if (dragElement.data('prefix') === 'ad') {
                                    eventKey = '{{ad.' + parent.msg(dragElement.data('source')) + '.' + dragElement.data('object-type') + "#" + dragElement.data('object-id') + '.' + dragElement.data('field') + '}}';
                                }

                                pos = element.caret();
                                value = element.val() || '';

                                if (element.hasClass('actionField')) {
                                    if(parent.filter(parent.selectedAction[element.data('action')].actionParameters, {hidden: false})[element.data('index')].type === "DATE") {
                                        parent.filter(parent.selectedAction[element.data('action')].actionParameters, {hidden: false})[element.data('index')].value = eventKey;
                                    } else {
                                        parent.filter(parent.selectedAction[element.data('action')].actionParameters, {hidden: false})[element.data('index')].value = value.insert(pos, eventKey);
                                    }
                                } else if (element.hasClass('dataSourceField')) {
                                    if (dataSource) {
                                        scope.lookupField.value = value.insert(pos, eventKey);
                                    }
                                }
                            } else if (!(dragElement.data('popover') === 'no' && element.data('type') !== 'format')){
                                if (emText !== undefined) {
                                    element.empty();
                                    element.append('<em style="color: gray;">' + parent.msg(emText) + '</em>');
                                }

                                element.find('em').remove();

                                dragElement = dragElement.clone();
                                dragElement.css("position", "relative");
                                dragElement.css("left", "0px");
                                dragElement.css("top", "0px");
                                dragElement.attr("unselectable", "on");

                                if ((dragElement.data('type') !== 'INTEGER' || dragElement.data('type') !== 'DOUBLE') && dragElement.data('popover') !== 'no') {
                                    if (dragElement.data('type') === 'UNICODE' || dragElement.data('type') === 'TEXTAREA') {
                                        dragElement.attr("manipulationpopover", "STRING");
                                    } else if (dragElement.data('type') === 'DATE') {
                                        if (element.data('type') === 'DATE') {
                                            dragElement.attr("manipulationpopover", "DATE2DATE");
                                        } else {
                                            dragElement.attr("manipulationpopover", "DATE");
                                        }
                                    }
                                }

                                dragElement.addClass('pointer');
                                dragElement.addClass('popoverEvent');
                                dragElement.addClass('nonEditable');
                                dragElement.removeAttr("ng-repeat");
                                dragElement.removeAttr("draggable");

                                position(element, dragElement);
                            }
                        }

                        parent.$digest();
                        scope.$apply();
                    }
                });
            }
        };
    });

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

    directives.directive('contenteditable', function ($compile) {
        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, element, attrs, ngModel) {
                var read = function () {
                    var container = $('<div></div>');
                    if (element.html() === "<br>") {
                        element.find('br').remove();
                    }
                    container.html($.trim(element.html()));
                    container.find('.editable').attr('contenteditable', false);
                    container.find('.popover').remove();
                    ngModel.$setViewValue(container.html());
                    scope.$apply();
                };

                if (!ngModel) {
                    return;
                }

                ngModel.$render = function () {
                    var container = $('<div></div>');
                    container.html(ngModel.$viewValue);
                    container.find('.editable').attr('contenteditable', true);

                    if (container.text() !== "") {
                        container = container.contents();
                        container.each(function () {
                            if (this.localName === 'span') {
                                return $compile(this)(scope);
                            }
                        });
                    } else {
                        container = container.html();
                    }

                    return element.html(container);
                };

                element.bind('focusin', function (event) {
                    var el = this;

                    event.stopPropagation();

                    window.setTimeout(function () {
                        var sel, range;
                        if (window.getSelection && document.createRange) {
                            range = document.createRange();
                            range.selectNodeContents(el);

                            if (el.childNodes.length !== 0) {
                                range.setStartAfter(el.childNodes[el.childNodes.length - 1]);
                            }

                            range.collapse(true);

                            sel = window.getSelection();
                            sel.removeAllRanges();
                            sel.addRange(range);
                        } else if (document.body.createTextRange) {
                            range = document.body.createTextRange();

                            if (el.childNodes.length !== 0) {
                                range.moveToElementText(el.childNodes[el.childNodes.length - 1]);
                            }

                            range.collapse(true);
                            range.select();
                        }
                    }, 1);

                    if (ngModel.$viewValue !== $.trim(element.html())) {
                        return scope.$eval(read);
                    }
                });

                element.bind('keypress', function (event) {
                    var type = $(this).data('type');

                    if (type !== 'TEXTAREA' && type !== 'MAP' && type !== 'LIST') {
                        return event.which !== 13;
                    }
                });

                element.bind('blur keyup change mouseleave', function (event) {
                    event.stopPropagation();
                    if (ngModel.$viewValue !== $.trim(element.html())) {
                        return scope.$eval(read);
                    }
                });

                return read;
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

    directives.directive('manipulationpopover', function ($compile, $templateCache, $http) {
        return {
            restrict: 'A',
            link: function (scope, el, attrs) {
                var manipulationOptions = '', title = '', loader, manType = attrs.manipulationpopover, elType = attrs.type, msgScope = scope, filter = scope.$parent.filter;

                while (msgScope.msg === undefined) {
                    msgScope = msgScope.$parent;
                }

                if (manType === 'STRING') {
                    title = msgScope.msg('task.stringManipulation', '');
                    loader = $http.get('../tasks/partials/widgets/string-manipulation.html', {cache: $templateCache})
                        .success(function (html) {
                            manipulationOptions = html;
                        });
                } else if (manType === 'DATE') {
                    title = msgScope.msg('task.dateManipulation', '');
                    loader = $http.get('../tasks/partials/widgets/date-manipulation.html', {cache: $templateCache})
                        .success(function (html) {
                            manipulationOptions = html;
                        });
                } else if (manType === 'DATE2DATE') {
                  title = msgScope.msg('task.dateManipulation', '');
                   loader = $http.get('../tasks/partials/widgets/date2date-manipulation.html', {cache: $templateCache})
                       .success(function (html) {
                           manipulationOptions = html;
                       });
                }

                el.bind('click', function () {
                    var man = $("[ismanipulate=true]").text();
                    if (man.length === 0) {
                        angular.element(this).attr('ismanipulate', 'true');
                    } else {
                        angular.element(this).removeAttr('ismanipulate');
                    }
                });


                if (elType === 'UNICODE' || elType === 'TEXTAREA' || elType === 'DATE') {
                    el.popover({
                        template : '<div unselectable="on" contenteditable="false" class="popover dragpopover"><div unselectable="on" class="arrow"></div><div unselectable="on" class="popover-inner"><h3 unselectable="on" class="popover-title"></h3><div unselectable="on" class="popover-content"><p unselectable="on"></p></div></div></div>',
                        title: title,
                        html: true,
                        content: function () {
                            var elem = $(manipulationOptions), element, manipulation;
                            scope.sortableArrayTemp = [];
                            $compile(elem)(msgScope);
                            msgScope.$apply(elem);
                            element = $("[ismanipulate=true]");
                            manipulation = element.attr('manipulate');

                            if (elem.length === 0) {
                                elem = $(manipulationOptions);
                                $compile(elem)(msgScope);
                                msgScope.$apply(elem);
                            }

                            elem.find("span").replaceWith(function () {
                                return $(this)[0].outerHTML;
                            });

                            if (manipulation !== undefined) {

                                scope.cleanArray = function() {
                                    var indexArray = scope.sortableArrayTemp.indexOf("");
                                    if (indexArray !== -1) {
                                        scope.sortableArrayTemp.splice(indexArray,1);
                                    }
                                };

                                scope.setSortable = function(elemen, index) {
                                // Every new manipulation should be added to options array.
                                // Add name and input for each manipulation and
                                // pattern only if manipulation takes parameters
                                var isValid = false, reg, i, options = [{
                                        name: 'join',
                                        input: 'input[join-update]',
                                        pattern: 5
                                    }, {
                                        name: 'split',
                                        input: 'input[split-update]',
                                        pattern: 6
                                    }, {
                                        name: 'substring',
                                        input: 'input[substring-update]',
                                        pattern: 10
                                    }, {
                                        name: 'dateTime',
                                        input: 'input[date-update]',
                                        pattern: 9
                                    }, {
                                        name: 'plusDays',
                                        input: 'input[manipulation-kind="plusDays"]',
                                        pattern: 9
                                    }, {
                                        name: 'minusDays',
                                        input: 'input[manipulation-kind="minusDays"]',
                                        pattern: 10
                                    }, {
                                        name: 'plusHours',
                                        input: 'input[manipulation-kind="plusHours"]',
                                        pattern: 10
                                    }, {
                                        name: 'minusHours',
                                        input: 'input[manipulation-kind="minusHours"]',
                                        pattern: 11
                                    }, {
                                        name: 'plusMinutes',
                                        input: 'input[manipulation-kind="plusMinutes"]',
                                        pattern: 12
                                    }, {
                                        name: 'minusMinutes',
                                        input: 'input[manipulation-kind="minusMinutes"]',
                                        pattern: 13
                                    }, {
                                        name: 'format',
                                        input: ''
                                    }, {
                                        name: 'capitalize',
                                        input: ''
                                    }, {
                                        name: 'toUpper',
                                        input: ''
                                    }, {
                                        name: 'toLower',
                                        input: ''
                                    }, {
                                        name: 'URLEncode',
                                        input: ''
                                    }, {
                                        name: 'parseDate',
                                        input: 'input[parsedate-update]',
                                        pattern: 10
                                    } ];

                                    for(i=0; i<options.length; i+=1) {
                                        if(elemen.indexOf(options[i].name) !== -1) {
                                            elemen = elemen.replace(elemen, options[i].name);
                                            isValid = true;
                                            break;
                                        }
                                    }

                                    if (isValid) {
                                        elem.find("span[setmanipulation="+elemen+"]").replaceWith(function () {
                                            if (elemen !== undefined && elemen.indexOf(this.attributes.getNamedItem('setmanipulation').value) !== -1) {
                                                $(this).parent().children().css({ 'display' : '' });
                                                $(this).parent().addClass('active');

                                                for(i=0; i<options.length; i+=1) {
                                                    if (options[i].input !== '') {
                                                        if (manipulation.indexOf(options[i].name) !== -1 && elemen.indexOf(options[i].name) !== -1) {
                                                            $(this.nextElementSibling).css({ 'display' : '' });
                                                            elem.find(options[i].input).val(manipulation.slice(manipulation.indexOf(options[i].name) + options[i].pattern, manipulation.indexOf(")", manipulation.indexOf(options[i].name))));
                                                            break;
                                                        }
                                                    }
                                                }

                                                $(elem[0]).append($(this).parent().clone().end());
                                            }
                                            return $(this)[0].outerHTML;
                                        });
                                    } else {
                                        // invalid manipulation
                                        reg = new RegExp("\\(.*?\\)", "g");
                                        elemen = elemen.replace(reg,"");
                                        elem.filter("ul#sortable").append('<li unselectable="on" class="padding-botton6 invalid"><span unselectable="on" class="pointer ng-binding" setmanipulation="'+elemen+'">'+elemen+'<span class="fa fa-times" style="float: right;"></span></span></li>');
                                    }
                                };

                            scope.sortableArrayTemp = manipulation.split(" ");
                            scope.sortableArrayTemp.forEach(scope.cleanArray);
                            scope.sortableArrayTemp.forEach(scope.setSortable);
                            }

                            return $compile(elem)(msgScope);
                        },
                        placement: "auto left",
                        trigger: 'manual'
                    }).click(function (event) {
                        event.stopPropagation();
                        if (!$(this).hasClass('hasPopoverShow')) {
                            var otherPopoverElem = $('.hasPopoverShow');

                            if (otherPopoverElem !== undefined && $(this) !== otherPopoverElem) {
                                otherPopoverElem.popover('hide');
                                otherPopoverElem.removeClass('hasPopoverShow');
                                otherPopoverElem.removeAttr('ismanipulate');
                            }
                            if (filter.key) {
                                $(this).attr('manipulate', filter.key.split("?").slice(1).join(" "));
                            }

                            $(this).addClass('hasPopoverShow');
                            $(this).attr('ismanipulate', 'true');
                            $(this).popover('show');
                        } else {
                            $(this).popover('hide');
                            $(this).removeClass('hasPopoverShow');
                            $(this).removeAttr('ismanipulate');
                        }

                        $('.dragpopover').click(function (event) {
                            event.stopPropagation();
                        });

                        $('.dragpopover').mousedown(function (event) {
                            event.stopPropagation();
                        });

                        $('.create-edit-task').click(function () {
                            $('.hasPopoverShow').each(function () {
                                $(this).popover('hide');
                                $(this).removeClass('hasPopoverShow');
                                $(this).removeAttr('ismanipulate');
                            });
                        });
                    });

                    el.bind("manipulateChanged", function () {
                        if (filter.key) {
                            var manipulateAttributes = el.attr('manipulate'),
                                key = filter.key.split("?")[0], array, i;

                            if (manipulateAttributes !== "") {
                                array = manipulateAttributes.split(" ");

                                for (i = 0; i < array.length; i += 1) {
                                    key = key.concat("?" + array[i]);
                                }
                                key = key.replace(/\?+(?=\?)/g, '');
                            }
                            filter.key = key;
                        }
                    });
                }
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


    directives.directive('datetimePickerStartDateInput', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var parent = scope;

                element.datetimepicker({
                    showTimezone: true,
                    useLocalTimezone: true,
                    dateFormat: 'yy-mm-dd',
                    timeFormat: 'HH:mm z',
                    showOn: true,
                    constrainInput: false,
                    onSelect: function (dateTex) {
                        //parent.selectedTrigger.eventParameters[$(this).data('index')].value = dateTex;
                        parent.task.trigger.startDate = dateTex;
                        parent.$apply();
                    }
                });
            }
        };
    });

    directives.directive('datetimePickerEndDateInput', function () {
            return {
                restrict: 'A',
                link: function (scope, element, attrs) {
                    var parent = scope;

                    element.datetimepicker({
                        showTimezone: true,
                        useLocalTimezone: true,
                        dateFormat: 'yy-mm-dd',
                        timeFormat: 'HH:mm z',
                        showOn: true,
                        constrainInput: false,
                        onSelect: function (dateTex) {
                            //parent.selectedTrigger.eventParameters[$(this).data('index')].value = dateTex;
                            parent.task.trigger.endDate = dateTex;
                            parent.$apply();
                        }
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
                    dateFormat: 'yy-mm-dd',
                    timeFormat: 'HH:mm z',
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

    directives.directive('setmanipulation', function () {
        return {
            restrict : 'A',
            require: '?ngModel',
            link : function (scope, el, attrs) {

                var manipulateElement = $("[ismanipulate=true]"), sortableElement, manipulateAttr;
                scope.sortableArray = [];
                scope.manipulations = '';

                scope.cleanArray = function() {
                    var indexArray = scope.sortableArray.indexOf("");
                    if (indexArray !== -1) {
                        scope.sortableArray.splice(indexArray,1);
                    }
                };

                scope.normalizeArray = function(element, index) {
                    scope.sortableArray.splice(index, 1, element + ' ');
                };

                scope.setSortableArray = function() {
                manipulateAttr = manipulateElement.attr("manipulate");
                    if (manipulateAttr !== undefined) {
                        scope.sortableArray = manipulateAttr.split(" ");
                        scope.sortableArray.forEach(scope.cleanArray);
                        scope.sortableArray.forEach(scope.normalizeArray);
                    }
                };

                scope.setSortableArray();

                el.bind('mouseenter mousedown', function() {

                    scope.dragStart = function(e, ui) {
                        ui.originalPosition.top = 0;
                        scope.setSortableArray();
                        ui.item.data('start', ui.item.index());
                    };

                    scope.dragEnd = function(e, ui) {
                        var start = ui.item.data('start'),
                        end = ui.item.index();
                        scope.sortableArray.splice(end, 0,
                        scope.sortableArray.splice(start, 1)[0]);
                        if(scope.sortableArray.length) {
                            scope.manipulations = scope.sortableArray.join("");
                        }
                        manipulateElement.attr('manipulate', scope.manipulations);
                        manipulateElement.trigger('manipulateChanged');
                        scope.$apply();
                    };

                    sortableElement = $('#sortable').sortable({
                        placeholder: 'ui-state-highlight',
                        axis: 'y',
                        cursor: 'move',
                        opacity: 0.95,
                        tolerance: 'pointer',
                        zIndex: 9999,
                        start: scope.dragStart,
                        update: scope.dragEnd
                    });
                });

                el.bind("click", function () {
                    var manipulateElement = $("[ismanipulate=true]"), joinSeparator = "", reg, manipulation, manipulateAttributes, manipulationAttributesIndex, nonParamManip = true, i, found,
                    // Every new manipulation that takes parameter should be added to
                    // paramOptions array. Name is just a name of manipulation
                    // and id is id of input field for that manipulation
                    paramOptions = [
                        {
                            name: 'join',
                            id: '#joinSeparator'
                        },
                        {
                            name: 'split',
                            id: '#splitSeparator'
                        },
                        {
                            name: 'substring',
                            id: '#substringSeparator'
                        },
                        {
                            name: 'dateTime',
                            id: '#dateFormat'
                        },
                        {
                            name: 'plusDays',
                            id: '#plusDays'
                        },
                        {
                            name: 'minusDays',
                            id: '#minusDays'
                        },
                        {
                            name: 'plusHours',
                            id: '#plusHours'
                        },
                        {
                            name: 'minusHours',
                            id: '#minusHours'
                        },
                        {
                            name: 'plusMinutes',
                            id: '#plusMinutes'
                        },
                        {
                            name: 'minusMinutes',
                            id: '#minusMinutes'
                        },
                        {
                            name: 'parseDate',
                            id: '#parseDate'
                        },
                        {
                            name: 'format',
                            id: ''
                        }
                    ];

                    manipulation = this.getAttribute("setManipulation");
                    manipulateAttributes = manipulateElement.attr("manipulate") || "";

                    if ($(this).parent(".invalid").remove().length) {
                        if (manipulateAttributes.indexOf(manipulation) !== -1) {
                            reg = new RegExp(manipulation + "(\\(.*\\))?( |$)", "g");
                            manipulateAttributes = manipulateAttributes.replace(reg, '');
                            manipulateElement.attr('manipulate', manipulateAttributes);
                            scope.setSortableArray();
                        }
                        return;
                    }

                    if (manipulateAttributes.charAt(manipulateAttributes.length - 1) !== " ") {
                        manipulateAttributes = manipulateAttributes + " ";
                    }

                    if (manipulateAttributes.indexOf(manipulation) !== -1) {
                        manipulationAttributesIndex = manipulateElement.attr("manipulate").indexOf(manipulation);

                        if (manipulation === "format") {
                            reg = new RegExp("format(\\((\\{.*\\})*\\))", "g");
                            manipulateAttributes = manipulateAttributes.replace(reg, '');
                        } else {

                            for (i = 0; i < paramOptions.length; i += 1) {
                                if(manipulation === paramOptions[i].name) {
                                    nonParamManip = false;
                                    break;
                                }
                            }

                            if (nonParamManip) {
                                reg = new RegExp(manipulation + "(\\(.*\\))?( |$)", "g");
                                manipulateAttributes = manipulateAttributes.replace(reg, '');
                            } else {
                                joinSeparator = manipulation + "\\(" + this.nextElementSibling.value + "\\)( |$)";
                                reg = new RegExp(joinSeparator, "g");
                                manipulateAttributes = manipulateAttributes.replace(reg, '');
                            }
                        }
                    } else {
                        manipulateAttributes = manipulateAttributes.replace(/ +(?= )/g, '');

                        for(i=0; i<paramOptions.length; i+=1) {
                            if(manipulation === paramOptions[i].name) {
                                if(paramOptions[i].id !== '') {
                                    $(paramOptions[i].id).val("");
                                }
                                manipulateAttributes = manipulateAttributes + manipulation + "()" + " ";
                                found = true;
                                break;
                            }
                        }
                        if(!found) {
                            manipulateAttributes = manipulateAttributes + manipulation + " ";
                        }
                    }

                    manipulateElement.attr('manipulate', manipulateAttributes);
                    manipulateElement.trigger('manipulateChanged');
                    scope.setSortableArray();

                    if (!$(this).parent().hasClass('active')) {
                        $(this).parent().children().css({ 'display' : '' });
                        $(this).parent().addClass('active');
                        $('#sortable').append($(this.parentElement).clone().end());
                    } else {
                        $(this).parent().children('.glyphicon').css({ 'display' : 'none' });
                        $(this.nextElementSibling).css({ 'display' : 'none' });
                        $(this).parent().removeClass("active");
                        $('#sortable-no').append($(this.parentElement).clone().end());
                    }
                });
            }
        };
    });

    directives.directive('joinUpdate', function () {
        return {
            restrict : 'A',
            require: '?ngModel',
            link : function (scope, el, attrs) {
                el.bind("focusout focusin keyup", function (event) {
                    event.stopPropagation();
                    var manipulateElement = $("[ismanipulate=true]"),
                        manipulation = "join(" + $("#joinSeparator").val() + ")",
                        elementManipulation = manipulateElement.attr("manipulate"),
                        regex = new RegExp("join\\(.*?\\)", "g");

                    elementManipulation = elementManipulation.replace(regex, manipulation);
                    manipulateElement.attr("manipulate", elementManipulation);
                    manipulateElement.trigger('manipulateChanged');
                });
            }
        };
    });

    directives.directive('splitUpdate', function () {
        return {
            restrict : 'A',
            require: '?ngModel',
            link : function (scope, el, attrs) {
                el.bind("focusout focusin keyup", function (event) {
                    event.stopPropagation();
                    var manipulateElement = $("[ismanipulate=true]"),
                        manipulation = "split(" + $("#splitSeparator").val() + ")",
                        elementManipulation = manipulateElement.attr("manipulate"),
                        regex = new RegExp("split\\(.*?\\)", "g");

                    elementManipulation = elementManipulation.replace(regex, manipulation);
                    manipulateElement.attr("manipulate", elementManipulation);
                    manipulateElement.trigger('manipulateChanged');
                });
            }
        };
    });

    directives.directive('substringUpdate', function () {
        return {
            restrict : 'A',
            require: '?ngModel',
            link : function (scope, el, attrs) {
                el.bind("focusout focusin keyup", function (event) {
                    event.stopPropagation();
                    var manipulateElement = $("[ismanipulate=true]"),
                        manipulation = "substring(" + $("#substringSeparator").val() + ")",
                        elementManipulation = manipulateElement.attr("manipulate"),
                        regex = new RegExp("substring\\(.*?\\)", "g");

                    elementManipulation = elementManipulation.replace(regex, manipulation);
                    manipulateElement.attr("manipulate", elementManipulation);
                    manipulateElement.trigger('manipulateChanged');
                });
            }
        };
    });

    directives.directive('dateUpdate', function () {
        return {
            restrict : 'A',
            require: '?ngModel',
            link : function (scope, el, attrs) {
                el.bind("focusout focusin keyup", function (event) {
                    event.stopPropagation();
                    var manipulateElement = $("[ismanipulate=true]"),
                        manipulation = "dateTime(" + $("#dateFormat").val() + ")",
                        elementManipulation = manipulateElement.attr("manipulate"),
                        regex = new RegExp("dateTime\\(.*?\\)", "g");

                    elementManipulation = elementManipulation.replace(regex, manipulation);
                    manipulateElement.attr("manipulate", elementManipulation);
                    manipulateElement.trigger('manipulateChanged');
                });
            }
        };
    });

    directives.directive('dateManipulationUpdate', function() {
        return {
            restrict : 'A',
            require: '?ngModel',
            link : function (scope, el, attrs) {
                el.bind("focusout focusin keyup", function (event) {
                    event.stopPropagation();
                    var
                        manipKind = attrs.manipulationKind,
                        manipulateElement = $("[ismanipulate=true]"),
                        manipulation = manipKind + "(" + $("#" + manipKind).val() + ")",
                        elementManipulation = manipulateElement.attr("manipulate"),
                        regex = new RegExp(manipKind + "\\(.*?\\)", "g");

                    elementManipulation = elementManipulation.replace(regex, manipulation);
                    manipulateElement.attr("manipulate", elementManipulation);
                    manipulateElement.trigger('manipulateChanged');
                });
            }
        };
    });

    directives.directive('parsedateUpdate', function () {
        return {
            restrict : 'A',
            require: '?ngModel',
            link : function (scope, el, attrs) {
                el.bind("focusout focusin keyup", function (event) {
                    event.stopPropagation();
                    var manipulateElement = $("[ismanipulate=true]"),
                        manipulation = "parseDate(" + $("#parseDate").val() + ")",
                        elementManipulation = manipulateElement.attr("manipulate"),
                        regex = new RegExp("parseDate\\(.*?\\)", "g");

                    elementManipulation = elementManipulation.replace(regex, manipulation);
                    manipulateElement.attr("manipulate", elementManipulation);
                    manipulateElement.trigger('manipulateChanged');
                });
            }
        };
    });

    directives.directive('selectEvent', function() {
        return function(scope, element, attrs) {
            var elm = angular.element(element);
            elm.click(function (event) {
                var li = elm.parent('li'),
                    content = $(element).find('.content-task'),
                    visible = content.is(":visible"),
                    other = $('[select-event=' + attrs.selectEvent + ']').not('#' + $(this).attr('id')),
                    contentOffsetTop = $('#inner-center').offset().top,
                    setContentCss;

                    other.parent('li').not('.selectedTrigger').removeClass('active');
                    other.find('.content-task').hide();

                    if (visible) {
                        if (!li.hasClass('selectedTrigger')) {
                            li.removeClass('active');
                        }

                        content.hide();
                    } else {
                        li.addClass('active');
                        content.show();
                        content.removeClass('left right bottom top');
                        content.parent().find('div.arrow').css({'top':'50%'});
                        setContentCss = function () {
                            if ($(content).children('.popover-content').height() > 200) {
                                content.css({'height': '290'});
                                content.children('.popover-content').css({'height': 200, 'overflow-y': 'auto'});
                                content.parent().find('div.arrow').css({'top': function () {return ($(content).height()/2);}});
                                content.css({'top': function () {return -($(content).height()/2  - 60);}});
                            } else {
                                content.css({'top': function () {return -($(content).height()/2 - 60);}});
                            }
                        };
                        if (($(window).width() - $(this).offset().left) < 138 + $(content).width() && $(this).offset().left > $(content).width() && $(this).parent().parent().offset().left + ($(content).width()/2) < $(this).offset().left) {
                            content.addClass('left');
                            content.css({'left': function () {return -($(content).width() + 3);}});
                            setContentCss();
                        } else if (($(window).width() - ($(this).offset().left + 138)) > $(content).width() && !($(this).parent().parent().offset().left + ($(content).width()/2) < $(this).offset().left && $(this).offset().top - contentOffsetTop - 71 > 200 && $(content).children('.popover-content').height() + 11 < 200))  {
                            content.addClass('right');
                            content.css({'left': '125px'});
                            setContentCss();
                        } else if ($(this).offset().top - contentOffsetTop - 71 > 200 && $(content).children('.popover-content').height() + 11 < 200 && ($(window).width() - ($(this).offset().left + 108)) > $(content).width()) {
                            content.addClass('top');
                            content.children('.popover-content').css({'height': function () {return (content.children('.popover-content').children('ul').height()+15);}, 'overflow-y': 'auto'});
                            content.css({'height': function () {return (content.children('.popover-content').children('ul').height() + content.children('.popover-title').height() + 33);}});
                            content.css({'top': function () {return -($(content).height() + 8);}});
                            content.css({'left': function () {return -($(content).width()/2 - 70);}});
                            content.parent().find('div.arrow').css({'top': function() {return ($(content).height() + 2);}});
                        } else {
                            content.addClass('bottom');
                            content.css({'top': '115px', 'height': '240'});
                            if ($(content).children('.popover-content').children('ul').height() < 200) {
                                content.css({'height': function () {return (content.children('.popover-content').children('ul').height() + content.children('.popover-title').height() + 30);}});
                            } else {
                                content.children('.popover-content').css({'height': 200, 'overflow-y': 'auto'});
                            }
                            if (($(window).width() - ($(this).offset().left + 108)) < $(content).width()) {
                                content.css({'left': function () {return -($(content).width()/2 - 40);}});
                            } else {
                                content.css({'left': function () {return -($(content).width()/2 - 80);}});
                            }
                            content.parent().find('div.arrow').css({'top':'-11px'});
                        }
                    }
            });
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

                            img.attr('src', '../tasks/api/channel/icon?moduleName=' + action.moduleName);
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

                        img.attr('src', '../tasks/api/channel/icon?moduleName=' + scope.item.task.trigger.moduleName);
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

}());
