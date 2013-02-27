var widgetModule = angular.module('motech-tasks');

widgetModule.directive('doubleClick', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.dblclick(function () {
                var parent = element.parent();

                if (parent.hasClass('trigger')) {
                    delete scope.selectedTrigger;
                    delete scope.task.trigger;
                    scope.draggedTrigger.display = scope.draggedTrigger.channel;
                } else if (parent.hasClass('action')) {
                    delete scope.selectedAction;
                    delete scope.task.action;
                    scope.draggedAction.display = scope.draggedAction.channel;
                }

                scope.$apply();
            });
        }
    }
})

widgetModule.directive('overflowChange', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            $('#collapse1').on({
                shown: function(){
                    $(this).css('overflow','visible');
                },
                hide: function(){
                    $(this).css('overflow','hidden');
                }
            });
        }
    }
});

widgetModule.directive('expandaccordion', function () {
     return {
         restrict: 'A',
         link: function (scope, element, attrs) {
             $('.accordion').on('show', function (e) {
                 $(e.target).siblings('.accordion-heading').find('.accordion-toggle i').removeClass("icon-chevron-right").addClass('icon-chevron-down');
             });
             $('.tasks-list').on('show', function (e) {
                 $(e.target).siblings('.accordion-heading').find('.accordion-toggle i').removeClass("icon-chevron-right").addClass('icon-chevron-down');
             });
             $('.accordion').on('hide', function (e) {
                 $(e.target).siblings('.accordion-heading').find('.accordion-toggle i').removeClass("icon-chevron-down").addClass("icon-chevron-right");
             });
             $('.tasks-list').on('hide', function (e) {
                 $(e.target).siblings('.accordion-heading').find('.accordion-toggle i').removeClass("icon-chevron-down").addClass('icon-chevron-right');
             });
         }
     }
 })

widgetModule.directive('draggable', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.draggable({
                revert: true,
                start: function (event, ui) {
                    if (element.hasClass('draggable')) {
                        element.find("div:first-child").popover('hide');
                    }
                }
            });
        }
    }
})

widgetModule.directive('droppable', function ($compile) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.droppable({
                drop: function (event, ui) {
                    var channelName, moduleName, moduleVersion, parent, value, pos, eventKey, dropElement, dragElement, browser;

                    var position = function(dropElement, dragElement) {
                       var sel, range;
                       var space = document.createTextNode(' ');
                       if (window.getSelection) {
                           sel = window.getSelection();
                           if (sel.getRangeAt && sel.rangeCount && sel.anchorNode.parentNode.tagName.toLowerCase() != 'span') {
                               range = sel.getRangeAt(0);
                               if (range.commonAncestorContainer.parentNode == dropElement[0] || range.commonAncestorContainer == dropElement[0] || range.commonAncestorContainer.parentNode.parentNode ==dropElement[0]) {
                                   var el = document.createElement("div");
                                   el.innerHTML = dragElement[0].outerHTML;
                                   var frag = document.createDocumentFragment(), node, lastNode;

                                   while ( (node = el.firstChild) ) {
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
                            } else if (document.selection && document.selection.type != "Control") {
                                document.selection.createRange().pasteHTML($compile(dragElement[0].outerHTML)(scope));
                            }
                        };

                    if (angular.element(ui.draggable).hasClass('triggerField') && element.hasClass('actionField')) {
                        dragElement = angular.element(ui.draggable);
                        dropElement = angular.element(element);
                        browser = scope.$parent.BrowserDetect.browser

                        parent = scope;

                        while (parent.msg === undefined) {
                            parent = parent.$parent;
                        }

                        if (dropElement.data('type') === 'DATE') {
                            delete scope.selectedAction.eventParameters[dropIndex].value;
                        }

                        if (browser != 'Chrome' && browser != 'Explorer') {
                            if (dragElement.data('prefix') === 'trigger') {
                                eventKey = '{{trigger.' + scope.selectedTrigger.eventParameters[dragElement.data('index')].eventKey + '}}';
                            } else if (dragElement.data('prefix') === 'ad') {
                                eventKey = '{{ad.' + parent.msg(dragElement.data('source')) + '.' + dragElement.data('object-type') + "#" + dragElement.data('object-id') + '.' + dragElement.data('field') + '}}';
                            }

                            pos = element.caret();
                            value = scope.selectedAction.eventParameters[dropElement.data('index')].value || '';

                            scope.selectedAction.eventParameters[dropElement.data('index')].value = value.insert(pos, eventKey);
                        } else {
                            dropElement.find('em').remove();

                            var dragElement = angular.element(ui.draggable).clone();
                            dragElement.css("position", "relative");
                            dragElement.css("left", "0px");
                            dragElement.css("top", "0px");
                            dragElement.attr("unselectable", "on");
                            if (dragElement.data('type') != 'NUMBER') {
                                dragElement.attr("manipulationpopover", "");
                            }

                            dragElement.addClass('pointer');
                            dragElement.addClass('popoverEvent');
                            dragElement.addClass('nonEditable');
                            dragElement.removeAttr("ng-repeat");
                            dragElement.removeAttr("draggable");

                            if (dragElement.data('prefix') === 'ad') {
                                dragElement.text(
                                    parent.msg(dragElement.data('source')) + '.' +
                                    parent.msg(dragElement.data('object')) + "#" +
                                    dragElement.data('object-id') + '.' +
                                    dragElement.text()
                                );
                            }

                            position(dropElement, dragElement);
                        }

                    } else if (angular.element(ui.draggable).hasClass('task-panel') && (element.hasClass('trigger') || element.hasClass('action'))) {
                        channelName = angular.element(ui.draggable).data('channel-name');
                        moduleName = angular.element(ui.draggable).data('module-name');
                        moduleVersion = angular.element(ui.draggable).data('module-version');

                        if (element.hasClass('trigger')) {
                            scope.setTaskEvent('trigger', channelName, moduleName, moduleVersion);
                            delete scope.task.trigger;
                            delete scope.selectedTrigger;
                        } else if (element.hasClass('action')) {
                            scope.setTaskEvent('action', channelName, moduleName, moduleVersion);
                            delete scope.task.action;
                            delete scope.selectedAction;
                        }
                    } else if (angular.element(ui.draggable).hasClass('dragged') && element.hasClass('task-selector')) {
                        parent = angular.element(ui.draggable).parent();

                        if (parent.hasClass('trigger')) {
                            delete scope.draggedTrigger;
                            delete scope.task.trigger;
                            delete scope.selectedTrigger;
                        } else if (parent.hasClass('action')) {
                            delete scope.draggedAction;
                            delete scope.task.action;
                            delete scope.selectedAction;
                        }
                    }

                    scope.$apply();
                }
            });
        }
    }
})

