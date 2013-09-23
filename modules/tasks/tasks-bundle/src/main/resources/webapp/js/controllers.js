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

        $scope.allTasks = [];
        $scope.activities = [];
        $scope.hideActive = false;
        $scope.hidePaused = false;
        $scope.filteredItems = [];
        $scope.itemsPerPage = 10;
        $scope.currentFilter = 'allItems';
        $scope.formatInput = [];

        $scope.getTasks = function () {
            $scope.allTasks = [];

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
        };

        $scope.enableTask = function (item, enabled) {
            item.task.enabled = enabled;

            item.task.$save(dummyHandler, function (response) {
                item.task.enabled = !enabled;
                handleResponse('task.error.actionNotChangeTitle', 'task.error.actionNotChange', response);
            });
        };

        $scope.deleteTask = function (item) {
            jConfirm(jQuery.i18n.prop('task.confirm.remove'), jQuery.i18n.prop("task.header.confirm"), function (val) {
                if (val) {
                    item.task.$remove(function () {
                        $scope.allTasks.removeObject(item);
                        $scope.search();
                    }, alertHandler('task.error.removed', 'task.header.error'));
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

        $scope.importTask = function () {
            blockUI();

            $('#importTaskForm').ajaxSubmit({
                success: function () {
                    $scope.getTasks();
                    $('#importTaskForm').resetForm();
                    $('#importTaskModal').modal('hide');
                    unblockUI();
                },
                error: function (response) {
                    handleResponse('task.header.error', 'task.error.import', response);
                }
            });
        };

        $scope.closeImportTaskModal = function () {
            $('#importTaskForm').resetForm();
            $('#importTaskModal').modal('hide');
        };

        $scope.resetItemsPagination();
        $scope.getTasks();

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
                                angular.forEach(step.lookup, function(lookupField) {
                                    lookupField.value = $scope.util.convertToView($scope, 'UNICODE', lookupField.value);
                                });
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
                motechConfirm('task.confirm.trigger', "task.header.confirm", function (val) {
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

            motechConfirm('task.confirm.trigger', "task.header.confirm", function (val) {
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
            motechConfirm('task.confirm.action', "task.header.confirm", function (val) {
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
                motechConfirm('task.confirm.action', "task.header.confirm", function (val) {
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
                motechConfirm('task.confirm.action', "task.header.confirm", function (val) {
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
            motechConfirm('task.confirm.filterSet', "task.header.confirm", function (val) {
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
            var text, splitted, empty;

            filter.negationOperator = filter.operator = filter.expression = empty;

            if (filter.displayName) {
                text = filter.displayName;
                splitted = text.split('.');
                text = "";
            }

            if (!select) {
                if (splitted) {
                    type = splitted[0];
                } else {
                    type = empty;
                }

                if (type === $scope.util.TRIGGER_PREFIX && splitted.length >= 2 && splitted[1] !== '' && splitted[splitted.length - 1] !== '') {
                    if (splitted.length > 2) {
                        splitted.shift();

                        text = splitted[0];
                        splitted.shift();

                        angular.forEach(splitted, function(key) {
                            text = text + '.' + key;
                        });
                    } else {
                        text = splitted[1];
                    }

                    select = {
                        'eventKey' : text
                    };
                } else if (type === $scope.util.DATA_SOURCE_PREFIX && splitted.length === 5 && splitted[4] !== '') {
                    text = splitted[3].split('#');
                    select = $scope.util.find({
                        msg: $scope.msg,
                        where: $scope.task.taskConfig.steps,
                        by: [{
                            what: '@type',
                            equalTo: 'DataSource'
                        }, {
                            what: 'providerName',
                            equalTo: splitted[1] + '.' + splitted[2]
                        }, {
                            what: 'type',
                            equalTo: text[0]
                        }]
                    });

                    if (select) {
                        text = $scope.findObject(select.providerId, text[0]);
                        text = $scope.util.find({
                            msg: $scope.msg,
                            where: text.fields,
                            by: [{
                                what: 'fieldKey',
                                equalTo : splitted[4]
                            }]
                        });

                        if (!text) {
                            select = empty;
                        }
                    }

                    field = {
                        'fieldKey' : splitted[4],
                        'fieldType' : type
                    };
                }

                if (!select) {
                    type = empty;
                }
            }

            switch(type) {
            case $scope.util.TRIGGER_PREFIX:
                filter.key = "{0}.{1}".format($scope.util.TRIGGER_PREFIX, select.eventKey);
                filter.displayName = filter.key;
                filter.type = select.type;
                break;
            case $scope.util.DATA_SOURCE_PREFIX:
                filter.key = "{0}.{1}.{2}#{3}.{4}".format($scope.util.DATA_SOURCE_PREFIX, select.providerId, select.type, select.objectId, field.fieldKey);
                filter.displayName = "{0}.{1}.{2}#{3}.{4}".format($scope.util.DATA_SOURCE_PREFIX, select.providerName, select.type, select.objectId, field.fieldKey);
                filter.type = field.type;
                break;
            default:
                filter.key = empty;
            }


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

            if (!$scope.$$phase) {
                $scope.$apply($scope.task);
            }
        };

        $scope.removeData = function (dataSource) {
            motechConfirm('task.confirm.dataSource', "task.header.confirm", function (val) {
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
                motechConfirm('task.confirm.changeDataSource', 'task.header.confirm', function (val) {
                    if (val) {
                        dataSource.name = '';
                        $scope.util.dataSource.select($scope, dataSource, selected);
                    }
                });
            } else {
                $scope.util.dataSource.select($scope, dataSource, selected);
            }
        };

        $scope.selectObject = function (object, selected) {
            if (object.type) {
                motechConfirm('task.confirm.changeObject', 'task.header.confirm', function (val) {
                    if (val) {
                        object.name = '';
                        $scope.util.dataSource.selectObject($scope, object, selected);
                    }
                });
            } else {
                $scope.util.dataSource.selectObject($scope, object, selected);
            }
        };

        $scope.selectLookup = function(data, lookup) {
            data.lookup = [];
            data.name=lookup.displayName;
            angular.forEach(lookup.fields, function(lookupField) {
                data.lookup.push({field:lookupField, value:''});
            });

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
                    key = span.text().replace(/[\[\]']+/g,''); // for non-typed events (span.text() returns then something like "[myField]")
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
                    if ($scope.util.isText(type) || $scope.util.isDate(type)) {
                        array = manipulations.split(" ");

                        for (i = 0; i < array.length; i += 1) {
                            key = key.concat("?" + array[i]);
                        }
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

        $scope.hasUnknownTrigger = function (value) {
            var unknown = false,
                regex, found, data, indexOf, prefix, dataArray, key, param;

            // check bubbles
            if ($scope.util.canHandleModernDragAndDrop($scope)) {
                prefix = $scope.util.TRIGGER_PREFIX;
                regex = new RegExp('<span.*data-prefix="'+ prefix +'".*data-type="UNKNOWN".*>\\[.*\\]</span>', "g");
                unknown = regex.exec(value) !== null;
            }

            // check regular string in the input
            if (!unknown) {
                regex = new RegExp("\\{\\{.*?\\}\\}", "g");
                while ((found = regex.exec(value)) !== null) {
                    data = found[0];
                    indexOf = data.indexOf('.');
                    prefix = data.slice(2, indexOf);
                    dataArray = data.slice(indexOf + 1, -2).split("?");
                    key = dataArray[0];

                    if (prefix === $scope.util.TRIGGER_PREFIX) {
                        param = $scope.util.find({
                            where: $scope.selectedTrigger.eventParameters,
                            by: {
                                what: 'eventKey',
                                equalTo: key
                            }
                        });

                        if (!param) {
                            unknown = true;
                            break;
                        }
                    }
                }
            }

            return unknown;
        };

        $scope.createDraggableElement = function (value, forFormat) {
            var regex, element, manipulateAttributes, joinSeparator, ds, values, splittedValue, newValue;

            if (value.length !== 0 && forFormat === 'convert') {
                regex = new RegExp('format(.*)', "g");
                manipulateAttributes = value.match(regex);
                if (manipulateAttributes) {
                    manipulateAttributes = manipulateAttributes[0].substr(0, manipulateAttributes[0].indexOf(")") + 1);
                    regex = new RegExp(' {{', "g");
                    joinSeparator = manipulateAttributes.replace(regex, '{');
                    regex = new RegExp('{{', "g");
                    joinSeparator = joinSeparator.replace(regex, '{');
                    regex = new RegExp('}}', "g");
                    joinSeparator = joinSeparator.replace(regex, '}');

                    regex = new RegExp("\\{ad([^)]+)\\}", "g");
                    values = joinSeparator.match(regex);

                    if (values) {
                        regex = new RegExp('{', "g");
                        values = values[0].replace(regex, '');
                        regex = new RegExp('}', "g");
                        values = values.replace(regex, '');
                        values = values.split('ad');

                        angular.forEach(values, function (element) {
                            if (element.length > 0) {
                                newValue = ($scope.createDraggableElement(element, forFormat));
                                splittedValue = element.split('.');

                                ds = $scope.util.find({
                                    msg: $scope.msg,
                                    where: $scope.task.taskConfig.steps,
                                    by: [{
                                        what: '@type',
                                        equalTo: 'DataSource'
                                    }, {
                                        what: 'providerName',
                                        equalTo: splittedValue[1]
                                    }]
                                });

                                if (ds) {
                                    newValue = element.replace(splittedValue[1], ds.providerId);
                                    joinSeparator = joinSeparator.replace(element, newValue);
                                }
                            }
                        });
                    }

                    value = value.replace(manipulateAttributes, joinSeparator);
                }
            }

            if (forFormat === 'true') {
                regex = new RegExp("\\{.*?\\}", "g");
            } else {
                regex = new RegExp("\\{\\{.*?\\}\\}", "g");
            }

            element = value.replace(regex, function (data) {
                var indexOf = data.indexOf('.'),
                    prefix, dataArray, key, manipulations,
                    span, cuts, param, type, field, dataSource, providerId, object, id;

                    if (forFormat === 'true') {
                        prefix = data.slice(1, indexOf);
                        dataArray = data.slice(indexOf + 1, -1).split("?");
                    } else {
                        prefix = data.slice(2, indexOf);
                        dataArray = data.slice(indexOf + 1, -2).split("?");
                    }

                    key = dataArray[0];
                    manipulations = dataArray.slice(1);

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
                        manipulations: manipulations,
                        popover: forFormat
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

                    motechAlert(msg, 'task.header.saved', function () {
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
                            angular.forEach(step.lookup, function(lookupField) {
                                lookupField.value = $scope.util.convertToView($scope, 'UNICODE', lookupField.value);
                            });
                        }
                    });

                    delete $scope.task.enabled;

                    unblockUI();
                    jAlert($scope.util.createErrorMessage($scope, data), $scope.msg('task.header.error'));
                };

            $scope.task.enabled = enabled;

            angular.forEach($scope.selectedAction, function (action, idx) {
                if ($scope.task.actions[idx].values === undefined) {
                    $scope.task.actions[idx].values = {};
                }

                angular.forEach(action.actionParameters, function (param) {
                    if ($scope.util.isChrome($scope) || $scope.util.isIE($scope)) {
                        $scope.task.actions[idx].values[param.key] = $scope.addDoubleBrackets($scope.util.convertToServer($scope, param.value));
                    } else {
                        $scope.task.actions[idx].values[param.key] = $scope.util.convertToServer($scope, param.value);
                    }

                    if (!param.required && isBlank($scope.task.actions[idx].values[param.key])) {
                        delete $scope.task.actions[idx].values[param.key];
                    }
                });
            });

            angular.forEach($scope.task.taskConfig.steps, function (step) {
                if (step['@type'] === 'DataSource') {
                    if (step.lookup === undefined) {
                        step.lookup = [];
                    }
                    angular.forEach(step.lookup, function(lookupField) {
                        lookupField.value = $scope.util.convertToServer($scope, lookupField.value || '');
                    });

                }
            });

            blockUI();

            if (!$routeParams.taskId) {
                $http.post('../tasks/api/task/save', $scope.task).success(success).error(error);
            } else {
                $scope.task.$save(success, error);
            }
        };

        $scope.actionCssClassWarning = function(prop) {
            var value, expression = false;

            if ($scope.selectedTrigger !== undefined) {
                value = prop.value === undefined ? '' : prop.value;
                expression = $scope.hasUnknownTrigger(value);
            }

            return expression;
        };

        $scope.actionCssClassError = function (prop) {
            var value, expression = false, required = prop && prop.required;

            if (!required || $scope.actionCssClassWarning(prop)) {
                return expression;
            } else if ($scope.selectedTrigger !== undefined) {
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

        $scope.changeFormatInput = function (newData) {
            $scope.formatInput = [];
            $scope.$apply();
            $scope.formatInput = newData;
            $scope.$apply();
        };

        $scope.showFormatManipulation = function () {
            $('#formatManipulation').modal();
            $scope.changeFormatInput($scope.getValues('true'));
        };

        $scope.getValues = function(forFormat) {
            var manipulateElement = $("[ismanipulate=true]"), joinSeparator = "", manipulation, manipulateAttributes, manipulationAttributesIndex, convertedValues = [], reg;
            manipulation = "format";
            manipulateAttributes = manipulateElement.attr("manipulate") || "";

            if ((manipulateAttributes.indexOf(manipulation) !== -1) && (manipulation === "format")) {
                manipulateAttributes = manipulateAttributes.match('format(.*)');
                reg = manipulateAttributes[1];
                if ((reg.indexOf("(") + 1) !== reg.indexOf(")")) {
                    joinSeparator = reg.substr(reg.indexOf("(") + 1, reg.indexOf(")") - 1);
                    reg = joinSeparator.split(",");
                } else {
                    reg = convertedValues;
                }
            }

            angular.forEach(reg, function (value) {
                convertedValues.push($scope.createDraggableElement(value, forFormat));
            });

            return convertedValues;
        };

        $scope.addFormatInput = function () {
            $scope.formatInput.push("");

            $scope.$apply();
        };

        $scope.deleteFormatInput = function (index) {
            var tempArray = [], counter = 0;

            $scope.tempSaveInput();

            angular.forEach($scope.formatInput, function (value) {
                if  (counter !== index) {
                    tempArray.push(value);
                }

                counter = counter + 1;
            });

            $scope.changeFormatInput(tempArray);
        };

        $scope.tempSaveInput = function () {
            var inputFields = $("[data-type=format]"), tempArray = [];

            angular.forEach(inputFields, function (value) {
                tempArray.push(value.innerHTML);
            });

            $scope.formatInput = tempArray;
        };

        $scope.saveInput = function () {
            var inputFields = $("[data-type=format]"), tempArray = [];

            angular.forEach(inputFields, function (value) {
                tempArray.push($scope.removeDoubleBrackets($scope.util.convertToServer($scope, value.innerHTML)));
            });

            $scope.formatInput = tempArray;
            $scope.changeFormatManipulation();
        };

        $scope.changeFormatManipulation = function () {
            var manipulation = "format(",
                manipulateElement = $("[ismanipulate=true]"),
                elementManipulation = manipulateElement.attr("manipulate"),
                regex = new RegExp("format\\(.*?\\)", "g");

            jQuery.each($scope.formatInput, function(value) {

                manipulation = manipulation + this;
                if (value !== $scope.formatInput.length - 1) {
                    manipulation = manipulation + ",";
                }
            });

            manipulation = manipulation + ")";
            manipulation = manipulation.replace(/\s+/g,"");

            elementManipulation = elementManipulation.replace(regex, manipulation);
            manipulateElement.attr("manipulate", elementManipulation);
            manipulateElement[0].parentElement.focus();
        };

        $scope.removeDoubleBrackets = function (value) {
            var tempValue = "", reg;

            if (value.length !== 0) {
                reg = new RegExp('{{', "g");
                tempValue = value.replace(reg, '{');
                reg = new RegExp('}}', "g");
                tempValue = tempValue.replace(reg, '}');
                value = value.replace(value, tempValue);
            }

            return value;
        };

        $scope.addDoubleBrackets = function (value) {
            var manipulateAttributes, reg = "", joinSeparator, splittedValue, newValue, values, ds;

            if (value.length !== 0) {
                reg = new RegExp("format\\(.*?\\)", "g");
                manipulateAttributes = value.match(reg);
                if (manipulateAttributes) {
                    manipulateAttributes = manipulateAttributes[0].substr(0, manipulateAttributes[0].indexOf(")") + 1);
                    reg = new RegExp('{', "g");
                    joinSeparator = manipulateAttributes.replace(reg, '{{');
                    reg = new RegExp('{{3,}', "g");
                    joinSeparator = joinSeparator.replace(reg, '{{');
                    reg = new RegExp('}', "g");
                    joinSeparator = joinSeparator.replace(reg, '}}');
                    reg = new RegExp('}{3,}', "g");
                    joinSeparator = joinSeparator.replace(reg, '}}');
                    reg = new RegExp("\\{ad([^)]+)\\}", "g");
                    values = joinSeparator.match(reg);

                    if (values) {
                        reg = new RegExp('{', "g");
                        values = values[0].replace(reg, '');
                        reg = new RegExp('}', "g");
                        values = values.replace(reg, '');
                        values = values.split('ad');

                        angular.forEach(values, function (element) {
                            if (element.length > 0) {
                                splittedValue = element.split('.');

                                ds = $scope.util.find({
                                    msg: $scope.msg,
                                    where: $scope.task.taskConfig.steps,
                                    by: [{
                                        what: '@type',
                                        equalTo: 'DataSource'
                                    }, {
                                        what: 'providerId',
                                        equalTo: splittedValue[1]
                                    }]
                                });

                                if (ds) {
                                    joinSeparator = joinSeparator.replace(splittedValue[1], $scope.msg(ds.providerName));
                                }
                            }
                        });
                    }

                    value = value.replace(manipulateAttributes, joinSeparator);
                }
            }

            return value;
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
            motechConfirm('task.history.confirm.clearHistory', 'task.history.confirm.clear',function (r) {
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
                motechAlert('task.settings.success.saved', 'server.saved');
            }, function() {
                motechAlert('task.settings.error.saved', 'server.error');
            });
        };

        $scope.cssClass = function(prop) {
            var msg = 'control-group';

            if (!$scope.isNumeric(prop)) {
                msg = msg.concat('server.error');
            }

            return msg;
        };

        $scope.isNumeric = function(prop) {
            return $scope.settings.hasOwnProperty(prop) && /^[0-9]+$/.test($scope.settings[prop]);
        };

    });

}());
