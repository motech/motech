(function () {

    'use strict';

    /* Controllers */

    var widgetModule = angular.module('motech-tasks');

    widgetModule.controller('DashboardCtrl', function ($scope, $filter, Tasks, Activities) {
        var RECENT_TASK_COUNT = 7, tasks, activities = [],
            searchMatch = function (item, method, searchQuery) {
                var result;

                if (!searchQuery) {
                    if (method === 'pausedTaskFilter') {
                        result = item.task.enabled === true;
                    } else if (method === 'activeTaskFilter') {
                        result = item.task.enabled === false;
                    } else if (method === 'noItems') {
                        result = false;
                    } else {
                        result = true;
                    }
                } else if (method === 'pausedTaskFilter' && item.task.description) {
                    result = item.task.enabled === true && (item.task.description.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1 || item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1);
                } else if (method === 'activeTaskFilter' && item.task.description) {
                    result = item.task.enabled === false && (item.task.description.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1 || item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1);
                } else if (method === 'activeTaskFilter' && item.task.description) {
                    result = item.task.description.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1 || item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
                } else if (method === 'allItems' && item.task.description) {
                    result = item.task.description.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1 || item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
                } else if (method === 'pausedTaskFilter') {
                    result = item.task.enabled === true && item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
                } else if (method === 'activeTaskFilter') {
                    result = item.task.enabled === false && item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
                } else if (method === 'noItems') {
                    result = false;
                } else {
                    result = item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
                }

                return result;
            };

        $scope.resetItemsPagination();
        $scope.allTasks = [];
        $scope.activities = [];
        $scope.hideActive = false;
        $scope.hidePaused = false;
        $scope.filteredItems = [];
        $scope.itemsPerPage = 10;
        $scope.currentFilter = 'allItems';

        tasks = Tasks.query(function () {
            activities = Activities.query(function () {
                var item, i, j;

                for (i = 0; i < tasks.length; i += 1) {
                    item = {
                        task: tasks[i],
                        success: 0,
                        error: 0
                    };

                    for (j = 0; j < activities.length; j += 1) {
                        if (activities[j].task === item.task._id && activities[j].activityType === 'SUCCESS') {
                            item.success += 1;
                        }

                        if (activities[j].task === item.task._id && activities[j].activityType === 'ERROR') {
                            item.error += 1;
                        }
                    }
                    $scope.allTasks.push(item);
                }

                for (i = 0; i < RECENT_TASK_COUNT && i < activities.length; i += 1) {
                    for (j = 0; j < tasks.length; j += 1) {
                        if (activities[i].task === tasks[j]._id) {
                            $scope.activities.push({
                                task: activities[i].task,
                                trigger: tasks[j].trigger,
                                actions: tasks[j].actions,
                                date: activities[i].date,
                                type: activities[i].activityType,
                                name: tasks[j].name
                            });
                            break;
                        }
                    }
                }

                $scope.search();
            });
        });

        $scope.enableTask = function (item, enabled) {
            item.task.enabled = enabled;

            item.task.$save(dummyHandler, function (response) {
                item.task.enabled = !enabled;
                handleResponse('error.actionNotChangeTitle', 'error.actionNotChange', response);
            });
        };

        $scope.deleteTask = function (item) {
            jConfirm(jQuery.i18n.prop('task.confirm.remove'), jQuery.i18n.prop("header.confirm"), function (val) {
                if (val) {
                    item.task.$remove(function () {
                        $scope.allTasks.removeObject(item);
                        $scope.search();
                    }, alertHandler('task.error.removed', 'header.error'));
                }
            });
        };

        $scope.search = function () {
            $scope.filteredItems = $filter('filter')($scope.allTasks, function (item) {
                return item && searchMatch(item, $scope.currentFilter, $scope.query);
            });

            $scope.setCurrentPage(0);
            $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
        };

        $scope.setHideActive = function () {
            if ($scope.hideActive === true) {
                $scope.hideActive = false;
                $scope.setFilter($scope.hidePaused ? 'pausedTaskFilter' : 'allItems');

                $('.setHideActive').find('i').removeClass("icon-ban-circle").addClass('icon-ok');
            } else {
                $scope.hideActive = true;
                $scope.setFilter($scope.hidePaused ? 'noItems' : 'activeTaskFilter');

                $('.setHideActive').find('i').removeClass("icon-ok").addClass('icon-ban-circle');
            }
        };

        $scope.setHidePaused = function () {
            if ($scope.hidePaused === true) {
                $scope.hidePaused = false;
                $scope.setFilter($scope.hideActive ? 'activeTaskFilter' : 'allItems');

                $('.setHidePaused').find('i').removeClass("icon-ban-circle").addClass('icon-ok');
            } else {
                $scope.hidePaused = true;
                $scope.setFilter($scope.hideActive ? 'noItems' : 'pausedTaskFilter');

                $('.setHidePaused').find('i').removeClass("icon-ok").addClass('icon-ban-circle');
            }
        };

        $scope.setFilter = function (method) {
            $scope.currentFilter = method;
            $scope.search();
        };

    });

    widgetModule.controller('ManageTaskCtrl', function ($scope, ManageTaskUtils, Channels, DataSources, Tasks, $q, $timeout, $routeParams, $http, $compile) {
        $scope.util = ManageTaskUtils;
        $scope.selectedActionChannel = [];
        $scope.selectedAction = [];

        $q.all([$scope.util.doQuery($q, Channels), $scope.util.doQuery($q, DataSources)]).then(function(data) {
            blockUI();

            $scope.channels = data[0];
            $scope.dataSources = data[1];

            if ($routeParams.taskId === undefined) {
                $scope.task = {
                    taskConfig: {
                        steps: []
                    }
                };
            } else {
                $scope.task = Tasks.get({ taskId: $routeParams.taskId }, function () {
                    var triggerChannel, trigger, dataSource, object;

                    if ($scope.task.trigger) {
                        triggerChannel = $scope.util.find({
                            where: $scope.channels,
                            by: {
                                what: 'moduleName',
                                equalTo: $scope.task.trigger.moduleName
                            }
                        });

                        trigger = triggerChannel && $scope.util.find({
                            where: triggerChannel.triggerTaskEvents,
                            by: {
                                what: 'subject',
                                equalTo: $scope.task.trigger.subject
                            }
                        });

                        if (trigger) {
                            $scope.util.trigger.select($scope, triggerChannel, trigger);
                        }
                    }

                    angular.forEach($scope.task.taskConfig.steps, function (step) {
                        var source, object;

                        angular.element('#collapse-step-' + step.order).livequery(function() {
                            $(this).collapse('hide');
                        });

                        if (step['@type'] === 'DataSource') {
                            source = $scope.findDataSource(step.providerId);
                            object = $scope.util.find({
                                where: source.objects,
                                by: {
                                    what: 'type',
                                    equalTo: step.type
                                }
                            });

                            if (source && object) {
                                step.providerName = source.name;
                                step.displayName = object.displayName;
                                step.lookup.value = $scope.util.convertToView($scope, 'UNICODE', step.lookup.value);
                            }
                        }
                    });

                    angular.forEach($scope.task.actions, function (info, idx) {
                        var action, actionBy = [];

                        $scope.selectedActionChannel[idx] = $scope.util.find({
                            where: $scope.channels,
                            by: {
                                what: 'moduleName',
                                equalTo: info.moduleName
                            }
                        });

                        if ($scope.selectedActionChannel[idx]) {
                            if (info.subject) {
                                actionBy.push({ what: 'subject', equalTo: info.subject });
                            }

                            if (info.serviceInterface && info.serviceMethod) {
                                actionBy.push({ what: 'serviceInterface', equalTo: info.serviceInterface });
                                actionBy.push({ what: 'serviceMethod', equalTo: info.serviceMethod });
                            }

                            action = $scope.util.find({
                                where: $scope.selectedActionChannel[idx].actionTaskEvents,
                                by: actionBy
                            });

                            if (action) {
                                $timeout(function () {
                                    $scope.util.action.select($scope, idx, action);
                                    angular.element('#collapse-action-' + idx).collapse('hide');

                                    angular.forEach($scope.selectedAction[idx].actionParameters, function (param) {
                                        param.value = info.values[param.key] || '';
                                        param.value = $scope.util.convertToView($scope, param.type, param.value);
                                    });
                                });
                            }
                        }
                    });
                });
            }

            unblockUI();
        });

        $scope.selectTrigger = function (channel, trigger) {
            if ($scope.task.trigger) {
                motechConfirm('task.confirm.trigger', "header.confirm", function (val) {
                    if (val) {
                        $scope.util.trigger.remove($scope);
                        $scope.util.trigger.select($scope, channel, trigger);
                    }
                });
            } else {
                $scope.util.trigger.select($scope, channel, trigger);
            }
        };

        $scope.removeTrigger = function ($event) {
            $event.stopPropagation();

            motechConfirm('task.confirm.trigger', "header.confirm", function (val) {
                if (val) {
                    $scope.util.trigger.remove($scope);
                }
            });
        };

        $scope.addAction = function () {
            if (!$scope.task.actions) {
                $scope.task.actions = [];
            }

            $scope.task.actions.push({});
        };

        $scope.removeAction = function (idx) {
            motechConfirm('task.confirm.action', "header.confirm", function (val) {
                if (val) {
                    $scope.task.actions.remove(idx);
                    $scope.selectedActionChannel.remove(idx);
                    $scope.selectedAction.remove(idx);

                    if (!$scope.$$phase) {
                        $scope.$apply($scope.task);
                    }
                }
            });
        };

        $scope.selectActionChannel = function (idx, channel) {
            if ($scope.selectedActionChannel[idx] && $scope.selectedAction[idx]) {
                motechConfirm('task.confirm.action', "header.confirm", function (val) {
                    if (val) {
                        $scope.task.actions[idx] = {};
                        $scope.selectedActionChannel[idx] = channel;
                        $scope.selectedAction.remove(idx);

                        if (!$scope.$$phase) {
                            $scope.$apply($scope.task);
                        }
                    }
                });
            } else {
                $scope.selectedActionChannel[idx] = channel;
            }
        };

        $scope.getActions = function (idx) {
            return ($scope.selectedActionChannel[idx] && $scope.selectedActionChannel[idx].actionTaskEvents) || [];
        };

        $scope.selectAction = function (idx, action) {
            if ($scope.selectedAction[idx]) {
                motechConfirm('task.confirm.action', "header.confirm", function (val) {
                    if (val) {
                        $scope.util.action.select($scope, idx, action);
                    }
                });
            } else {
                $scope.util.action.select($scope, idx, action);
            }
        };

        $scope.addFilterSet = function () {
            var lastStep = $scope.task.taskConfig.steps.last();

            $scope.task.taskConfig.steps.push({
                '@type': 'FilterSet',
                filters: [],
                order: (lastStep && lastStep.order + 1) || 0
            });
        };

        $scope.removeFilterSet = function (data) {
            motechConfirm('task.confirm.filterSet', "header.confirm", function (val) {
                if (val) {
                    $scope.task.taskConfig.steps.removeObject(data);

                    if (!$scope.$$phase) {
                        $scope.$apply($scope.task);
                    }
                }
            });
        };

        $scope.addFilter = function (filterSet) {
            filterSet.filters.push({});
        };

        $scope.removeFilter = function (filterSet, filter) {
            filterSet.filters.removeObject(filter);
        };

        $scope.selectParam = function (filter, type, select, field) {
            switch(type) {
            case $scope.util.TRIGGER_PREFIX:
                filter.key = "{0}.{1}".format($scope.util.TRIGGER_PREFIX, select.eventKey);
                filter.displayName = "{0} ({1})".format($scope.msg(select.displayName), $scope.msg('header.trigger'));
                filter.type = select.type;
                break;
            case $scope.util.DATA_SOURCE_PREFIX:
                filter.key = "{0}.{1}.{2}#{3}.{4}".format($scope.util.DATA_SOURCE_PREFIX, select.providerId, select.type, select.objectId, field.fieldKey);
                filter.displayName = "{0} ({1}#{2} ({3}))".format($scope.msg(field.displayName), $scope.msg(select.displayName), select.objectId, $scope.msg(select.providerName));
                filter.type = field.type;
                break;
            }
        };

        $scope.operators = function (param) {
            var array = ['exist'];

            if (param) {
                if ($scope.util.isText(param.type)) {
                    $.merge(array, ["equals", "contains", "startsWith", "endsWith"]);
                } else if ($scope.util.isNumber(param.type)) {
                    $.merge(array, ["gt", "lt", "equal"]);
                }
            }

            return array;
        };

        $scope.addDataSource = function () {
            var sources = $scope.getDataSources(),
                lastStep = $scope.task.taskConfig.steps.last(),
                last;

            last = sources && sources.last();

            $scope.task.taskConfig.steps.push({
                '@type': 'DataSource',
                objectId: (last && last.objectId + 1) || 0,
                order: (lastStep && lastStep.order + 1) || 0
            });
        };

        $scope.removeData = function (dataSource) {
            motechConfirm('task.confirm.dataSource', "header.confirm", function (val) {
                if (val) {
                    $scope.task.taskConfig.steps.removeObject(dataSource);

                    if (!$scope.$$phase) {
                        $scope.$apply($scope.task);
                    }
                }
            });
        };

        $scope.getDataSources = function () {
            return $scope.util.find({
                where: $scope.task.taskConfig.steps,
                by: [{
                    what: '@type',
                    equalTo: 'DataSource'
                }],
                unique: false
            });
        };

        $scope.findDataSource = function (providerId) {
            return $scope.util.find({
                where: $scope.dataSources,
                by: {
                    what: '_id',
                    equalTo: providerId
                }
            });
        };

        $scope.findObject = function (providerId, type, id) {
            var dataSource = $scope.findDataSource(providerId),
                by,
                found;

            if (dataSource) {
                by = [];

                if (type) {
                    by.push({ what: 'type', equalTo: type });
                }

                if (id) {
                    by.push({ what: 'id', equalTo: id});
                }

                found = $scope.util.find({
                    where: dataSource.objects,
                    by: by
                });
            }

            return found;
        };

        $scope.selectDataSource = function (dataSource, selected) {
            if (dataSource.providerId) {
                motechConfirm('task.confirm.changeDataSource', 'header.confirm', function (val) {
                    if (val) {
                        $scope.util.dataSource.select($scope, dataSource, selected);
                    }
                });
            } else {
                $scope.util.dataSource.select($scope, dataSource, selected);
            }
        };

        $scope.selectObject = function (object, selected) {
            if (object.type) {
                motechConfirm('task.confirm.changeObject', 'header.confirm', function (val) {
                    if (val) {
                        $scope.util.dataSource.selectObject($scope, object, selected);
                    }
                });
            } else {
                $scope.util.dataSource.selectObject($scope, object, selected);
            }
        };

        $scope.selectLookup = function(data, lookup) {
            data.lookup = {};
            data.lookup.field = lookup;
        };

        $scope.refactorDivEditable = function (value) {
            var result = $('<div/>').append(value),
                isChrome = $scope.util.isChrome($scope),
                isIE = $scope.util.isIE($scope);

            result.find('em').remove();

            result.find('span[data-prefix]').replaceWith(function () {
                var span = $(this), prefix = span.data('prefix'),
                    manipulations = span.attr('manipulate') || '',
                    type = span.data('type'),
                    object = {}, key, source, array, val, i;

                switch (prefix) {
                case $scope.util.TRIGGER_PREFIX:
                    for (i = 0; i < $scope.selectedTrigger.eventParameters.length; i += 1) {
                        if ($scope.msg($scope.selectedTrigger.eventParameters[i].displayName) === $(this).text()) {
                            key = $scope.selectedTrigger.eventParameters[i].eventKey;
                            break;
                        }
                    }
                    break;
                case $scope.util.DATA_SOURCE_PREFIX:
                    source = span.data('source');
                    object.type = span.data('object-type');
                    object.id = span.data('object-id');
                    key = span.data('field');
                    break;
                default:
                    key = span.data('value').toString();
                }

                if (manipulations !== "") {
                    if ($scope.util.isText(type)) {
                        array = manipulations.split(" ");

                        for (i = 0; i < array.length; i += 1) {
                            key = key.concat("?" + array[i]);
                        }
                    } else if ($scope.util.isDate(type)) {
                        key = key.concat("?" + manipulations);
                    }
                }

                key = key.replace(/\?+(?=\?)/g, '');

                switch (prefix) {
                case $scope.util.TRIGGER_PREFIX:
                    val = '{{{0}.{1}}}'.format(prefix, key);
                    break;
                case $scope.util.DATA_SOURCE_PREFIX:
                    val = '{{{0}.{1}.{2}#{3}.{4}}}'.format(prefix, $scope.msg(source), object.type, object.id, key);
                    break;
                default:
                    val = key;
                }

                return val;
            });

            if (isChrome) {
                result.find("div").replaceWith(function () {
                    return "\n{0}".format(this.innerHTML);
                });
            }

            if (isIE) {
                result.find("p").replaceWith(function () {
                    return "{0}<br>".format(this.innerHTML);
                });

                result.find("br").last().remove();
            }

            if (isChrome || isIE) {
                if (result[0].childNodes[result[0].childNodes.length - 1] === '<br>') {
                    result[0].childNodes[result[0].childNodes.length - 1].remove();
                }

                result.find("br").replaceWith("\n");
            }

            return result.text();
        };

        $scope.createDraggableElement = function (value) {
            var regex = new RegExp("\\{\\{.*?\\}\\}", "g"), element;

            element = value.replace(regex, function (data) {
                var indexOf = data.indexOf('.'),
                    prefix = data.slice(2, indexOf),
                    dataArray = data.slice(indexOf + 1, -2).split("?"),
                    key = dataArray[0],
                    manipulations = dataArray.slice(1),
                    span, cuts, param, type, field, dataSource, providerId, object, id;

                switch (prefix) {
                case $scope.util.TRIGGER_PREFIX:
                    param = $scope.util.find({
                        where: $scope.selectedTrigger.eventParameters,
                        by: {
                            what: 'eventKey',
                            equalTo: key
                        }
                    });

                    if (!param) {
                        param = {
                            type: 'UNKNOWN',
                            displayName: key
                        };
                    }

                    span = $scope.util.createDraggableSpan({
                        msg: $scope.msg,
                        param: param,
                        prefix: prefix,
                        manipulations: manipulations
                    });
                    break;
                case $scope.util.DATA_SOURCE_PREFIX:
                    cuts = key.split('.');

                    providerId = cuts[0];
                    type = cuts[1].split('#');
                    id = type.last();

                    cuts.remove(0, 1);
                    type.removeObject(id);

                    field = cuts.join('.');
                    type = type.join('#');

                    dataSource = $scope.util.find({
                        where: $scope.task.taskConfig.steps,
                        by: [{
                            what: '@type',
                            equalTo: 'DataSource'
                        }, {
                            what: 'providerId',
                            equalTo: providerId
                        }, {
                            what: 'type',
                            equalTo: type
                        }, {
                            what: 'objectId',
                            equalTo: +id
                        }]
                    });

                    object = dataSource && $scope.findObject(dataSource.providerId, dataSource.type);

                    param = object && $scope.util.find({
                        where: object.fields,
                        by: {
                            what: 'fieldKey',
                            equalTo: field
                        }
                    });

                    if (!param) {
                        param = {
                            type: 'UNKNOWN',
                            displayName: field
                        };
                    }

                    span = $scope.util.createDraggableSpan({
                        msg: $scope.msg,
                        param: param,
                        prefix: prefix,
                        manipulations: manipulations,
                        providerName: dataSource.providerName,
                        object: {
                            id: id,
                            type: type,
                            field: field,
                            displayName: object.displayName
                        }
                    });
                    break;
                }

                return span || '';
            });

            return element.replace(/\n/g, "<br>");
        };

        $scope.save = function (enabled) {
            var success = function () {
                    var msg = enabled ? 'task.success.savedAndEnabled' : 'task.success.saved', loc, indexOf;

                    motechAlert(msg, 'header.saved', function () {
                        unblockUI();
                        loc = window.location.toString();
                        indexOf = loc.indexOf('#');

                        window.location = "{0}#/dashboard".format(loc.substring(0, indexOf));
                    });
                },
                error = function (response) {
                    var data = (response && response.data) || response;

                    angular.forEach($scope.task.actions, function (action) {
                        delete action.values;
                    });

                    angular.forEach($scope.task.taskConfig.steps, function (step) {
                        if (step['@type'] === 'DataSource') {
                            step.lookup.value = $scope.util.convertToView($scope, 'UNICODE', step.lookup.value);
                        }
                    });

                    delete $scope.task.enabled;

                    unblockUI();
                    jAlert($scope.util.createErrorMessage($scope, data), 'header.error');
                };

            $scope.task.enabled = enabled;

            angular.forEach($scope.selectedAction, function (action, idx) {
                if ($scope.task.actions[idx].values === undefined) {
                    $scope.task.actions[idx].values = {};
                }

                angular.forEach(action.actionParameters, function (param) {
                    $scope.task.actions[idx].values[param.key] = $scope.util.convertToServer($scope, param.value);
                });
            });

            angular.forEach($scope.task.taskConfig.steps, function (step) {
                if (step['@type'] === 'DataSource') {
                    if (step.lookup === undefined) {
                        step.lookup = {};
                    }

                    step.lookup.value = $scope.util.convertToServer($scope, step.lookup.value || '');
                }
            });

            blockUI();

            if (!$routeParams.taskId) {
                $http.post('../tasks/api/task/save', $scope.task).success(success).error(error);
            } else {
                $scope.task.$save(success, error);
            }
        };

        $scope.actionCssClass = function (prop) {
            var value, expression = false;

            if ($scope.selectedTrigger !== undefined) {
                value = prop.value === undefined ? '' : prop.value;

                if ($scope.util.canHandleModernDragAndDrop($scope)) {
                    value = $scope.refactorDivEditable(value);
                }

                expression = value.length === 0 || value === "\n";
            }

            return expression;
        };

        $scope.getBooleanValue = function (value) {
            return (value === 'true' || value === 'false') ? null : value;
        };

        $scope.setBooleanValue = function (action, index, value) {
            $scope.selectedAction[action].actionParameters[index].value = $scope.util.createBooleanSpan($scope, value);
        };

        $scope.checkedBoolean = function (action, index, val) {
            var prop = $scope.selectedAction[action].actionParameters[index],
                value = $scope.refactorDivEditable(prop.value === undefined ? '' : prop.value);

            return value === val;
        };

        $scope.getTaskValidationError = function (error) {
            var array = [], i;

            for (i = 0; i < error.args.length; i += 1) {
                array.push($scope.msg(error.args[i]));
            }

            return $scope.msg(error.message, array);
        };

        $scope.showHelp = function () {
            $('#helpModalDate').modal();
        };
    });

    widgetModule.controller('LogCtrl', function ($scope, Tasks, Activities, $routeParams, $filter) {
        var data, task, searchMatch = function (activity, filterHistory) {
            var result;

            if (filterHistory === $scope.histories[0]) {
                result = true;
            } else {
                result = activity.activityType === filterHistory;
            }

            return result;
        };

        $scope.resetItemsPagination();
        $scope.filteredItems = [];
        $scope.limitPages = [10, 20, 50, 100];
        $scope.itemsPerPage = $scope.limitPages[0];
        $scope.histories = ['ALL', 'WARNING', 'SUCCESS', 'ERROR'];
        $scope.filterHistory = $scope.histories[0];

        if ($routeParams.taskId !== undefined) {
            data = { taskId: $routeParams.taskId };

            task = Tasks.get(data, function () {
                $scope.activities = Activities.query(data, $scope.search);

                setInterval(function () {
                    $scope.activities = Activities.query(data);
                }, 30 * 1000);

                if (task.trigger) {
                    $scope.trigger = {
                        channelName: task.trigger.channelName,
                        moduleName: task.trigger.moduleName,
                        moduleVersion: task.trigger.moduleVersion
                    };
                }

                $scope.actions = [];

                angular.forEach(task.actions, function (action) {
                    $scope.actions.push({
                        channelName: action.channelName,
                        moduleName: action.moduleName,
                        moduleVersion: action.moduleVersion
                    });
                });

                $scope.description = task.description;
                $scope.enabled = task.enabled;
                $scope.name = task.name;
            });
        }

        $scope.changeItemsPerPage = function () {
            $scope.setCurrentPage(0);
            $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
        };

        $scope.changeFilterHistory = function () {
            $scope.search();
        };

        $scope.search = function () {
            $scope.filteredItems = $filter('filter')($scope.activities, function (activity) {
                return activity && searchMatch(activity, $scope.filterHistory);
            });

            $scope.setCurrentPage(0);
            $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
        };

        $scope.clearHistory = function () {
            motechConfirm('history.confirm.clearHistory', 'history.confirm.clear',function (r) {
                if (!r) {
                    return;
                }
                Activities.remove({taskId: $routeParams.taskId});
                $scope.activities = [];
                $scope.search();
            });
        };
    });


    widgetModule.controller('SettingsCtrl', function ($scope, Settings) {
        $scope.settings = Settings.get();

        $scope.submit = function() {
            $scope.settings.$save(function() {
                motechAlert('settings.success.saved', 'main.saved');
            }, function() {
                motechAlert('settings.error.saved', 'main.error');
            });
        };

        $scope.cssClass = function(prop) {
            var msg = 'control-group';

            if (!$scope.isNumeric(prop)) {
                msg = msg.concat(' error');
            }

            return msg;
        };

        $scope.isNumeric = function(prop) {
            return $scope.settings.hasOwnProperty(prop) && /^[0-9]+$/.test($scope.settings[prop]);
        };

    });

}());
