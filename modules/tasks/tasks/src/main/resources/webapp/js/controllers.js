(function () {

    'use strict';

    /* Controllers */

    var controllers = angular.module('tasks.controllers', []);

    controllers.controller('TasksDashboardCtrl', function ($scope, $filter, Tasks, Activities, $rootScope, $http, ManageTaskUtils, ModalFactory, LoadingModal) {
        var tasks, activities = [],
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
        $scope.util = ManageTaskUtils;

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        }, {
            show: true,
            button: '#tasks-filters'
        });

        $("#tasks-filters").bind('click', function () {
            $("#recentTaskActivity-tab").removeClass('active');
            $("#recentTaskActivity").removeClass('in active');
            $("#filters-tab").addClass('active');
            $("#filters").addClass(' in active');
        });

        $scope.getNumberOfActivities = function(id, type) {
            var numberOfActivities;
            $.ajax({
                url: '../tasks/api/activity/' + id + '/' + type,
                success:  function(data) {
                    numberOfActivities = data;
                },
                async: false
            });

            return numberOfActivities;
        };

        $scope.getTasks = function () {
            $scope.allTasks = [];

            tasks = Tasks.query(function () {
                activities = Activities.query(function () {
                    var item, i, j;

                    for (i = 0; i < tasks.length; i += 1) {
                        item = {
                            task: tasks[i],
                            success: $scope.getNumberOfActivities(tasks[i].id, 'SUCCESS'),
                            error: $scope.getNumberOfActivities(tasks[i].id, 'ERROR')
                        };

                        $scope.allTasks.push(item);
                    }
                    $scope.allTasks=$scope.sortTasksAlphabetically($scope.allTasks);
                    $rootScope.search();
                    $('#inner-center').trigger("change");
                });
            });
        };

        $scope.sortTasksAlphabetically = function(tasks){
            var taskNames = [], sortedTasks = [], i, j, k;
            for (i=0; i<tasks.length; i += 1){
                taskNames.push(tasks[i].task.name);
            }
            taskNames.sort();
            for (j=0; j<taskNames.length; j += 1){
                for (k=0; k<tasks.length; k += 1){
                    if(taskNames[j] === tasks[k].task.name){
                        sortedTasks.push(tasks[k]);
                    }
                }
            }
            return sortedTasks;
        };

        $scope.enableTask = function (item, enabled) {
            var task = {};

            LoadingModal.open();

            task.enabled = item.task.enabled = enabled;
            task.id = item.task.id;
            task.name = item.task.name;

            $http.post('../tasks/api/task/enable-or-disable/' + task.id, task)
                .success(function () {
                    LoadingModal.close();
                })
                .error(function (response) {
                    task.enabled = item.task.enabled = !enabled;

                    LoadingModal.close();
                    ModalFactory.showErrorAlert(null, 'task.error.actionNotChangeTitle', $scope.util.createErrorMessage($scope, response, false));
                });
        };

        $scope.deleteTask = function (item) {
            ModalFactory.showConfirm({
                title: $scope.msg('task.header.confirm'),
                message: $scope.msg('task.confirm.remove'),
                type: 'type-danger',
                callback: function(result) {
                    if (result) {
                        LoadingModal.open();
                        item.task.$remove(function () {
                            $scope.allTasks.removeObject(item);
                            $rootScope.search();
                            $('#inner-center').trigger("change");
                            LoadingModal.close();
                        }, function () {
                            LoadingModal.close();
                            ModalFactory.showErrorAlert('task.error.removed');
                        });
                    }
                }
            });
        };

        $rootScope.search = function () {
            $scope.filteredItems = $filter('filter')($scope.allTasks, function (item) {
                return item && searchMatch(item, $scope.currentFilter, $rootScope.query);
            });

            $scope.setCurrentPage(0);
            $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
        };

        $rootScope.setHideActive = function () {
            if ($scope.hideActive === true) {
                $scope.hideActive = false;
                $scope.setFilter($scope.hidePaused ? 'pausedTaskFilter' : 'allItems');

                $('.setHideActive').find('i').removeClass("fa-square-o").addClass('fa-check-square-o');
            } else {
                $scope.hideActive = true;
                $scope.setFilter($scope.hidePaused ? 'noItems' : 'activeTaskFilter');

                $('.setHideActive').find('i').removeClass("fa-check-square-o").addClass('fa-square-o');
            }
        };

        $rootScope.setHidePaused = function () {
            if ($scope.hidePaused === true) {
                $scope.hidePaused = false;
                $scope.setFilter($scope.hideActive ? 'activeTaskFilter' : 'allItems');

                $('.setHidePaused').find('i').removeClass("fa-square-o").addClass('fa-check-square-o');
            } else {
                $scope.hidePaused = true;
                $scope.setFilter($scope.hideActive ? 'noItems' : 'pausedTaskFilter');

                $('.setHidePaused').find('i').removeClass("fa-check-square-o").addClass('fa-square-o');
            }
        };

        $scope.setFilter = function (method) {
            $scope.currentFilter = method;
            $rootScope.search();
            $('#inner-center').trigger("change");
        };

        $scope.resetItemsPagination();
        $scope.getTasks();
    });

    controllers.controller('TasksRecentActivityCtrl', function ($scope, Tasks, Activities) {

            var RECENT_TASK_COUNT = 7, tasks, activities = [];

            $scope.activities = [];
            $scope.formatInput = [];

            $scope.getNumberOfActivities = function(id, type) {
                var numberOfActivities;
                $.ajax({
                    url: '../tasks/api/activity/' + id + '/' + type,
                    success:  function(data) {
                        numberOfActivities = data;
                    },
                    async: false
                });

                return numberOfActivities;
            };

            $scope.getTasks = function () {

                tasks = Tasks.query(function () {
                    activities = Activities.query(function () {
                        var item, i, j;

                        for (i = 0; i < tasks.length; i += 1) {
                            item = {
                                task: tasks[i],
                                success: $scope.getNumberOfActivities(tasks[i].id, 'SUCCESS'),
                                error: $scope.getNumberOfActivities(tasks[i].id, 'ERROR')
                            };
                        }

                        for (i = 0; i < RECENT_TASK_COUNT && i < activities.length; i += 1) {
                            for (j = 0; j < tasks.length; j += 1) {
                                if (activities[i].task === tasks[j].id) {
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
                    });
                });
            };
            $scope.getTasks();

        });

    controllers.controller('TasksFilterCtrl', function($scope, $rootScope) {

        $scope.setHidePaused = function() {
            $rootScope.setHidePaused();
        };

        $scope.setHideActive = function() {
            $rootScope.setHideActive();
        };

        $scope.search = function() {
            $rootScope.query = $scope.query;
            $rootScope.search();
            $('#inner-center').trigger("change");
        };
    });

    controllers.controller('TasksManageCtrl', function ($rootScope, $scope, ManageTaskUtils, Channels, DataSources, Tasks, Triggers,
                $q, $timeout, $stateParams, $http, $filter, ModalFactory, LoadingModal, HelpStringManipulation) {

        $scope.showBubbles = false;
        $scope.toggleBubbles = function(toggleBubbles) {
            $scope.showBubbles = toggleBubbles;
        };

        $scope.util = ManageTaskUtils;
        $scope.selectedActionChannel = [];
        $scope.selectedAction = [];
        $scope.task = {
            taskConfig: {
                steps: []
            }
        };
        $scope.task.retryTaskOnFailure = false;
        $scope.task.useTimeWindow = false;
        $scope.taskStepNumber = 0;
        $scope.debugging = false;
        $scope.startTime = "";
        $scope.endTime = "";
        $scope.lastSelectedField = "";
        $scope.days = [{id:0, day: 'Monday', checked: false}, {id:1, day: 'Tuesday', checked: false}, {id:2, day: 'Wednesday', checked: false},
                       {id:3, day: 'Thursday', checked: false}, {id:4, day: 'Friday', checked: false}, {id:5, day:'Saturday', checked: false},
                       {id:6, day: 'Sunday', checked: false}];

        $scope.changeCheckbox = function (debugging) {
            $scope.debugging = debugging;
            $rootScope.$broadcast('debugging', { debug: debugging });
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.filter = $filter('filter');

        LoadingModal.open();

        $q.all([$scope.util.doQuery($q, Channels), $scope.util.doQuery($q, DataSources)]).then(function(data) {
            $scope.channels = data[0];
            $scope.dataSources = data[1];

            if ($stateParams.taskId === undefined) {
                $scope.task = {
                    taskConfig: {
                        steps: []
                    }
                };
            } else {
                LoadingModal.open();
                $scope.task = Tasks.get({ taskId: $stateParams.taskId }, function () {
                    Triggers.getTrigger($scope.task.trigger, function(trigger) {
                        var triggerChannel, dataSource, object, actionStep, actionIndex = 0, actionFilterIndex = 0, filtersToDelete = [], actionSteps= [], actionFilters = [];

                        if ($scope.task.useTimeWindow === true) {
                           $scope.startTime = $scope.task.startTime + " +0000";
                           $scope.endTime = $scope.task.endTime + " +0000";
                           $scope.task.days.forEach(function(day, idx) {
                                if (day === "true") {
                                    $scope.days[idx].checked = true;
                                } else if  (day === "false") {
                                    $scope.days[idx].checked = false;
                                }
                           });
                        }

                        triggerChannel = $scope.util.find({
                            where: $scope.channels,
                            by: {
                                what: 'moduleName',
                                equalTo: $scope.task.trigger.moduleName
                            }
                        });

                        if (trigger) {
                            $timeout(function() {
                                $scope.util.trigger.select($scope, triggerChannel, trigger);
                            });
                        }

                        angular.forEach($scope.task.taskConfig.steps, function (step) {
                            var source, object;

                            angular.element('#collapse-step-' + step.order).livequery(function() {
                                $(this).collapse('hide');
                            });

                            if (step['@type'] === 'DataSourceDto') {
                                source = $scope.findDataSource(step.providerName);
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
                                }
                            }
                            if ($scope.isActionFilterSet(step)) {
                                actionFilters.push(step);
                                filtersToDelete.push(step);
                            }
                            $scope.taskStepNumber = step.order;
                        });

                        angular.forEach(filtersToDelete, function(filter) {
                            $scope.task.taskConfig.steps.removeObject(filter);
                        });

                        for (actionStep = 0; actionStep < ($scope.task.actions.length + actionFilters.length); actionStep += 1) {
                            if ($scope.shouldAddFilterToActions(actionFilterIndex, actionFilters, actionStep)) {
                                actionFilters[actionFilterIndex]['@type'] = 'FilterActionSetDto';
                                actionSteps.push(actionFilters[actionFilterIndex]);
                                actionFilterIndex += 1;
                            } else {
                                actionSteps.push($scope.task.actions[actionIndex]);
                                actionIndex += 1;
                            }
                        }
                        $scope.task.actions = actionSteps;

                        angular.forEach($scope.task.actions, function (info, idx) {
                            if (info['@type'] !== 'FilterActionSetDto') {
                                var action = null, actionBy = [];

                                $scope.selectedActionChannel[idx] = $scope.util.find({
                                    where: $scope.channels,
                                    by: {
                                        what: 'moduleName',
                                        equalTo: info.moduleName
                                    }
                                });

                                if ($scope.selectedActionChannel[idx]) {
                                    if (info.name) {
                                        actionBy.push({ what: 'name', equalTo: info.name });
                                        action = $scope.util.find({
                                            where: $scope.selectedActionChannel[idx].actionTaskEvents,
                                            by: actionBy
                                        });
                                    } else {
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
                                    }

                                    if (action) {
                                        $timeout(function () {
                                            if (info.specifiedName) {
                                                action.specifiedName = info.specifiedName;
                                            }

                                            $scope.util.action.select($scope, idx, action);
                                            angular.element('#collapse-action-' + idx).collapse('hide');
                                            angular.forEach($scope.selectedAction[idx].actionParameters, function (param) {
                                                param.value = info.values[param.key] || '';
                                            });
                                        });
                                    }
                                }
                            }
                        });
                    });
                });
            }

            LoadingModal.close();
        });

        $scope.isTaskValid = function() {
            var useTimeWindowValidation;
            if($scope.task.useTimeWindow) {
                useTimeWindowValidation = $scope.isTimeFormat($scope.startTime)
                && $scope.isTimeFormat($scope.endTime);
            } else {
                useTimeWindowValidation = true;
            }

            return $scope.task.name && useTimeWindowValidation;
        };

        $scope.isNumericalNonNegativeValue = function (value) {
            return !isNaN(value) && value >= 0;
        };

        $scope.isTimeFormat = function (value) {
            return value.length === 11;
        };

        $scope.removeTrigger = function ($event) {
            $event.stopPropagation();

            ModalFactory.showConfirm('task.confirm.trigger', "task.header.confirm", function (val) {
                if (val) {
                    $scope.util.trigger.remove($scope);
                }
            });
        };

        $scope.addAction = function () {
            if (!$scope.task.actions) {
                $scope.task.actions = [];
            }

            $scope.task.actions.push({'postActionParameters': []});
        };

        $scope.removeAction = function (idx) {
            var removeActionSelected = function (idx) {
                $scope.task.actions.remove(idx);
                delete $scope.selectedActionChannel[idx];
                delete $scope.selectedAction[idx];

                if (!$scope.$$phase) {
                    $scope.$apply($scope.task);
                }
            };

            if ($scope.selectedActionChannel[idx] !== undefined && $scope.selectedActionChannel[idx].displayName !== undefined) {
                ModalFactory.showConfirm('task.confirm.action', "task.header.confirm", function (val) {
                    if (val) {
                        removeActionSelected(idx);
                    }
                });
            } else {
                removeActionSelected(idx);
            }
        };

        $scope.selectActionChannel = function (idx, channel,stepShowBubbles) {
             $scope.stepShowBubbles=stepShowBubbles;
            if ($scope.selectedActionChannel[idx] && $scope.selectedAction[idx]) {
                ModalFactory.showConfirm('task.confirm.action', "task.header.confirm", function (val) {
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

        $scope.selectAction = function (idx, action,stepShowBubbles) {
            $scope.stepShowBubbles=stepShowBubbles;
            if ($scope.selectedAction[idx]) {
                ModalFactory.showConfirm('task.confirm.action', "task.header.confirm", function (val) {
                    if (val) {
                        $scope.util.action.select($scope, idx, action);
                    }
                });
            } else {
                $scope.util.action.select($scope, idx, action);
            }
        };

        $scope.addFilterSet = function () {
            $scope.taskStepNumber += 1;

            $scope.task.taskConfig.steps.push({
                '@type': 'FilterSetDto',
                filters: [],
                operator: "AND",
                order: $scope.taskStepNumber
            });
        };

        $scope.addActionFilterSet = function () {
            if (!$scope.task.actions) {
                $scope.task.actions = [];
            }

            $scope.taskStepNumber += 1;
            $scope.task.actions.push({
                '@type': 'FilterActionSetDto',
                filters: [],
                operator: "AND",
                order: $scope.taskStepNumber
            });
        };

        $scope.isActionFilterSet = function (step) {
            var result = false;
            if (step['@type'] === 'FilterSetDto' && step.actionFilterOrder !== null) {
                result = true;
            }
            return result;
        };

        $scope.shouldAddFilterToActions = function (index, actionFilters, step) {
            var result = false;
            if(index < actionFilters.length && actionFilters[index].actionFilterOrder === step) {
                result = true;
            }
            return result;
        };

        $scope.removeFilterSet = function (data) {
            var removeFilterSetSelected = function (data) {

                if (data['@type'] === 'FilterSetDto') {
                    $scope.task.taskConfig.steps.removeObject(data);
                } else if (data['@type'] === 'FilterActionSetDto') {
                    $scope.changeActionData(data);
                    $scope.task.actions.removeObject(data);
                }
                $scope.taskStepNumber -= 1;
                if (!$scope.$$phase) {
                    $scope.$apply($scope.task);
                }
            };

            if (data.filters !== undefined && data.filters.length > 0) {
                ModalFactory.showConfirm('task.confirm.filterSet', "task.header.confirm", function (val) {
                    if (val) {
                        removeFilterSetSelected(data);
                    }
                });
            } else {
                removeFilterSetSelected(data);
            }
        };

        $scope.changeActionData = function (data) {
            var i;

            angular.forEach($scope.task.actions, function (action, idx){
                if(action === data) {
                    if($scope.selectedAction[idx+1] !== undefined) {
                        $scope.selectedAction[idx] = angular.copy($scope.selectedAction[idx+1]);
                        $scope.selectedAction.removeObject($scope.selectedAction[idx+1]);
                        $scope.selectedActionChannel[idx] = angular.copy($scope.selectedActionChannel[idx+1]);
                        $scope.selectedActionChannel.removeObject($scope.selectedActionChannel[idx+1]);
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
                        'eventKey' : text,
                        'type': 'UNICODE'
                    };
                } else if (type === $scope.util.DATA_SOURCE_PREFIX && splitted.length === 5 && splitted[4] !== '') {
                    text = splitted[3].split('#');
                    select = $scope.util.find({
                        msg: $scope.msg,
                        where: $scope.task.taskConfig.steps,
                        by: [{
                            what: '@type',
                            equalTo: 'DataSourceDto'
                        }, {
                            what: 'providerName',
                            equalTo: splitted[1] + '.' + splitted[2]
                        }, {
                            what: 'type',
                            equalTo: text[0]
                        }]
                    });

                    if (select) {
                        text = $scope.findObject(select.providerName, text[0]);
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
                if (select.eventKey) {
                    filter.key = "{0}.{1}".format($scope.util.TRIGGER_PREFIX, select.eventKey);
                    filter.displayName = filter.key;
                    filter.type = select.type;
                } else {
                    filter.key = empty;
                    filter.type = empty;
                }
                break;
            case $scope.util.DATA_SOURCE_PREFIX:
                filter.key = "{0}.{1}.{2}#{3}.{4}".format($scope.util.DATA_SOURCE_PREFIX, select.providerName, select.type, select.objectId, field.fieldKey);
                filter.displayName = "{0}.{1}.{2}#{3}.{4}".format($scope.util.DATA_SOURCE_PREFIX, select.providerName, select.type, select.objectId, field.fieldKey);
                filter.type = field.type;
                break;
            default:
                filter.key = empty;
                filter.type = empty;
            }
        };

        $scope.setFilterOperators = function(type) {
            if ($scope.util.isNumber(type)) {
                $scope.filterOperators = $scope.util.FILTER_OPERATORS['task.number'].options;
            }  else if ($scope.util.isDate(type)) {
                $scope.filterOperators = $scope.util.FILTER_OPERATORS['task.date'].options;
            } else if ($scope.util.isBoolean(type)) {
                $scope.filterOperators = $scope.util.FILTER_OPERATORS['task.boolean'].options;
            } else {
                $scope.filterOperators = $scope.util.FILTER_OPERATORS['task.string'].options;
            }
        };

        $scope.getPopoverType = function(filter) {
            if (!filter.manipulations || !Array.isArray(filter.manipulations)) {
                if (filter.displayName && filter.key) {
                    var manipulations, manipulationsBuff;
                    manipulationsBuff = filter.key.split('?');
                    manipulationsBuff.shift();
                    manipulations = [];
                    manipulationsBuff.forEach(function (manipulationStr) {
                        var manipulation = {},
                        parts = manipulationStr.split('(');
                        manipulation.type = parts.shift();
                        if(parts.length > 0) {
                            manipulation.argument = parts[0].replace(')','');
                        }
                        manipulations.push(manipulation);
                    });
                    filter.manipulations = manipulations;
                }
            }
            if (filter.type === 'UNICODE' || filter.type === 'TEXTAREA') {
                return "STRING";
            } else if (filter.type === 'DATE') {
                return "DATE";
            }
        };

        $scope.addSpecifiedDataSourceName = function (changedStep) {
            var steps = $scope.task.taskConfig.steps;

            steps.forEach(function (step) {
                if (step.order === changedStep.order) {
                    step.specifiedName = changedStep.specifiedName;
                }
            });

            if (!$scope.$$phase) {
                $scope.$apply($scope.task);
            }
        };

        $scope.addSpecifiedActionName = function (changedAction, changedActionIndex) {
            var actions = $scope.task.actions;

            actions.forEach(function (action, index) {
                if (index === changedActionIndex) {
                    action.specifiedName = changedAction.specifiedName;
                }
            });

            if (!$scope.$$phase) {
                $scope.$apply($scope.task);
            }
        };

        $scope.addDataSource = function () {
            var sources = $scope.getDataSources(),
                last;

            last = sources && sources.last();
            $scope.taskStepNumber += 1;

            $scope.task.taskConfig.steps.push({
                '@type': 'DataSourceDto',
                objectId: (last && last.objectId + 1) || 0,
                order: $scope.taskStepNumber
            });

            if (!$scope.$$phase) {
                $scope.$apply($scope.task);
            }
        };

        $scope.removeData = function (dataSource) {
            if (dataSource.type !== undefined || (dataSource.providerName !== undefined && dataSource.providerName !== '')) {
                ModalFactory.showConfirm('task.confirm.dataSource', "task.header.confirm", function (val) {
                    if (val) {
                        $scope.task.taskConfig.steps.removeObject(dataSource);

                        if (!$scope.$$phase) {
                            $scope.$apply($scope.task);
                        }
                    }
                });
            } else {
                $scope.task.taskConfig.steps.removeObject(dataSource);

                if (!$scope.$$phase) {
                    $scope.$apply($scope.task);
                }
            }
            $scope.taskStepNumber -= 1;
        };

        $scope.getDataSources = function () {
            if ($scope.task.taskConfig === undefined) {
                return;
            }
            return $scope.util.find({
                where: $scope.task.taskConfig.steps,
                by: [{
                    what: '@type',
                    equalTo: 'DataSourceDto'
                }],
                unique: false
            });
        };

        $scope.findDataSource = function (providerName) {
            return $scope.util.find({
                where: $scope.dataSources,
                by: {
                    what: 'name',
                    equalTo: providerName
                }
            });
        };

        $scope.findObject = function (providerName, type, id) {
            var dataSource = $scope.findDataSource(providerName),
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
            if (dataSource.providerName) {
                ModalFactory.showConfirm('task.confirm.changeDataSource', 'task.header.confirm', function (val) {
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
                ModalFactory.showConfirm('task.confirm.changeObject', 'task.header.confirm', function (val) {
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

        $scope.hasUnknownTrigger = function (value) {
            var unknown = false,
                regex, found, data, indexOf, prefix, dataArray, key, param;

            // check bubbles
            prefix = $scope.util.TRIGGER_PREFIX;
            regex = new RegExp('<span.*data-prefix="'+ prefix +'".*data-type="UNKNOWN".*>\\[.*\\]</span>', "g");
            unknown = regex.exec(value) !== null;

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

        $scope.save = function (enabled) {
            var actionOrder = [], taskOrder = [], filtersToDelete = [], success = function (response) {
                var alertMessage = enabled ? $scope.msg('task.success.savedAndEnabled') : $scope.msg('task.success.saved'),
                loc, indexOf, errors = response.validationErrors || response;

                if (errors.length > 0) {
                    alertMessage = $scope.util.createErrorMessage($scope, errors, true);
                }
                LoadingModal.close();
                ModalFactory.showAlert({
                    type: 'type-success',
                    message: alertMessage,
                    callback: function () {

                        loc = window.location.toString();
                        indexOf = loc.indexOf('#');

                        window.location = "{0}#/tasks/dashboard".format(loc.substring(0, indexOf));
                    }
                });
            },
            error = function (response) {
                var data = (response && response.data) || response;
                $scope.task.actions = angular.copy(actionOrder);
                $scope.task.taskConfig.steps = angular.copy(taskOrder);

                angular.forEach($scope.task.actions, function (action) {
                    delete action.values;
                });

                delete $scope.task.enabled;

                LoadingModal.close();
                ModalFactory.showErrorAlert(null, 'task.header.error', $scope.util.createErrorMessage($scope, data, false));
            };

            $scope.task.enabled = enabled;

            angular.forEach($scope.selectedAction, function (action, idx) {
                if ($scope.task.actions[idx].values === undefined) {
                    $scope.task.actions[idx].values = {};
                }

                if ($scope.task.actions[idx].name === undefined && action.name !== undefined) {
                    $scope.task.actions[idx].name = action.name;
                }

                if ($scope.task.actions[idx].specifiedName === undefined && action.specifiedName !== undefined) {
                    $scope.task.actions[idx].specifiedName = action.specifiedName;
                }

                angular.forEach(action.actionParameters, function (param) {
                    if (param && typeof param.value === "string") {
                        while(param.value.indexOf("\u00a0") >= 0) {
                            param.value = param.value.replace("\u00a0", " ");
                        }
                        param.value = param.value.trim();
                    }
                    $scope.task.actions[idx].values[param.key] = param.value;

                    if (!param.required && isBlank($scope.task.actions[idx].values[param.key])) {
                        delete $scope.task.actions[idx].values[param.key];
                    }
                });
            });
            actionOrder = angular.copy($scope.task.actions);
            taskOrder = angular.copy($scope.task.taskConfig.steps);

            angular.forEach($scope.task.actions, function (action, idx) {
                if (action['@type'] === 'FilterActionSetDto') {
                    $scope.task.taskConfig.steps.push({
                        '@type': 'FilterSetDto',
                        filters: action.filters,
                        operator: action.operator,
                        actionFilterOrder: idx
                    });
                    filtersToDelete.push(action);
                }
            });

            angular.forEach(filtersToDelete, function(filter) {
                $scope.task.actions.removeObject(filter);
            });

            angular.forEach($scope.task.taskConfig.steps, function (step) {
                if (step['@type'] === 'DataSourceDto') {
                    if (step.lookup === undefined) {
                        step.lookup = [];
                    }
                }
            });

            angular.forEach($scope.task.taskConfig.steps, function (step) {
                angular.forEach(step.filters, function (filter) {
                    var manipulations = [];
                    angular.forEach(filter.manipulations, function (manipulation) {
                        manipulations.push(manipulation.type + '=' + manipulation.argument);
                    });
                    filter.manipulations = manipulations;
                });
            });

            if (!$scope.task.useTimeWindow) {
                $scope.task.startTime = undefined;
                $scope.task.endTime = undefined;
            } else {
                $scope.task.startTime = $scope.startTime;
                $scope.task.endTime = $scope.endTime;
                $scope.task.days = [];
                $scope.days.forEach(function(day, idx) {
                    $scope.task.days[idx] = day.checked.toString();
                });
            }

            LoadingModal.open();

            if (!$stateParams.taskId) {
                $http.post('../tasks/api/task/save', $scope.task).success(success).error(error);
            } else {
                $http.post('../tasks/api/task/' + $stateParams.taskId, $scope.task).success(success).error(error);
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

                expression = !value || value.length === 0 || value === "\n";
            }

            return expression;
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

        $scope.taskMsg = function(message) {
            if (message === undefined) {
                return "";
            }
            message = $scope.msg(message);
            if (message[0] === '[' && message[message.length-1] === ']') {
                return message.substr(1, message.length-2);
            } else {
                return message;
            }
        };

        $scope.$on('triggerSelected', function(event, args) {
            $scope.selectedTrigger = args.selectedTrigger;
        });

        $scope.getAvailableFields = function () {
            var dataSources, fields = [];
            if($scope.selectedTrigger) {
                $scope.selectedTrigger.eventParameters.forEach(function (_field) {
                    var field = JSON.parse(JSON.stringify(_field));
                    field.prefix = ManageTaskUtils.TRIGGER_PREFIX;
                    fields.push(field);
                });
            }
            dataSources = $scope.getDataSources();
            if (dataSources && Array.isArray(dataSources)) {
                dataSources.forEach(function (source) {
                    var service = $scope.findObject(source.providerName, source.type);
                    if (!service || !service.fields){
                        return false;
                    }
                    service.fields.forEach(function (_field) {
                        var field =  JSON.parse(JSON.stringify(_field));
                        field.prefix = ManageTaskUtils.DATA_SOURCE_PREFIX;
                        field.serviceName = service.displayName;
                        field.providerName = source.providerName;
                        field.providerType = source.type;
                        field.specifiedParentName = source.specifiedName;
                        field.objectId = source.objectId;
                        fields.push(field);
                    });
                });
            }

            if ($scope.selectedAction && Array.isArray($scope.selectedAction)) {
                $scope.selectedAction.forEach(function (action, idx) {
                    if (action.postActionParameters && Array.isArray(action.postActionParameters)) {
                        action.postActionParameters.forEach(function (postActionParameter) {
                            postActionParameter.prefix = ManageTaskUtils.POST_ACTION_PREFIX;
                            postActionParameter.objectId = idx;
                            postActionParameter.channelName = $scope.task.actions[idx].channelName;
                            postActionParameter.actionName = $scope.task.actions[idx].displayName;
                            postActionParameter.specifiedParentName = $scope.task.actions[idx].specifiedName;
                            postActionParameter.displayName = postActionParameter.prefix.concat(".", postActionParameter.objectId,
                                                                                                ".", postActionParameter.key
                                                                                                );
                            fields.push(postActionParameter);
                        });
                    }
                });
            }

            return fields;
        };

        $scope.$watchCollection(function(){
            var fieldIds = [];
            $scope.getAvailableFields().forEach(function(field){
                fieldIds.push(field.id);
            });
            return fieldIds;
        }, function() {
           $scope.fields = $scope.getAvailableFields();
        });

        $scope.openHelpStringManipulation = function () {
            HelpStringManipulation.open($scope);
        };

        $scope.getKeys = function (eventParameters) {
            var newArray = [];
            eventParameters.forEach(function (param) {
                newArray.push('trigger.'+param.eventKey);
            });
            return newArray;
        };

        $scope.getLastSelectedField = function () {
            return $scope.lastSelectedField;
        };

        $scope.setLastSelectedField = function (lastSelectedField) {
            $scope.lastSelectedField = lastSelectedField;
        };
    });

    controllers.controller('TasksLogCtrl', function ($scope, Tasks, Activities, $stateParams, $filter, $http,
                            ModalFactory, LoadingModal, BootstrapDialogManager) {
        var data, task, allTasks, i, selectedTaskId, url, keys;

        $scope.taskId = $stateParams.taskId;
        $scope.activityTypes = ['In progress', 'Success', 'Warning', 'Error', 'Filtered'];
        $scope.stackTraceEl = [];
        $scope.allTaskTypes = ['All'];
        $scope.failedTasks = [];

        $scope.selectedActivityType = ['In progress', 'Success', 'Error', 'Filtered'];
        $scope.selectedTaskType = 'All';

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        allTasks = Tasks.query(function () {
            for (i = 0; i < allTasks.length; i += 1) {
                $scope.allTaskTypes.push(allTasks[i].name);
            }
            $("#taskHistoryTable").trigger('reloadGrid');
        });

        if ($stateParams.taskId) {
            data = { taskId: $scope.taskId };

            task = Tasks.get(data, function () {

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
                $('#inner-center').trigger("change");
            });
        }

        $scope.getSelectedTaskId = function () {
            if (allTasks.$resolved) {
                if ($scope.selectedTaskType !== 'All') {
                    $scope.selectedTaskId = $.grep(allTasks, function (task) {
                        return task.name === $scope.selectedTaskType;
                    })[0].id;
                } else {
                    $scope.selectedTaskId = false;
                }
            }
        };

        $scope.doesTaskExist = function (taskId) {
            keys = Object.keys($scope.failedActivitiesWithTaskId);
            for(i = 0; i < keys.length; i += 1) {
                if ($scope.failedActivitiesWithTaskId[keys[i]] === taskId) {
                    return true;
                }
            }
            return false;
        };

        $scope.getTaskNameFromId = function (id) {
            if (allTasks.$resolved) {
                return $.grep(allTasks, function (task) {
                    return task.id === id;
                })[0].name;
            }
        };

        $scope.retryFailedTasks = function () {
            $http.post('../tasks/api/activity/retryMultiple', $scope.failedTasks)
                 .success(function () {
                      ModalFactory.showSuccessAlert('task.retryMultiple.info', 'task.retryMultiple.header');
                 })
                 .error(function() {
                      ModalFactory.showErrorAlert('task.retryMultiple.failed', 'task.retryMultiple.header');
                 });
        };

        $scope.filterHistory = function () {
            if ($scope.selectedTaskId) {
                url = '../tasks/api/activity/' + $scope.selectedTaskId;
            } else {
                url = '../tasks/api/activity/all';
            }

            $('#taskHistoryTable').jqGrid('setGridParam', {
                url: url,
                page: 1,
                postData: {
                    activityType: $scope.selectedActivityType.join(',').replace(' ', '_').toUpperCase(),
                    dateTimeFrom: $('#dateTimeFrom').val() ? $('#dateTimeFrom').val() : null,
                    dateTimeTo: $('#dateTimeTo').val() ? $('#dateTimeTo').val() : null
                }
            }).trigger('reloadGrid');
        };

        $scope.changeActivityTypeFilter = function () {
            $('#taskHistoryTable').jqGrid('setGridParam', {
                page: 1,
                postData: {
                    activityType: $scope.selectedActivityType.join(',').replace(' ', '_').toUpperCase()
                }}).trigger('reloadGrid');
        };

        $scope.refresh = function () {
            $("#taskHistoryTable").trigger('reloadGrid');
        };

        $scope.clearHistory = function () {
            ModalFactory.showConfirm('task.history.confirm.clearHistory', 'task.history.confirm.clear',function (r) {
                if (!r) {
                    return;
                }
                LoadingModal.open();
                Activities.remove({taskId: $stateParams.taskId}, function () {
                     $scope.refresh();
                     LoadingModal.close();
                 }, function (response) {
                     LoadingModal.close();
                     ModalFactory.showErrorAlertWithResponse('task.history.deleteError', 'task.header.error', response);
                 });
            });
        };

        $scope.retryTask = function (activityId) {
            $http.post('../tasks/api/activity/retry/' + activityId)
                .success(function () {
                    ModalFactory.showSuccessAlert('task.retry.info', 'task.retry.header');
                })
                .error(function() {
                    ModalFactory.showErrorAlert('task.retry.failed', 'task.retry.header');
                });
        };

        $scope.showStackTrace = function (index) {
            var dialog = new BootstrapDialog({
                title: 'Stack trace',
                message: $scope.stackTraceEl[index],
                buttons: [{
                    label: $scope.msg('task.close'),
                    cssClass: 'btn btn-default',
                    action: function (dialogItself) {
                        BootstrapDialogManager.close(dialogItself);
                    }
                }],
                onhide: function (dialog) {
                    BootstrapDialogManager.onhide(dialog);
                }
            });
            BootstrapDialogManager.open(dialog);
        };
    });


    controllers.controller('TasksSettingsCtrl', function ($scope, Settings, ModalFactory) {
        $scope.settings = Settings.get();

        $scope.retry = {
            value: undefined
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.submit = function() {
            $scope.settings.$save(function() {
                ModalFactory.showSuccessAlert('task.settings.success.saved', 'server.saved');
            }, function() {
                ModalFactory.showErrorAlert('task.settings.error.saved', 'server.error');
            });
        };

        $scope.addTaskRetry = function(retry) {
            var number = $scope.getRetryNumber();
            $scope.settings.taskRetries[number.toString()] = retry.value;
            $scope.retry = {};
        };

        $scope.removeTaskRetry = function(name) {
            delete $scope.settings.taskRetries[name];
        };

        $scope.getRetryNumber = function() {
            var key,parts, number = 0;
            for (key in $scope.settings.taskRetries) {
                number = parseInt(key, 10);
            }
            return number + 1;
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

    controllers.controller('MapsCtrl', function ($scope, ModalFactory) {
        var exp, values, keyValue, dragAndDrop = $scope.BrowserDetect.browser === 'Chrome' || $scope.BrowserDetect.browser === 'Explorer' || $scope.BrowserDetect.browser === 'Firefox';

        if (dragAndDrop) {
            exp = /((?::)((?!<span|<br>|>)[\w\W\s])+(?=$|<span|<\/span>|<br>))|((?:<br>)((?!<span|<br>)[\w\W\s])*(?=:))|(<span((?!<span)[\w\W\s])*<\/span>)/g;
        } else {
            exp = /((?:^|\n|\r)[\w{}\.#\s]*(?=:)|(:[\w{}\.#\s]*)(?=\n|$))/g;
        }

        $scope.data = $scope.$parent.$parent.$parent.i;
        $scope.pairs = [];
        $scope.pair = {key:"", value:""};
        $scope.dataTransformed = false;
        $scope.mapError = "";

        $scope.$watch(function (scope) {
            return scope.data.value;

        }, function () {
            var i,j,key,value;
            if ($scope.pairs.length === 0 && $scope.data.value !== "" && $scope.data.value !== null && !$scope.dataTransformed) {
                values = $scope.data.value.split("\n");

                for (i = 0; i < values.length; i += 1) {
                    keyValue = values[i].split(":");

                    exp = new RegExp("( relative;.*?\">.*<\/span>)");
                    for (j = 1; j < keyValue.length; j += 1) {
                        if (exp.test(keyValue[j])) {
                            keyValue[j-1] += ":" + keyValue[j];
                            keyValue.splice(j, 1);
                            j -= 1;
                        }
                    }

                    if (keyValue.length === 2) {
                        key = keyValue[0];
                        value = keyValue[1];
                        $scope.pairs.push({key:key, value:value});
                    }
                }

                $scope.dataTransformed = true;
            }
        });

        $scope.addPair = function (pair) {
            if ($scope.uniquePairKey(pair.key, -1)) {
                $scope.mapError = $scope.msg('task.error.duplicateMapKeys');
            } else if ($scope.emptyMap(pair)) {
                $scope.mapError = $scope.msg('task.error.emptyMapPair');
            } else {
                $scope.addToDataValue(pair, $scope.pairs.length);
                $scope.pairs.push({key: pair.key , value : pair.value});
                $scope.pair = {key:"", value:""};
                $scope.mapError = "";
            }
        };

       /**
       * Checks if the keys are unique.
       */
       $scope.uniquePairKey = function (mapKey, elementIndex) {
           var exp, keysList;
           elementIndex = parseInt(elementIndex, 10);
           exp = new RegExp('(<span.*?>)','g');
           keysList = function () {
               var resultKeysList = [];
               angular.forEach($scope.pairs, function (pair, index) {
                   if (pair !== null && pair.key !== undefined && pair.key.toString() !== '') {
                        if (index !== elementIndex) {
                            resultKeysList.push(pair.key.toString().replace(exp, ""));
                        }
                   }
               }, resultKeysList);
               return resultKeysList;
           };
           return $.inArray(mapKey.replace(exp, ""), keysList()) !== -1;
       };

       /**
       * Checks if the pair is empty.
       */
       $scope.emptyMap = function (pair) {
           return !(pair.key.toString().length > 0 && pair.value.toString().length > 0);
       };

        $scope.remove = function (index) {
            $scope.pairs.splice(index,1);
            $scope.data.value = "";

            $scope.pairs.forEach(function(element, index, array) {
                 $scope.addToDataValue(element, index);
             });
        };

        $scope.updateMap = function (pair, index) {
            if (!$scope.uniquePairKey(pair.key, index) && !$scope.emptyMap(pair)) {
                $scope.data.value = "";

                $scope.pairs.forEach(function(element, index, array) {
                     $scope.addToDataValue(element, index);
                });
            }
        };

        $scope.clearKey = function () {
            $scope.pair.key="";
        };

        $scope.clearValue = function () {
            $scope.pair.value="";
        };

        $scope.reset = function () {
            var resetMap = function () {
                $scope.pairs = [];
                $scope.data.value = "";

                if (!$scope.$$phase) {
                    $scope.$apply($scope.task);
                }
            };

            ModalFactory.showConfirm('task.confirm.reset.map', "task.header.confirm", function (val) {
                if (val) {
                    resetMap();
                }
            });
        };

        $scope.addToDataValue = function (pair, index) {
            if ($scope.data.value === null) {
                $scope.data.value = "";
            }

            if ($scope.data.value.length > 0) {
                $scope.data.value = $scope.data.value.concat("\n" + pair.key + ":" + pair.value);
            } else {
                $scope.data.value = $scope.data.value.concat(pair.key + ":" + pair.value);
            }
        };
    });

    controllers.controller('TriggersModalCtrl', function ($scope, $rootScope, $timeout, LoadingModal, BootstrapDialogManager, Triggers, ModalFactory, $filter) {

        $scope.searchTrigger = {
            displayName: ""
        };

        $scope.changeCurrentPage = function(staticTriggersPage, dynamicTriggersPage) {
            if ($scope.validatePages(staticTriggersPage, dynamicTriggersPage)) {
                $scope.staticTriggers.page = staticTriggersPage;
                $scope.dynamicTriggers.page = dynamicTriggersPage;
            }
        };

        $scope.$watch('searchTrigger.displayName', function() {
            var searchActive = true;

            if ($scope.hasStaticTriggers) {
                searchActive = $scope.changePagesAfterSearch($scope.staticTriggers, searchActive);
                if(searchActive === false) {
                    $scope.staticTriggers.total = $scope.staticTriggersPages;
                }
            }
            if ($scope.hasDynamicTriggers) {
                searchActive = $scope.changePagesAfterSearch($scope.dynamicTriggers, searchActive);
                if(searchActive === false) {
                    $scope.dynamicTriggers.total = $scope.dynamicTriggersPages;
                }
            }
        });

        $scope.changePagesAfterSearch = function(triggersTable, searchActive) {
            var searchedTriggers = [];

            triggersTable.page = 1;
            if ($scope.searchTrigger.displayName !== "") {
                searchedTriggers = $filter('filter')(triggersTable.triggers, $scope.searchTrigger);
                triggersTable.total = parseInt(searchedTriggers.length / $scope.pageSize, 10);
                if (searchedTriggers.length % $scope.pageSize > 0) {
                    triggersTable.total += 1;
                }
            } else {
                searchActive = false;
            }
            return searchActive;
        };

        $scope.validatePages = function(staticTriggersPage, dynamicTriggersPage){
            var valid = true;

            if ($scope.hasStaticTriggers) {
                if (staticTriggersPage === null ||
                    staticTriggersPage === undefined) {
                    valid = false;
                }
            }

            if ($scope.hasDynamicTriggers) {
                if (dynamicTriggersPage === null ||
                    dynamicTriggersPage === undefined) {
                    valid = false;
                }
            }

            return valid;
        };

        $scope.selectTrigger = function (channel, trigger) {
            if ($scope.task.trigger) {
                ModalFactory.showConfirm('task.confirm.trigger', "task.header.confirm", function (val) {
                    if (val) {
                        $scope.util.trigger.remove($scope);
                        $scope.util.trigger.select($scope, channel, trigger);
                        $rootScope.$broadcast('triggerSelected', { selectedTrigger: trigger });
                        BootstrapDialogManager.close($scope.triggersDialog);
                    }
                });
            } else {
                $timeout(function () {
                    $scope.util.trigger.select($scope, channel, trigger);
                    $rootScope.$broadcast('triggerSelected', { selectedTrigger: trigger });
                    BootstrapDialogManager.close($scope.triggersDialog);
                });
            }
        };

        $scope.openTriggersModal = function() {

             LoadingModal.open();
             $scope.pageSize = 10;
             $scope.staticTriggersPager = 1;
             $scope.dynamicTriggersPager = 1;
             $scope.selectedChannel = $scope.channel;
             Triggers.get(
             {
                 moduleName: $scope.channel.moduleName,
                 staticTriggersPage: $scope.staticTriggersPager,
                 dynamicTriggersPage: $scope.dynamicTriggersPager
             },
             function(data) {
                 $scope.dynamicTriggers = data.dynamicTriggersList;
                 $scope.staticTriggers = data.staticTriggersList;
                 $scope.hasDynamicTriggers = $scope.dynamicTriggers.triggers.length > 0;
                 $scope.hasStaticTriggers = $scope.staticTriggers.triggers.length > 0;
                 $scope.staticTriggersPages = $scope.staticTriggers.total;
                 $scope.dynamicTriggersPages = $scope.staticTriggers.total;
                 if ($scope.hasStaticTriggers && $scope.hasDynamicTriggers) {
                     $scope.divSize = "col-md-6";
                 } else {
                     $scope.divSize = "col-md-12";
                 }
                 BootstrapDialogManager.open($scope.triggersDialog);
                 LoadingModal.close();
             });
        };
    });

    controllers.controller('ImportModalCtrl', function ($scope, BootstrapDialogManager) {

        $scope.openImportTaskModal = function () {
            BootstrapDialogManager.open($scope.importDialog);
        };
    });

}());
