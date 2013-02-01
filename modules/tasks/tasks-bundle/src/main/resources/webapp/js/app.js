'use strict';

/* App Module */

angular.module('motech-tasks', ['motech-dashboard', 'channelServices', 'taskServices', 'activityServices', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/dashboard', {templateUrl: '../tasks/partials/tasks.html', controller: DashboardCtrl}).
            when('/task/new', {templateUrl: '../tasks/partials/form.html', controller: ManageTaskCtrl}).
            when('/task/:taskId/edit', {templateUrl: '../tasks/partials/form.html', controller: ManageTaskCtrl}).
            when('/task/:taskId/log', {templateUrl: '../tasks/partials/history.html', controller: LogCtrl}).
            otherwise({redirectTo: '/dashboard'});
    }
]).filter('filterPagination', function () {
    return function (input, start) {
        start = +start;
        return input.slice(start);
    }
}).filter('fromNow', function () {
    return function(date) {
        return moment(date).fromNow();
    };
}).directive('doubleClick', function () {
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
}).directive('expandaccordion', function () {
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
}).directive('draggable', function () {
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
}).directive('droppable', function ($compile) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.droppable({
                drop: function (event, ui) {
                    var dragIndex, dropIndex, channelName, moduleName, moduleVersion,
                        parent, value, position, eventKey, dragType, dropType;

                    if (angular.element(ui.draggable).hasClass('triggerField') && element.hasClass('actionField')) {
                        dragIndex = angular.element(ui.draggable).data('index');
                        dropIndex = angular.element(element).data('index');
                        dragType = angular.element(ui.draggable).data('type');
                        dropType = angular.element(element).data('type');

                        if ((dragType == 'UNICODE' || dragType == 'TEXTAREA') && dropType == 'NUMBER') {
                            return;
                        }

                        if (dropType === 'DATE') {
                            delete scope.selectedAction.eventParameters[dropIndex].value;
                        }

                        eventKey = '{{' + scope.selectedTrigger.eventParameters[dragIndex].eventKey + '}}';
                        position = element.caret();
                        value = scope.selectedAction.eventParameters[dropIndex].value || '';

                        scope.selectedAction.eventParameters[dropIndex].value = value.insert(position, eventKey);
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
}).directive('integer', function () {
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
}).directive('datetimePickerInput', function() {
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
});
