(function () {
    'use strict';

    /* Directives */

    var widgetModule = angular.module('motech-tasks');

    widgetModule.directive('overflowChange', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $(element).find('.overflowChange').livequery(function () {
                    $(this).on({
                        shown: function () {
                            $(this).css('overflow', 'visible');
                        },
                        hide: function () {
                            $(this).css('overflow', 'hidden');
                        }
                    });
                });
            }
        };
    });

    widgetModule.directive('expandAccordion', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $('.accordion').on('show', function (e) {
                    $(e.target).siblings('.accordion-heading').find('.accordion-toggle i.icon-caret-right').removeClass('icon-caret-right').addClass('icon-caret-down');
                });

                $('.tasks-list').on('show', function (e) {
                    $(e.target).siblings('.accordion-heading').find('.accordion-toggle i.icon-caret-right').removeClass('icon-caret-right').addClass('icon-caret-down');
                });

                $('.accordion').on('hide', function (e) {
                    $(e.target).siblings('.accordion-heading').find('.accordion-toggle i.icon-caret-down').removeClass('icon-caret-down').addClass('icon-caret-right');
                });

                $('.tasks-list').on('hide', function (e) {
                    $(e.target).siblings('.accordion-heading').find('.accordion-toggle i.icon-caret-down').removeClass('icon-caret-down').addClass('icon-caret-right');
                });
            }
        };
    });

    widgetModule.directive('draggable', function () {
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


    widgetModule.directive('droppable', function (ManageTaskUtils, $compile) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.droppable({
                    drop: function (event, ui) {
                        var parent = scope, value, pos, eventKey, dragElement, browser, emText, dataSource,
                            position = function (dropElement, dragElement) {
                                var sel, range, space = document.createTextNode(''), el, frag, node, lastNode;

                                if (window.getSelection) {
                                    sel = window.getSelection();

                                    if (sel.getRangeAt && sel.rangeCount && sel.anchorNode.parentNode.tagName.toLowerCase() !== 'span') {
                                        range = sel.getRangeAt(0);

                                        if (range.commonAncestorContainer.parentNode === dropElement[0] || range.commonAncestorContainer === dropElement[0] || range.commonAncestorContainer.parentNode.parentNode === dropElement[0]) {
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
                                        where: parent.selectedDataSources,
                                        by: [{
                                            what: 'dataSourceId',
                                            equalTo: element.data('index')
                                        }, {
                                            what: 'id',
                                            equalTo: element.data('object-id')
                                        }]
                                    });
                                }

                                if (emText !== undefined) {
                                    if (element.hasClass('actionField')) {
                                        delete parent.selectedAction[element.data('action')].actionParameters[element.data('index')].value;
                                    } else if (element.hasClass('dataSourceField')) {
                                        if (dataSource) {
                                            delete dataSource.lookup.value;
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
                                    if(parent.selectedAction[element.data('action')].actionParameters[element.data('index')].type === "DATE") {
                                        parent.selectedAction[element.data('action')].actionParameters[element.data('index')].value = eventKey;
                                    } else {
                                        parent.selectedAction[element.data('action')].actionParameters[element.data('index')].value = value.insert(pos, eventKey);
                                    }
                                } else if (element.hasClass('dataSourceField')) {
                                    if (dataSource) {
                                        dataSource.lookup.value = value.insert(pos, eventKey);
                                    }
                                }
                            } else {
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

                                if (dragElement.data('type') !== 'INTEGER' || dragElement.data('type') !== 'DOUBLE') {
                                    dragElement.attr("manipulationpopover", "");
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
                    }
                });
            }
        };
    });

    widgetModule.directive('integer', function () {
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

    widgetModule.directive('double', function () {
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

    widgetModule.directive('readOnly', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.keypress(function (evt) {
                    return false;
                });
            }
        };
    });

    widgetModule.directive('contenteditable', function ($compile) {
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
                    return ngModel.$setViewValue(container.html());
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

    widgetModule.directive('editableContent', function ($compile, $timeout, $http, $templateCache) {
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

    widgetModule.directive('manipulationpopover', function ($compile, $templateCache, $http) {
        return {
            restrict: 'A',
            link: function (scope, el, attrs) {
                var manipulationOptions = '', title = '', loader, elType = el.data('type'), msgScope = scope;

                while (msgScope.msg === undefined) {
                    msgScope = msgScope.$parent;
                }

                if (elType === 'UNICODE' || elType === 'TEXTAREA') {
                    title = msgScope.msg('task.stringManipulation', '');
                    loader = $http.get('../tasks/partials/widgets/string-manipulation.html', {cache: $templateCache})
                        .success(function (html) {
                            manipulationOptions = html;
                        });
                } else if (elType === 'DATE') {
                    title = msgScope.msg('task.dateManipulation', '');
                    loader = $http.get('../tasks/partials/widgets/date-manipulation.html', {cache: $templateCache})
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

                            elem.find("span").replaceWith(function () {
                                return $(this)[0].outerHTML;
                            });

                            if (manipulation !== undefined && manipulation.indexOf('date') === -1) {

                                scope.cleanArray = function() {
                                    var indexArray = scope.sortableArrayTemp.indexOf("");
                                    if (indexArray !== -1) {
                                        scope.sortableArrayTemp.splice(indexArray,1);
                                    }
                                };

                                scope.setSortable = function(elemen, index) {
                                    if(elemen.indexOf('join') !== -1) {
                                        elemen = elemen.replace(elemen, 'join');
                                    }
                                    elem.find("span[setmanipulation="+elemen+"]").replaceWith(function () {
                                        if (elemen !== undefined && elemen.indexOf(this.attributes.getNamedItem('setmanipulation').value) !== -1) {
                                            $(this).append('<span class="icon-ok" style="float: right;"></span>');
                                            $(this).parent().addClass('active');
                                            if (manipulation.indexOf("join") !== -1) {
                                                $(this.nextElementSibling).css({ 'display' : '' });
                                                elem.find('input').val(manipulation.slice(manipulation.indexOf("join") + 5, manipulation.indexOf(")")));
                                            } else {
                                                elem.find('input').val("");
                                            }
                                            $(elem[0]).append($(this).parent().clone().end());
                                        }
                                        return $(this)[0].outerHTML;
                                    });

                                };

                            scope.sortableArrayTemp = manipulation.split(" ");
                            scope.sortableArrayTemp.forEach(scope.cleanArray);
                            scope.sortableArrayTemp.forEach(scope.setSortable);
                            }

                            if ($(elem).children().is("input[id='dateFormat']")) {
                                element = $("[ismanipulate=true]");
                                manipulation = element.attr('manipulate');

                                if (manipulation !== undefined) {
                                    elem.first().append('<span class="icon-remove" style="float: right;"></span>');
                                    elem.first().append('<span class="icon-ok" style="float: right;"></span>');

                                    elem[4].children[0].value = manipulation.slice(manipulation.indexOf("dateTime") + 9, manipulation.indexOf(")"));
                                } else {
                                    elem.find('input').val("");
                                }
                            }

                            return $compile(elem)(msgScope);
                        },
                        placement: "left",
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

                        $('span.icon-remove').click(function () {
                            $(this.parentElement).children().remove();
                            $("[ismanipulate=true]").removeAttr("manipulate");
                            $("#dateFormat").val('');
                        });

                        $('.box-content').click(function () {
                            $('.hasPopoverShow').each(function () {
                                $(this).popover('hide');
                                $(this).removeClass('hasPopoverShow');
                                $(this).removeAttr('ismanipulate');
                            });
                        });
                    });
                }
            }
        };
    });

    widgetModule.directive('datetimePicker', function () {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.focus(function () {
                    $(this).prev('input').datetimepicker('show');
                });
            }
        };
    });

    widgetModule.directive('datetimePickerInput', function () {
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
                        parent.selectedAction[$(this).data('action')].actionParameters[$(this).data('index')].value = dateTex;
                        parent.$apply();
                    }
                });
            }
        };
    });

    widgetModule.directive('timePickerInput', function () {
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
                        parent.selectedAction[$(this).data('action')].actionParameters[$(this).data('index')].value = dateTex;
                        parent.$apply();
                    }
                });
            }
        };
    });

    widgetModule.directive('setmanipulation', function () {
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
                        scope.setSortableArray();
                        ui.item.data('start', ui.item.index());
                    };

                    scope.dragEnd = function(e, ui) {
                        var start = ui.item.data('start'),
                        end = ui.item.index();
                        scope.sortableArray.splice(end, 0,
                        scope.sortableArray.splice(start, 1)[0]);
                        if(scope.sortableArray.length) {
                            scope.manipulations = scope.sortableArray.join(" ");
                        }
                        manipulateElement.attr('manipulate', scope.manipulations);
                        scope.$apply();
                    };

                    sortableElement = $('#sortable').sortable({
                        start: scope.dragStart,
                        update: scope.dragEnd
                    });
                });

                el.bind("click", function () {
                    var manipulateElement = $("[ismanipulate=true]"), joinSeparator = "", reg, manipulation, manipulateAttributes, manipulationAttributesIndex;

                    if (manipulateElement.data('type') !== "DATE") {
                        manipulation = this.getAttribute("setManipulation");
                        manipulateAttributes = manipulateElement.attr("manipulate") || "";

                        if (manipulateAttributes.indexOf(manipulation) !== -1) {
                            manipulationAttributesIndex = manipulateElement.attr("manipulate").indexOf(manipulation);

                            if (manipulation !== "join") {
                                reg = new RegExp(manipulation, "g");
                                manipulateAttributes = manipulateAttributes.replace(reg, '');
                            } else {
                                joinSeparator = manipulation + "\\(" + this.nextElementSibling.value + "\\)";
                                reg = new RegExp(joinSeparator, "g");
                                manipulateAttributes = manipulateAttributes.replace(reg, '');
                            }
                        } else {
                            manipulateAttributes = manipulateAttributes.replace(/ +(?= )/g, '');

                            if (manipulation !== "join") {
                                manipulateAttributes = manipulateAttributes + manipulation + " ";
                            } else {
                                $("#joinSeparator").val("");
                                manipulateAttributes = manipulateAttributes + manipulation + "()" + " ";
                            }
                        }

                        manipulateElement.attr('manipulate', manipulateAttributes);
                        scope.setSortableArray();
                    }

                    if (this.children.length === 0) {
                        $(this).append('<span class="icon-ok" style="float: right;"></span>');
                        $(this.nextElementSibling).css({ 'display' : '' });
                        $(this).parent().addClass('active');
                        $('#sortable').append($(this.parentElement).clone().end());
                    } else {
                        $(this).children().remove();
                        $(this.nextElementSibling).css({ 'display' : 'none' });
                        $(this).parent().removeClass("active");
                        $('#sortable-no').append($(this.parentElement).clone().end());
                    }
                });

                el.bind("focusout focusin keyup", function (event) {
                    event.stopPropagation();
                    var dateFormat = this.value, manipulateElement = $("[ismanipulate=true]"), deleteButton, manipulation;

                    if (manipulateElement.data("type") === 'DATE') {
                        deleteButton = $('<span class="icon-remove" style="float: right;"></span>');
                        manipulation = this.getAttribute("setManipulation") + "(" + dateFormat + ")";
                        manipulateElement.removeAttr("manipulate");

                        if (dateFormat.length !== 0) {
                            manipulateElement.attr("manipulate", manipulation);

                            if (this.parentElement.parentElement.firstChild.children.length === 0) {
                                $(this.parentElement.parentElement.firstChild).append(deleteButton);
                                $(this.parentElement.parentElement.firstChild).append('<span class="icon-ok" style="float: right;"></span>');
                            }
                        } else if (dateFormat.length === 0) {
                            $(this.parentElement.parentElement.firstChild).children().remove();
                        }
                        $('span.icon-remove').click(function () {
                            $(this.parentElement).children().remove();
                            $("[ismanipulate=true]").removeAttr("manipulate");
                            $("#dateFormat").val('');
                        });
                    }
                });
            }
        };
    });

    widgetModule.directive('joinUpdate', function () {
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
                });
            }
        };
    });

    widgetModule.directive('ngTrigger', function (ManageTaskUtils, $compile) {
        return function(scope, elem, attrs) {
            elem.bind("focusout keyup", function(event) {
                    scope.$apply(attrs.ngTrigger);
            });
        };
    });

    widgetModule.directive('selectEvent', function() {
        return function(scope, element, attrs) {
            $(element).click(function (event) {
                var li = $(element).parent('li'),
                    content = $(element).find('.content-task'),
                    visible = content.is(":visible"),
                    other = $('[select-event=' + attrs.selectEvent + ']').not('#' + $(this).attr('id'));

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
                        if (($(window).width() - $(this).offset().left) < 138 + $(content).width() && $(this).offset().left > $(content).width()) {
                            content.addClass('left');
                            content.css({'left': function() {return -($(content).width() + 3);}});
                            content.css({'top': function() {return -($(content).height()/2 - 60);}});
                        } else if (($(window).width() - ($(this).offset().left + 138)) > $(content).width() && $(content).width() < $(this).offset().left)  {
                            content.addClass('right');
                            content.css({'left': '125px'});
                            content.css({'top': function() {return -($(content).height()/2 - 60);}});
                        } else if ($(document).height() - $(this).offset().top < $(content).height() + 125) {
                            content.addClass('top');
                            content.css({'top': function() {return -($(content).height()+8);}});
                            content.css({'left': function() {return -($(content).width()/2 - 60);}});
                            content.parent().find('div.arrow').css({'top': function() {return ($(content).height()+2);}});
                        } else {
                            content.addClass('bottom');
                            content.css({'top': '115px'});
                            content.css({'left': function() {return -($(content).width()/2 - 60);}});
                            content.parent().find('div.arrow').css({'top':'-11px'});
                        }
                    }
            });
        };
    });

    widgetModule.directive('helpPopover', function($compile, $http) {
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

    widgetModule.directive('divPlaceholder', function() {
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

    widgetModule.directive('actionSortableCursor', function () {
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

    widgetModule.directive('actionsPopover', function () {
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

    widgetModule.directive('triggerPopover', function () {
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

}());