widgetModule.directive('number', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.keypress(function (evt) {
                var charCode = (evt.which) ? evt.which : evt.keyCode,
                    allow = [8, 44, 46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57]; // char code: , . 0 1 2 3 4 5 6 7 8 9

                return allow.indexOf(charCode) >= 0;
            });
        }
    };
})

widgetModule.directive('contenteditable', function($compile) {
    return {
        restrict: 'A',
        require: '?ngModel',
        link: function(scope, element, attrs, ngModel) {
            var read;
            if (!ngModel) {
                return;
            }
            ngModel.$render = function() {
                var container = $('<div></div>');
                container.html(ngModel.$viewValue);
                container.find('.editable').attr('contenteditable', true);
                if (container.text()!="") {
                    container = container.contents();
                    container.each(function() {
                        if (this.localName == 'span') {
                            return $compile(this)(scope);
                        }
                    });
                } else {
                    container = container.html();
                }
                return element.html(container);
            };

            element.bind('focusin', function(event) {
            event.stopPropagation();
            var el = this;
                window.setTimeout(function() {
                    var sel, range;
                    if (window.getSelection && document.createRange) {
                        range = document.createRange();
                        range.selectNodeContents(el);
                        if (el.childNodes.length!=0) {
                            range.setStartAfter(el.childNodes[el.childNodes.length-1]);
                        }
                        range.collapse(true);
                        sel = window.getSelection();
                        sel.removeAllRanges();
                        sel.addRange(range);
                    } else if (document.body.createTextRange) {
                        range = document.body.createTextRange();
                        if (el.childNodes.length!=0) {
                            range.moveToElementText(el.childNodes[el.childNodes.length-1]);
                        }
                        range.collapse(true);
                        range.select();
                    }
                }, 1);
            });

            element.bind('keypress', function(event) {
                if ($(this).data('type')!='TEXTAREA') {
                    return event.which != 13;
                }
            })

            element.bind('blur keyup change mouseleave', function(event) {
                event.stopPropagation();
                if (ngModel.$viewValue !== $.trim(element.html())) {
                    return scope.$apply(read);
                }
            });

            return read = function() {
                var container = $('<div></div>');
                if(element.html()=="<br>") {
                    element.find('br').remove();
                }
                container.html($.trim(element.html()));
                container.find('.editable').attr('contenteditable', false);
                container.find('.popover').remove();
                return ngModel.$setViewValue(container.html());
            };
        }
    }
})

