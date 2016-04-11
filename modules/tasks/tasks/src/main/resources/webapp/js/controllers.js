(function () {

    'use strict';

    /* Controllers */

    var controllers = angular.module('tasks.controllers', []);

    controllers.controller('TasksDashboardCtrl', function ($scope, $filter, Tasks, Activities, $rootScope, $http, ManageTaskUtils,  ModalFactory, LoadingModal) {
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

                    $rootScope.search();
                    $('#inner-center').trigger("change");
                });
            });
        };

        $scope.enableTask = function (item, enabled) {
            item.task.enabled = enabled;

            $http.post('../tasks/api/task/' + item.task.id, item.task)
                .success(dummyHandler)
                .error(function (response) {
                    item.task.enabled = !enabled;
                    ModalFactory.alert({
                        type: 'type-danger',
                        title: $scope.msg('task.error.actionNotChangeTitle'),
                        message: $scope.util.createErrorMessage($scope, response, false)
                    });
                });
        };

        $scope.deleteTask = function (item) {
            ModalFactory.confirm({
                title: $scope.msg('task.header.confirm'),
                message: $scope.msg('task.confirm.remove'),
                type: 'type-warning',
                callback: function(result) {
                    if (result) {
                        LoadingModal.open();
                        item.task.$remove(function () {
                            $scope.allTasks.removeObject(item);
                            $rootScope.search();
                            $('#inner-center').trigger("change");
                            LoadingModal.close();
                        },
                            ModalFactory.alertHandler('task.error.removed', 'task.header.error')
                        );
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

        $scope.importTask = function () {
            LoadingModal.open();

            $('#importTaskForm').ajaxSubmit({
                success: function () {
                    $scope.getTasks();
                    $('#importTaskForm').resetForm();
                    $('#importTaskModal').modal('hide');
                    LoadingModal.close();
                },
                error: function (response) {
                    ModalFactory.handleResponse('task.header.error', 'task.error.import', response);
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

    controllers.controller('TasksManageCtrl', function ($scope, ManageTaskUtils, Channels, DataSources, Tasks, Triggers,
            $q, $timeout, $routeParams, $http, $compile, $filter, ModalFactory, LoadingModal) {
        $scope.util = ManageTaskUtils;
        $scope.selectedActionChannel = [];
        $scope.selectedAction = [];
        $scope.task = {
            taskConfig: {
                steps: []
            }
        };
        $scope.task.retryTaskOnFailure = false;

        $scope.openTriggersModal = function(channel) {
            LoadingModal.open();
            $scope.staticTriggersPager = 1;
            $scope.dynamicTriggersPager = 1;
            $scope.selectedChannel = channel;
            Triggers.get(
                {
                    moduleName: channel.moduleName,
                    staticTriggersPage: $scope.staticTriggersPager,
                    dynamicTriggersPage: $scope.dynamicTriggersPager
                },
                function(data) {
                    $scope.dynamicTriggers = data.dynamicTriggersList;
                    $scope.staticTriggers = data.staticTriggersList;
                    $scope.staticTriggersPage = $scope.staticTriggers.page;
                    $scope.dynamicTriggersPage = $scope.dynamicTriggers.page;
                    $("#staticTriggersPager").val($scope.staticTriggersPage);
                    $("#dynamicTriggersPager").val($scope.dynamicTriggersPage);
                    $scope.hasDynamicTriggers = $scope.dynamicTriggers.triggers.length > 0;
                    $scope.hasStaticTriggers = $scope.staticTriggers.triggers.length > 0;
                    if ($scope.hasStaticTriggers && $scope.hasDynamicTriggers) {
                        $scope.divSize = "col-md-6";
                    } else {
                        $scope.divSize = "col-md-12";
                    }
                    $('#triggersModal').modal('show');
                    LoadingModal.close();
                }
            );
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

        $scope.reloadLists = function(staticTriggersPage, dynamicTriggersPage) {
            if ($scope.validatePages(staticTriggersPage, dynamicTriggersPage)) {
                LoadingModal.open();
                Triggers.get(
                    {
                        moduleName: $scope.selectedChannel.moduleName,
                        staticTriggersPage: staticTriggersPage,
                        dynamicTriggersPage: dynamicTriggersPage
                    },
                    function(data) {
                        $scope.dynamicTriggers = data.dynamicTriggersList;
                        $scope.staticTriggers = data.staticTriggersList;
                        $scope.staticTriggersPage = $scope.staticTriggers.page;
                        $scope.dynamicTriggersPage = $scope.dynamicTriggers.page;
                        $("#staticTriggersPager").val($scope.staticTriggersPage);
                        $("#dynamicTriggersPager").val($scope.dynamicTriggersPage);
                        LoadingModal.close();
                    }
                );
            }
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.filter = $filter('filter');

        LoadingModal.open();

        $q.all([$scope.util.doQuery($q, Channels), $scope.util.doQuery($q, DataSources)]).then(function(data) {
            LoadingModal.open();

            $scope.channels = data[0];
            $scope.dataSources = data[1];

            if ($routeParams.taskId === undefined) {
                $scope.task = {
                    taskConfig: {
                        steps: []
                    }
                };
                $scope.task.retryTaskOnFailure = false;
            } else {
                $scope.task = Tasks.get({ taskId: $routeParams.taskId }, function () {
                    Triggers.getTrigger($scope.task.trigger, function(trigger) {
                        var triggerChannel, dataSource, object;

                        if ($scope.task.numberOfRetries > 0) {
                           $scope.task.retryTaskOnFailure = true;
                           $scope.task.retryIntervalInSeconds = $scope.task.retryIntervalInMilliseconds / 1000;
                        } else {
                           $scope.task.retryTaskOnFailure = false;
                        }

                        triggerChannel = $scope.util.find({
                            where: $scope.channels,
                            by: {
                                what: 'moduleName',
                                equalTo: $scope.task.trigger.moduleName
                            }
                        });

                        if (trigger) {
                            $scope.util.trigger.select($scope, triggerChannel, trigger);
                        }

                        angular.forEach($scope.task.taskConfig.steps, function (step) {
                            var source, object;

                            angular.element('#collapse-step-' + step.order).livequery(function() {
                                $(this).collapse('hide');
                            });

                            if (step['@type'] === 'DataSource') {
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
                                    angular.forEach(step.lookup, function(lookupField) {
                                        lookupField.value = $scope.util.convertToView($scope, 'UNICODE', lookupField.value);
                                    });
                                }
                            }
                        });

                        angular.forEach($scope.task.actions, function (info, idx) {
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
                });
            }

            LoadingModal.close();
        });

        $scope.isTaskValid = function() {
            // Retry task on failure inputs validation - only numerical non negative values
            var retryTaskOnFailureValidation;
            if ($scope.task.retryTaskOnFailure) {
                retryTaskOnFailureValidation = $scope.isNumericalNonNegativeValue($scope.task.numberOfRetries)
                && $scope.isNumericalNonNegativeValue($scope.task.retryIntervalInSeconds);
            } else {
                retryTaskOnFailureValidation = true;
            }

            return $scope.task.name && retryTaskOnFailureValidation;
        };

        $scope.isNumericalNonNegativeValue = function (value) {
            return !isNaN(value) && value >= 0;
        };

        $scope.selectTrigger = function (channel, trigger) {
            if ($scope.task.trigger) {
                ModalFactory.motechConfirm('task.confirm.trigger', "task.header.confirm", function (val) {
                    if (val) {
                        $scope.util.trigger.remove($scope);
                        $scope.util.trigger.select($scope, channel, trigger);
                        $('#triggersModal').modal('hide');
                    }
                });
            } else {
                $scope.util.trigger.select($scope, channel, trigger);
                $('#triggersModal').modal('hide');
            }
        };

        $scope.removeTrigger = function ($event) {
            $event.stopPropagation();

            ModalFactory.motechConfirm('task.confirm.trigger', "task.header.confirm", function (val) {
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
            var removeActionSelected = function (idx) {
                $scope.task.actions.remove(idx);
                $scope.selectedActionChannel.remove(idx);
                $scope.selectedAction.remove(idx);

                if (!$scope.$$phase) {
                    $scope.$apply($scope.task);
                }
            };

            if ($scope.selectedActionChannel[idx] !== undefined && $scope.selectedActionChannel[idx].displayName !== undefined) {
                ModalFactory.motechConfirm('task.confirm.action', "task.header.confirm", function (val) {
                    if (val) {
                        removeActionSelected(idx);
                    }
                });
            } else {
                removeActionSelected(idx);
            }
        };

        $scope.selectActionChannel = function (idx, channel) {
            if ($scope.selectedActionChannel[idx] && $scope.selectedAction[idx]) {
                ModalFactory.motechConfirm('task.confirm.action', "task.header.confirm", function (val) {
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
                ModalFactory.motechConfirm('task.confirm.action', "task.header.confirm", function (val) {
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
                operator: "AND",
                order: (lastStep && lastStep.order + 1) || 0
            });
        };

        $scope.removeFilterSet = function (data) {
            var removeFilterSetSelected = function (data) {
                $scope.task.taskConfig.steps.removeObject(data);

                if (!$scope.$$phase) {
                    $scope.$apply($scope.task);
                }
            };

            if (data.filters !== undefined && data.filters.length > 0) {
                ModalFactory.motechConfirm('task.confirm.filterSet', "task.header.confirm", function (val) {
                    if (val) {
                        removeFilterSetSelected(data);
                    }
                });
            } else {
                removeFilterSetSelected(data);
            }
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
                filter.key = "{0}.{1}".format($scope.util.TRIGGER_PREFIX, select.eventKey);
                filter.displayName = filter.key;
                filter.type = select.type;
                break;
            case $scope.util.DATA_SOURCE_PREFIX:
                filter.key = "{0}.{1}.{2}#{3}.{4}".format($scope.util.DATA_SOURCE_PREFIX, select.providerName, select.type, select.objectId, field.fieldKey);
                filter.displayName = "{0}.{1}.{2}#{3}.{4}".format($scope.util.DATA_SOURCE_PREFIX, select.providerName, select.type, select.objectId, field.fieldKey);
                filter.type = field.type;
                break;
            default:
                filter.key = empty;
            }
        };

        $scope.getPopoverType = function(filter) {
            if (filter.type === 'UNICODE' || filter.type === 'TEXTAREA') {
                return "STRING";
            } else if (filter.type === 'DATE') {
                return "DATE";
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
            if (dataSource.type !== undefined || (dataSource.providerName !== undefined && dataSource.providerName !== '')) {
                ModalFactory.motechConfirm('task.confirm.dataSource', "task.header.confirm", function (val) {
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
        };

        $scope.getDataSources = function () {
            if ($scope.task.taskConfig === undefined) {
                return;
            }
            return $scope.util.find({
                where: $scope.task.taskConfig.steps,
                by: [{
                    what: '@type',
                    equalTo: 'DataSource'
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
                ModalFactory.motechConfirm('task.confirm.changeDataSource', 'task.header.confirm', function (val) {
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
                ModalFactory.motechConfirm('task.confirm.changeObject', 'task.header.confirm', function (val) {
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
                isIE = $scope.util.isIE($scope),
                isFirefox = $scope.util.isFirefox($scope);

            result.find('em').remove();

            result.find('span[data-prefix]').replaceWith(function () {
                var span = $(this), prefix = span.data('prefix'),
                    manipulations = span.attr('manipulate') || '',
                    type = span.data('type'),
                    object = {}, key, source, array, val, i;

                switch (prefix) {
                case $scope.util.TRIGGER_PREFIX:
                    key = span.data('eventkey');
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
                    if ($scope.util.isText(type) || $scope.util.isDate(type) || $scope.util.isDate2Date(type) ) {
                        array = $scope.extractManipulations(manipulations);

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
                    val = '{{{0}.{1}.{2}#{3}.{4}}}'.format(prefix, source, object.type, object.id, key);
                    break;
                default:
                    val = key;
                }

                return val;
            });

            if (isIE) {
                result.find("p").replaceWith(function () {
                    return "{0}<br>".format(this.innerHTML);
                });

                result.find("br").last().remove();
            } else {
                result.find("div").replaceWith(function () {
                    return "\n{0}".format(this.innerHTML);
                });
            }

            if (result[0].childNodes[result[0].childNodes.length - 1] === '<br>') {
                result[0].childNodes[result[0].childNodes.length - 1].remove();
            }

            result.find("br").replaceWith("\n");

            return result.text();
        };

        $scope.extractManipulations = function (manipulations) {
            var extractedManipulations = [], insideManipulation = false, builtManipulation = "", i;

            for (i = 0; i < manipulations.length; i += 1) {
                if (manipulations[i] === "(") {
                    insideManipulation = true;
                } else if (manipulations[i] === ")") {
                    insideManipulation = false;
                }

                if (manipulations[i] === " " && !insideManipulation) {
                    extractedManipulations.push(builtManipulation);
                    builtManipulation = "";
                } else {
                    builtManipulation += manipulations[i];
                }
            }

            // we must add last manipulation to the array
            if (builtManipulation !== "") {
                extractedManipulations.push(builtManipulation);
            }

            return extractedManipulations;
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

        $scope.createDraggableElement = function (value, fieldType, forFormat) {
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
                                    newValue = element.replace(splittedValue[1], ds.providerName);
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
                    span, cuts = {dot: [], hash: []}, param, type, field, dataSource, providerName, object, id;

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
                        msg: $scope.taskMsg,
                        param: param,
                        prefix: prefix,
                        manipulations: manipulations,
                        fieldType: fieldType,
                        popover: forFormat
                    });
                    break;
                case $scope.util.DATA_SOURCE_PREFIX:
                    cuts.hash = key.split('#');
                    cuts.dot[0] = cuts.hash[0].split('.');
                    cuts.dot[1] = cuts.hash[1].split('.');

                    providerName = cuts.dot[0][0];
                    id = cuts.dot[1][0];

                    type = cuts.dot[0].slice(1).join('.');
                    field = cuts.dot[1].slice(1).join('.');

                    dataSource = $scope.util.find({
                        where: $scope.task.taskConfig.steps,
                        by: [{
                            what: '@type',
                            equalTo: 'DataSource'
                        }, {
                            what: 'providerName',
                            equalTo: providerName
                        }]
                    });

                    object = dataSource && $scope.findObject(dataSource.providerName, dataSource.type);

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
                        msg: $scope.taskMsg,
                        param: param,
                        prefix: prefix,
                        manipulations: manipulations,
                        fieldType: fieldType,
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
            var success = function (response) {
                var alertMessage = enabled ? $scope.msg('task.success.savedAndEnabled') : $scope.msg('task.success.saved'),
                loc, indexOf, errors = response.validationErrors || response;

                if (errors.length > 0) {
                    alertMessage = $scope.util.createErrorMessage($scope, errors, true);
                }
                LoadingModal.close();
                ModalFactory.alert({
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

                LoadingModal.close();
                ModalFactory.alert({
                    type: 'type-danger',
                    message: $scope.util.createErrorMessage($scope, data, false)
                });
            };

            $scope.task.enabled = enabled;

            angular.forEach($scope.selectedAction, function (action, idx) {
                if ($scope.task.actions[idx].values === undefined) {
                    $scope.task.actions[idx].values = {};
                }

                if ($scope.task.actions[idx].name === undefined && action.name !== undefined) {
                    $scope.task.actions[idx].name = action.name;
                }

                angular.forEach(action.actionParameters, function (param) {
                    $scope.task.actions[idx].values[param.key] = $scope.addDoubleBrackets($scope.util.convertToServer($scope, param.value));

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

            if (!$scope.task.retryTaskOnFailure) {
                // If the retryTaskOnFailure flag is set on false, we set the following properties to undefined.
                // The default values will be set for them in the backend.
                $scope.task.numberOfRetries = undefined;
                $scope.task.retryIntervalInMilliseconds = undefined;
            } else {
                // Convert given value from UI in seconds to milliseconds
                $scope.task.retryIntervalInMilliseconds = $scope.task.retryIntervalInSeconds * 1000;
            }

            LoadingModal.open();

            if (!$routeParams.taskId) {
                $http.post('../tasks/api/task/save', $scope.task).success(success).error(error);
            } else {
                $http.post('../tasks/api/task/' + $routeParams.taskId, $scope.task).success(success).error(error);
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
                value = $scope.refactorDivEditable(value);

                expression = !value || value.length === 0 || value === "\n";
            }

            return expression;
        };

        $scope.getBooleanValue = function (value) {
            return (value === 'true' || value === 'false') ? null : value;
        };

        $scope.setBooleanValue = function (action, index, value) {
            $scope.filter($scope.selectedAction[action].actionParameters, {hidden: false})[index].value = $scope.util.createBooleanSpan($scope, value);
        };

        $scope.checkedBoolean = function (action, index, val) {
            var prop = $scope.filter($scope.selectedAction[action].actionParameters, {hidden: false})[index],
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
            $timeout(function() {
                $scope.formatInput = [];
                $scope.formatInput = newData;
                $scope.$apply();
            }, 1);

            if (!$scope.$$phase) { // check if we are in digest
                $scope.$digest(); // run digest
            }
        };

        $scope.showFormatManipulation = function () {
            $scope.changeFormatInput($scope.getValues('true'));
            $('#formatManipulation').modal({keyboard: false});
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
                convertedValues.push($scope.createDraggableElement(value, manipulation, forFormat));
            });

            return convertedValues;
        };

        $scope.addFormatInput = function () {

            $scope.tempSaveInput();

            $timeout(function() {
                $scope.formatInput.push('');
                $scope.$apply();
            }, 0);
        };

        $scope.deleteFormatInput = function (indexToRemove) {
            var tempArray = [];

            $scope.tempSaveInput();

            angular.forEach($scope.formatInput, function (value, index) {
                if  (indexToRemove !== index) {
                    tempArray.push(value);
                }
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

            angular.forEach($scope.formatInput, function(value, index) {

                manipulation = manipulation + value;
                if (index !== $scope.formatInput.length - 1) {
                    manipulation = manipulation + ",";
                }
            });

            manipulation = manipulation + ")";
            manipulation = manipulation.replace(/\s+/g,"");

            elementManipulation = elementManipulation.replace(regex, manipulation);
            manipulateElement.attr("manipulate", elementManipulation);
            $timeout(function() {
                manipulateElement[0].focus();
            }, 0);
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
                                        what: 'providerName',
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
    });

    controllers.controller('TasksLogCtrl', function ($scope, Tasks, Activities, $routeParams, $filter, $http, ModalFactory, LoadingModal) {
        var data, task;

        $scope.taskId = $routeParams.taskId;
        $scope.activityTypes = ['All', 'Warning', 'Success', 'Error'];
        $scope.selectedActivityType = 'All';

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        if ($routeParams.taskId !== undefined) {
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
            });
        }

        $scope.changeActivityTypeFilter = function () {
            $('#taskHistoryTable').jqGrid('setGridParam', {
                page: 1,
                postData: {
                    activityType: ($scope.selectedActivityType === 'All') ? '' : $scope.selectedActivityType.toUpperCase()
                }}).trigger('reloadGrid');
        };

        $scope.refresh = function () {
            $("#taskHistoryTable").trigger('reloadGrid');
        };

        $scope.clearHistory = function () {
            ModalFactory.motechConfirm('task.history.confirm.clearHistory', 'task.history.confirm.clear',function (r) {
                if (!r) {
                    return;
                }
                LoadingModal.open();
                Activities.remove({taskId: $routeParams.taskId}, function () {
                     $scope.refresh();
                     LoadingModal.close();
                 }, function (response) {
                     LoadingModal.close();
                     ModalFactory.handleResponse('task.header.error', 'task.history.deleteError', response);
                 });
            });
        };

        $scope.retryTask = function (activityId) {
            $http.post('../tasks/api/activity/retry/' + activityId)
                .success(function () {
                    ModalFactory.motechAlert('task.retry.info', 'task.retry.header');
                })
                .error(function() {
                    ModalFactory.motechAlert('task.retry.failed', 'task.retry.header');
                });
        };
    });


    controllers.controller('TasksSettingsCtrl', function ($scope, Settings, ModalFactory) {
        $scope.settings = Settings.get();

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.submit = function() {
            $scope.settings.$save(function() {
                ModalFactory.motechAlert('task.settings.success.saved', 'server.saved');
            }, function() {
                ModalFactory.motechAlert('task.settings.error.saved', 'server.error');
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
                values = $scope.data.value.split("<br>");

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

                    key = keyValue[0];
                    value =  keyValue[1];

                    $scope.pairs.push({key:key, value:value});
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

            ModalFactory.motechConfirm('task.confirm.reset.map', "task.header.confirm", function (val) {
                if (val) {
                    resetMap();
                }
            });
        };

        $scope.addToDataValue = function (pair, index) {
            var paired;
            if (index > 0 && dragAndDrop) {
                paired = "<div>" + pair.key + ":" + pair.value + "</div>";
            } else {
                paired = pair.key + ":" + pair.value;
            }

            if(!dragAndDrop) {
                paired = paired.concat("\n");
            }

            if ($scope.data.value === null) {
                $scope.data.value = "";
            }

            $scope.data.value = $scope.data.value.concat(paired);
        };
    });
}());
