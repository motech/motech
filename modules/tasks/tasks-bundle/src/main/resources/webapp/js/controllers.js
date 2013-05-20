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
                                action: tasks[j].action,
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
        $scope.selectedDataSources = [];

        $q.all([$scope.util.doQuery($q, Channels), $scope.util.doQuery($q, DataSources)]).then(function(data) {
            blockUI();

            $scope.channels = data[0];
            $scope.dataSources = data[1];

            if ($routeParams.taskId === undefined) {
                $scope.task = {};
            } else {
                $scope.task = Tasks.get({ taskId: $routeParams.taskId }, function () {
                    var triggerChannel, trigger, action, actionBy = [],
                        dataSource, dataSourceId, object, obj, i;

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

                    if ($scope.task.filters.length > 0) {
                        $http.get($scope.util.FILTER_SET_PATH).success(function (html) {
                            angular.element($scope.util.BUILD_AREA_ID).append($compile(html)($scope));
                            angular.element($scope.util.BUILD_AREA_ID + ' #filter-set #collapse-filter').collapse('hide');
                        });
                    }

                    for (dataSourceId in $scope.task.additionalData) {
                        if ($scope.task.additionalData.hasOwnProperty(dataSourceId)) {
                            dataSource = $scope.util.find({
                                where: $scope.dataSources,
                                by: {
                                    what: '_id',
                                    equalTo: dataSourceId
                                }
                            });

                            for (i = 0; i < $scope.task.additionalData[dataSourceId].length; i += 1) {
                                object = $scope.task.additionalData[dataSourceId][i];

                                obj = $scope.util.find({
                                    where: dataSource.objects,
                                    by: {
                                        what: 'type',
                                        equalTo: object.type
                                    }
                                });

                                $scope.selectedDataSources.push({
                                    id: object.id,
                                    dataSourceId: dataSource._id,
                                    dataSourceName: dataSource.name,
                                    displayName: obj.displayName,
                                    type: object.type,
                                    failIfDataNotFound: object.failIfDataNotFound,
                                    lookup: {
                                        field: object.lookupField,
                                        value: object.lookupValue
                                    }
                                });
                            }
                        }
                    }

                    $scope.selectedDataSources.sort(function (one, two) {
                        return one.id - two.id;
                    });

                    $http.get($scope.util.DATA_SOURCE_PATH).success(function (html) {
                        angular.forEach($scope.selectedDataSources, function (data) {
                            var childScope = $scope.$new();

                            data.lookup.value = $scope.util.convertToView($scope, 'UNICODE', data.lookup.value);
                            childScope.data = data;

                            angular.element($scope.util.BUILD_AREA_ID).append($compile(html)(childScope));
                        });

                        angular.element($scope.util.BUILD_AREA_ID + " .accordion-body").collapse('hide');
                    });

                    if ($scope.task.action) {
                        $scope.selectedActionChannel = $scope.util.find({
                            where: $scope.channels,
                            by: {
                                what: 'moduleName',
                                equalTo: $scope.task.action.moduleName
                            }
                        });

                        if ($scope.selectedActionChannel) {
                            if ($scope.task.action.subject) {
                                actionBy.push({ what: 'subject', equalTo: $scope.task.action.subject });
                            }

                            if ($scope.task.action.serviceInterface && $scope.task.action.serviceMethod) {
                                actionBy.push({ what: 'serviceInterface', equalTo: $scope.task.action.serviceInterface });
                                actionBy.push({ what: 'serviceMethod', equalTo: $scope.task.action.serviceMethod });
                            }

                            action = $scope.util.find({
                                where: $scope.selectedActionChannel.actionTaskEvents,
                                by: actionBy
                            });

                            if (action) {
                                $timeout(function () {
                                    $scope.util.action.select($scope, action);
                                    angular.element('#collapse-action').collapse('hide');

                                    angular.forEach($scope.selectedAction.actionParameters, function (param) {
                                        param.value = $scope.task.actionInputFields[param.key];
                                        param.value = $scope.util.convertToView($scope, param.type, param.value);
                                    });
                                });
                            }
                        }
                    }
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
            $scope.task.action = {};
        };

        $scope.removeAction = function () {
            motechConfirm('task.confirm.action', "header.confirm", function (val) {
                if (val) {
                    delete $scope.task.action;

                    if (!$scope.$$phase) {
                        $scope.$apply($scope.task);
                    }
                }
            });
        };

        $scope.selectActionChannel = function (channel) {
            if ($scope.selectedActionChannel && $scope.selectedAction) {
                motechConfirm('task.confirm.action', "header.confirm", function (val) {
                    if (val) {
                        $scope.task.action = {};
                        $scope.selectedActionChannel = channel;
                        delete $scope.selectedAction;

                        if (!$scope.$$phase) {
                            $scope.$apply($scope.task);
                        }
                    }
                });
            } else {
                $scope.selectedActionChannel = channel;
            }
        };

        $scope.getActions = function () {
            return ($scope.selectedActionChannel && $scope.selectedActionChannel.actionTaskEvents) || [];
        };

        $scope.selectAction = function (action) {
            if ($scope.selectedAction) {
                motechConfirm('task.confirm.action', "header.confirm", function (val) {
                    if (val) {
                        $scope.util.action.select($scope, action);
                    }
                });
            } else {
                $scope.util.action.select($scope, action);
            }
        };

        $scope.addFilterSet = function () {
            $scope.task.filters = [];

            $http.get($scope.util.FILTER_SET_PATH).success(function (html) {
                angular.element($scope.util.BUILD_AREA_ID).append($compile(html)($scope));
            });
        };

        $scope.removeFilterSet = function () {
            motechConfirm('task.confirm.filterSet', "header.confirm", function (val) {
                if (val) {
                    delete $scope.task.filters;
                    angular.element($scope.util.BUILD_AREA_ID).children($scope.util.FILTER_SET_ID).remove();

                    if (!$scope.$$phase) {
                        $scope.$apply($scope.task);
                    }
                }
            });
        };

        $scope.addFilter = function () {
            $scope.task.filters.push({});
        };

        $scope.removeFilter = function (filter) {
            $scope.task.filters.removeObject(filter);
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
            var childScope = $scope.$new(),
                length = $scope.selectedDataSources.length,
                lastData = $scope.selectedDataSources[length - 1];

            childScope.data = { id: (lastData && lastData.id + 1) || 0 };

            $scope.selectedDataSources.push(childScope.data);

            $http.get($scope.util.DATA_SOURCE_PATH).success(function (html) {
                angular.element($scope.util.BUILD_AREA_ID).append($compile(html)(childScope));
            });
        };

        $scope.removeData = function (data) {
            motechConfirm('task.confirm.dataSource', "header.confirm", function (val) {
                if (val) {
                    $scope.selectedDataSources.removeObject(data);
                    angular.element($scope.util.BUILD_AREA_ID).children($scope.util.DATA_SOURCE_PREFIX_ID + data.id).remove();

                    if (!$scope.$$phase) {
                        $scope.$apply($scope.selectedDataSources);
                    }
                }
            });
        };

        $scope.findDataSource = function (dataSourceId) {
            return $scope.util.find({
                where: $scope.dataSources,
                by: {
                    what: '_id',
                    equalTo: dataSourceId
                }
            });
        };

        $scope.findObject = function (dataSourceId, type, id) {
            var dataSource = $scope.findDataSource(dataSourceId),
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

        $scope.selectDataSource = function (data, selected) {
            if (data.dataSourceId) {
                motechConfirm('task.confirm.changeDataSource', 'header.confirm', function (val) {
                    if (val) {
                        $scope.util.dataSource.select($scope, data, selected);
                    }
                });
            } else {
                $scope.util.dataSource.select($scope, data, selected);
            }
        };

        $scope.selectObject = function (data, selected) {
            if (data.type) {
                motechConfirm('task.confirm.changeObject', 'header.confirm', function (val) {
                    if (val) {
                        $scope.util.dataSource.selectObject($scope, data, selected);
                    }
                });
            } else {
                $scope.util.dataSource.selectObject($scope, data, selected);
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
                    span, cuts, param, type, field, dataSource, dataSourceId, object, id;

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

                    dataSourceId = cuts[0];
                    type = cuts[1].split('#');
                    id = type.last();

                    cuts.remove(0, 1);
                    type.removeObject(id);

                    field = cuts.join('.');
                    type = type.join('#');

                    dataSource = $scope.util.find({
                        where: $scope.selectedDataSources,
                        by: [{
                            what: 'dataSourceId',
                            equalTo: dataSourceId
                        }, {
                            what: 'type',
                            equalTo: type
                        }, {
                            what: 'id',
                            equalTo: +id
                        }]
                    });

                    object = dataSource && $scope.findObject(dataSource.dataSourceId, dataSource.type);

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
                        dataSourceName: dataSource.dataSourceName,
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
            var action = $scope.selectedAction;

            $scope.task.actionInputFields = {};
            $scope.task.enabled = enabled;
            $scope.task.additionalData = {};

            angular.forEach($scope.selectedDataSources, function (data) {
                var exists = false, lookupValue = (data.lookup && data.lookup.value) || '',
                    additionalData, object, i;

                lookupValue = $scope.util.convertToServer($scope, lookupValue);

                if ($scope.task.additionalData[data.dataSourceId] === undefined) {
                    $scope.task.additionalData[data.dataSourceId] = [];
                }

                additionalData = $scope.task.additionalData[data.dataSourceId];

                for (i = 0; i < additionalData.length; i += 1) {
                    object = additionalData[i];

                    if (object.id === data.id) {
                        object.type = data.type;
                        object.lookupField = data.lookup.field;
                        object.lookupValue = lookupValue;
                        object.failIfDataNotFound = data.failIfDataNotFound;

                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    additionalData.push({
                        id: data.id,
                        type: data.type,
                        lookupField: (data.lookup && data.lookup.field),
                        lookupValue: lookupValue,
                        failIfDataNotFound: data.failIfDataNotFound
                    });
                }
            });

            if (action) {
                angular.forEach(action.actionParameters, function (param) {
                    $scope.task.actionInputFields[param.key] = $scope.util.convertToServer($scope, param.value);
                });
            }

            blockUI();

            if (!$routeParams.taskId) {
                $http.post('../tasks/api/task/save', $scope.task)
                    .success(function () {
                        var msg = enabled ? 'task.success.savedAndEnabled' : 'task.success.saved', loc, indexOf;

                        motechAlert(msg, 'header.saved', function () {
                            unblockUI();
                            loc = window.location.toString();
                            indexOf = loc.indexOf('#');

                            window.location = "{0}#/dashboard".format(loc.substring(0, indexOf));
                        });
                    })
                    .error(function (response) {
                        delete $scope.task.actionInputFields;
                        delete $scope.task.enabled;
                        delete $scope.task.additionalData;

                        unblockUI();
                        jAlert($scope.util.createErrorMessage($scope, response), 'header.error');
                    });
            } else {
                $scope.task.$save(function() {
                    var loc, indexOf;

                    motechAlert('task.success.saved', 'header.saved', function () {
                        unblockUI();
                        loc = window.location.toString();
                        indexOf = loc.indexOf('#');

                        window.location = "{0}#/dashboard".format(loc.substring(0, indexOf));
                    });
                }, function (response) {
                    delete $scope.task.actionInputFields;
                    delete $scope.task.enabled;
                    delete $scope.task.additionalData;

                    unblockUI();
                    jAlert($scope.util.createErrorMessage($scope, response.data), 'header.error');
                });
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

        $scope.setBooleanValue = function (index, value) {
            $scope.selectedAction.actionParameters[index].value = $scope.util.createBooleanSpan($scope, value);
        };

        $scope.checkedBoolean = function (index, val) {
            var prop = $scope.selectedAction.actionParameters[index],
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

                $scope.trigger = {
                    display: task.trigger.channelName,
                    module: task.trigger.moduleName,
                    version: task.trigger.moduleVersion
                };

                $scope.action = {
                    display: task.action.channelName,
                    module: task.action.moduleName,
                    version: task.action.moduleVersion
                };

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
}());