widgetModule.directive('editableContent', function($compile, $timeout, $http , $templateCache) {
    var templateText, templateLoader,
        baseURL = '../tasks/partials/widgets/',
        typeTemplateMapping = {
            TEXTAREA : 'content-editable-textarea.html',
            NUMBER: 'content-editable-number.html',
            UNICODE : 'content-editable-unicode.html',
            DATE : 'content-editable-date.html'
        };

   return {
       restrict: 'E',
       replace : true,
       transclude: true,
       scope: {
           data: '=',
           index: '='
       },
       compile: function(tElement, tAttrs, scope) {
             var tplURL = baseURL + typeTemplateMapping[tAttrs.type];
             templateLoader = $http.get(tplURL, {cache: $templateCache})
               .success(function(html) {
                 tElement.html(html);
               });

             return function (scope, element, attrs) {
               templateLoader.then(function (templateText) {
                 element.html($compile(tElement.html())(scope));
               });
               $timeout(function () {
                  element.find('div').focusout();
               });
             }
       }

   }
})

widgetModule.directive('manipulationpopover', function($compile, $timeout, $templateCache, $http) {
    return {
        restrict: 'A',
        link: function(scope, el, attrs) {
            var manipulationOptions = '', title = '', templatePopover='';
            var loader;
            var elType = el.data('type');
            var msgScope = scope;
            while (msgScope.msg==undefined) {
                msgScope = msgScope.$parent;
            }
            if (elType == 'UNICODE' || elType == 'TEXTAREA') {
                title = msgScope.msg('stringManipulation', '');
                loader = $http.get('../tasks/partials/widgets/string-manipulation.html', {cache: $templateCache})
                    .success(function(html) {
                        manipulationOptions = html;
                });
            } else if (elType == 'DATE') {
                title = msgScope.msg('dateManipulation', '');
                loader = $http.get('../tasks/partials/widgets/date-manipulation.html', {cache: $templateCache})
                    .success(function(html) {
                        manipulationOptions = html;
                });
            }

            el.bind('click', function() {
                var man = $("[ismanipulate=true]").text();
                if (man.length == 0) {
                    angular.element(this).attr('ismanipulate', 'true');
                } else {
                    angular.element(this).removeAttr('ismanipulate');
                }
            });


            if (elType == 'UNICODE' || elType == 'TEXTAREA' || elType == 'DATE') {
                el.popover({
                    template : '<div unselectable="on" contenteditable="false" class="popover dragpopover"><div unselectable="on" class="arrow"></div><div unselectable="on" class="popover-inner"><h3 unselectable="on" class="popover-title"></h3><div unselectable="on" class="popover-content"><p unselectable="on"></p></div></div></div>',
                    title: title,
                    html: true,
                    content: function() {
                        var elem = $(manipulationOptions);
                        $compile(elem)(msgScope);
                        msgScope.$apply(elem);
                        elem.find('span').replaceWith(function() {
                            var element = $("[ismanipulate=true]");
                            var manipulation = element.attr('manipulate');
                            if (manipulation != undefined && manipulation.indexOf(this.attributes.getNamedItem('setmanipulation').value) != -1) {
                                $(this).append('<span class="icon-ok" style="float: right"></span>');
                                if (manipulation.indexOf("join") != -1) {
                                    $(this.nextElementSibling).css({ 'display' : '' });
                                    elem.find('input').val(manipulation.slice(manipulation.indexOf("join")+5, manipulation.indexOf(")")));
                                } else {
                                    elem.find('input').val("");
                                }
                            }
                            return $(this)[0].outerHTML;
                        });
                        if (elem.is('span')) {
                           var element = $("[ismanipulate=true]");
                           var manipulation = element.attr('manipulate');
                           if (manipulation != undefined) {
                               elem.first().append('<span class="icon-remove" style="float: right"></span>');
                               elem.first().append('<span class="icon-ok" style="float: right"></span>');
                               elem[4].children[0].value = manipulation.slice(manipulation.indexOf("dateTime")+9, manipulation.indexOf(")"));
                           } else {
                               elem.find('input').val("");
                           }
                        }
                        return $compile(elem)(msgScope);;
                    },
                    placement: "top",
                    trigger: 'manual'
                }).click(function(event) {
                    event.stopPropagation();
                    if (!$(this).hasClass('hasPopoverShow')) {
                        var otherPopoverElem = $('.hasPopoverShow');
                        if (otherPopoverElem != undefined && $(this) != otherPopoverElem) {
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

                    $('.dragpopover').click(function(event) {
                            event.stopPropagation();
                    });

                    $('.dragpopover').mousedown(function(event) {
                            event.stopPropagation();
                    });

                    $('span.icon-remove').click(function() {
                        $(this.parentElement).children().remove();
                        $("[ismanipulate=true]").removeAttr("manipulate");
                        $("#dateFormat").val('');
                    });

                    $('.box-content').click(function(event) {
                        $('.hasPopoverShow').each(function() {
                            $(this).popover('hide');
                            $(this).removeClass('hasPopoverShow');
                            $(this).removeAttr('ismanipulate');
                        });
                    });
                });
            }
        }
    };
})

widgetModule.directive('datetimePickerInput', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.datetimepicker({
                showTimezone: true,
                useLocalTimezone: true,
                dateFormat: 'yy-mm-dd',
                timeFormat: 'HH:mm z',
                onSelect: function(dateTex) {
                    scope.selectedAction.eventParameters[$(this).data('index')].value = dateTex;
                    scope.$apply();
                }
            });
        }
    }
})

widgetModule.directive('setmanipulation', function() {
    return {
        restrict : 'A',
        require: '?ngModel',
        link : function (scope, el, attrs, ngModel) {

            el.bind("click", function() {
                var manipulateElement = $("[ismanipulate=true]");
                var joinSeparator = "";
                var reg;
                if (manipulateElement.data('type') != "DATE") {
                    var manipulation = this.getAttribute("setManipulation");
                    var manipulateAttributes = manipulateElement.attr("manipulate") ? manipulateElement.attr("manipulate") : "";
                    if (manipulateAttributes.indexOf(manipulation) != -1) {
                        var manipulationAttributesIndex = manipulateElement.attr("manipulate").indexOf(manipulation);
                        if (manipulation != "join") {
                            reg = new RegExp(manipulation, "g")
                            manipulateAttributes = manipulateAttributes.replace(reg, '');
                        } else {
                            joinSeparator = manipulation + "\\(" + this.nextElementSibling.value + "\\)";
                            reg = new RegExp(joinSeparator, "g")
                            manipulateAttributes = manipulateAttributes.replace(reg, '');
                        }
                    } else {
                        manipulateAttributes = manipulateAttributes.replace(/ +(?= )/g, '');
                        if (manipulation != "join") {
                            manipulateAttributes = manipulateAttributes + manipulation + " ";
                        } else {
                            $("#joinSeparator").val("")
                            manipulateAttributes = manipulateAttributes + manipulation + "()" + " ";
                        }
                    }
                    manipulateElement.attr('manipulate', manipulateAttributes);
                }
                if (this.children.length == 0) {
                   $(this).append('<span class="icon-ok" style="float: right"></span>');
                   $(this.nextElementSibling).css({ 'display' : '' });
                } else {
                    $(this).children().remove();
                    $(this.nextElementSibling).css({ 'display' : 'none' });
                }
            });

            el.bind("focusout focusin keyup", function(event) {
                event.stopPropagation();
                var dateFormat = this.value;
                var manipulateElement = $("[ismanipulate=true]");
                if (manipulateElement.data("type") == 'DATE') {
                    var deleteButton = $('<span class="icon-remove" style="float: right"></span>');
                    var manipulation = this.getAttribute("setManipulation") + "("+ dateFormat + ")";
                        manipulateElement.removeAttr("manipulate");
                    if (dateFormat.length != 0) {
                        manipulateElement.attr("manipulate", manipulation);
                        if (this.parentElement.parentElement.firstChild.children.length == 0) {
                            $(this.parentElement.parentElement.firstChild).append(deleteButton);
                            $(this.parentElement.parentElement.firstChild).append('<span class="icon-ok" style="float: right"></span>');
                        }
                    } else if (dateFormat.length == 0) {
                        $(this.parentElement.parentElement.firstChild).children().remove();
                    }
                    $('span.icon-remove').click(function() {
                        $(this.parentElement).children().remove();
                        $("[ismanipulate=true]").removeAttr("manipulate");
                        $("#dateFormat").val('');
                    });
                }
            });
        }
    }
})

widgetModule.directive('joinUpdate', function() {
      return {
          restrict : 'A',
          require: '?ngModel',
          link : function (scope, el, attrs, ngModel) {
              el.bind("focusout focusin keyup", function(event) {
                  event.stopPropagation();
                  var manipulateElement = $("[ismanipulate=true]");
                  var manipulation = "join("+ $("#joinSeparator").val() + ")";
                  var elementManipulation = manipulateElement.attr("manipulate") ;
                  elementManipulation = elementManipulation.replace(/join\(.*?\)/g, manipulation);
                  manipulateElement.attr("manipulate", elementManipulation);
              });
          }
      }
});

widgetModule.directive('integer', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.keypress(function (evt) {
                var charCode = (evt.which) ? evt.which : evt.keyCode,
                    caret = element.caret(), value = element.val(),
                    begin = value.indexOf('{{'), end = value.indexOf('}}') + 2;

                if (begin !== -1) {
                    while (end !== -1) {
                        if (caret > begin && caret < end) {
                            return false;
                        }

                        begin = value.indexOf('{{', end);
                        end = begin === -1 ? -1 : value.indexOf('}}', begin) + 2;
                    }
                }

                return !(charCode > 31 && (charCode < 48 || charCode > 57));
            });
        }
    };
})
