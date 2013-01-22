'use strict';

/* Controllers */

function DashboardCtrl($scope, Tasks, Activities) {
    var RECENT_TASK_COUNT = 7;

    $scope.activeTasks = [];
    $scope.pausedTasks = [];
    $scope.activities = [];
    $scope.showActive = true;
    $scope.showPaused = true;

    var tasks = Tasks.query(function () {
        var activities = Activities.query(function () {
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

                if (item.task.enabled) {
                    $scope.activeTasks.push(item);
                } else {
                    $scope.pausedTasks.push(item);
                }
            }

            for (i = 0; i < RECENT_TASK_COUNT && i < activities.length; i += 1) {
                for (j = 0 ; j < tasks.length; j += 1) {
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
        });
    });

    $scope.get = function (taskEvent, prop) {
        var index;

        switch (prop) {
            case 'displayName': index = 0; break;
            case 'moduleName': index = 1; break;
            case 'moduleVersion': index = 2; break;
            case 'subject': index = 3; break;
            default: index = 0; break;
        }

        return taskEvent.split(':')[index];
    };

    $scope.enableTask = function (item, enabled) {
        item.task.enabled = enabled;

        item.task.$save(function () {
            if (item.task.enabled) {
                $scope.pausedTasks.removeObject(item);
                $scope.activeTasks.push(item);
            } else {
                $scope.activeTasks.removeObject(item);
                $scope.pausedTasks.push(item);
            }
        });
    }

    $scope.deleteTask = function (item) {
        var enabled = item.task.enabled;

        jConfirm(jQuery.i18n.prop('task.confirm.remove'), jQuery.i18n.prop("header.confirm"), function (val) {
            if (val) {
                item.task.$remove(function () {
                    if (enabled) {
                        $scope.activeTasks.removeObject(item);
                    } else {
                        $scope.pausedTasks.removeObject(item);
                    }
                }, alertHandler('task.error.removed', 'header.error'));
            }
        });
    };

    $scope.setShowActive = function () {
       if($scope.showActive != true) {
            $scope.showActive = true;
       } else {
            $scope.showActive = false;
       }
    }

    $scope.setShowPaused = function () {
       if($scope.showPaused != true) {
            $scope.showPaused = true;
       } else {
            $scope.showPaused = false;
       }
    }
}

function ManageTaskCtrl($scope, Channels, Tasks, $routeParams, $http) {
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.task = {};
    $scope.filters = [];
    $scope.negationOperators = [{key:'info.filter.is',value:'true'}, {key:'info.filter.isNot',value:'false'}];

    $scope.channels = Channels.query(function (){
        if ($routeParams.taskId != undefined) {
            $scope.task = Tasks.get({ taskId: $routeParams.taskId }, function () {
                var trigger = $scope.task.trigger.split(':'), action = $scope.task.action.split(':'),
                    i;

                $scope.setTaskEvent('trigger', trigger[0], trigger[1], trigger[2]);
                $scope.setTaskEvent('action', action[0], action[1], action[2]);

                for (i = 0; i < $scope.draggedTrigger.events.length; i += 1) {
                    if ($scope.draggedTrigger.events[i].subject == trigger[3]) {
                        $scope.selectedTrigger = $scope.draggedTrigger.events[i];
                        $scope.draggedTrigger.display = $scope.selectedTrigger.displayName;
                        break;
                    }
                }

                for (i = 0; i < $scope.draggedAction.events.length; i += 1) {
                    if ($scope.draggedAction.events[i].subject == action[3]) {
                        $scope.selectedAction = $scope.draggedAction.events[i];
                        $scope.draggedAction.display = $scope.selectedAction.displayName;
                        break;
                    }
                }

                for (i = 0; i < $scope.selectedAction.eventParameters.length; i += 1) {
                    $scope.selectedAction.eventParameters[i].value = $scope.task.actionInputFields[$scope.selectedAction.eventParameters[i].eventKey];
                }

                $scope.filters = [];
                if ($scope.task.filters) {
                    for (i = 0; i<$scope.task.filters.length; i += 1) {
                        for (var j = 0; j <  $scope.selectedTrigger.eventParameters.length; j+=1) {
                            if ( $scope.selectedTrigger.eventParameters[j].displayName==$scope.task.filters[i].eventParameter.displayName) {
                                $scope.task.filters[i].eventParameter=$scope.selectedTrigger.eventParameters[j];
                                break;
                            }
                        }
                        if ($scope.task.filters[i].negationOperator) {
                            $scope.task.filters[i].negationOperator = $scope.negationOperators[0];
                        } else {
                            $scope.task.filters[i].negationOperator = $scope.negationOperators[1];
                        }
                        $scope.filters.push($scope.task.filters[i]);
                    }
                }
            });
        }
    });

    $scope.setTaskEvent = function (taskEventType, channelName, moduleName, moduleVersion) {
        var channel, selected, i, j;

        for (i = 0; i < $scope.channels.length; i += 1) {
            channel = $scope.channels[i];

            if (channel.displayName == channelName && channel.moduleName == moduleName && channel.moduleVersion == moduleVersion) {
                selected = {
                    display: channelName,
                    channel: channelName,
                    module: moduleName,
                    version: moduleVersion,
                };

                if (taskEventType === 'trigger') {
                    $scope.draggedTrigger = selected;
                    $scope.draggedTrigger.events = channel.triggerTaskEvents;
                } else if (taskEventType === 'action') {
                    for (j = 0; j < channel.actionTaskEvents.length; j += 1) {
                        delete channel.actionTaskEvents[j].value;
                    }

                    $scope.draggedAction = selected;
                    $scope.draggedAction.events = channel.actionTaskEvents;
                }

                break;
            }
        }
    };

    $scope.selectTaskEvent = function (taskEventType, taskEvent) {
        if (taskEventType === 'trigger') {
            $scope.draggedTrigger.display = taskEvent.displayName;
            $scope.task.trigger = "{0}:{1}:{2}:{3}".format($scope.draggedTrigger.channel, $scope.draggedTrigger.module, $scope.draggedTrigger.version, taskEvent.subject);
            $scope.selectedTrigger = taskEvent;
        } else if (taskEventType === 'action') {
            $scope.draggedAction.display = taskEvent.displayName;
            $scope.task.action = "{0}:{1}:{2}:{3}".format($scope.draggedAction.channel, $scope.draggedAction.module, $scope.draggedAction.version, taskEvent.subject);
            $scope.selectedAction = taskEvent;
        }

        delete $scope.task.actionInputFields;

        if ($scope.selectedAction != undefined) {
            var i;

            for (i = 0; i < $scope.selectedAction.eventParameters.length; i += 1) {
                delete $scope.selectedAction.eventParameters[i].value;
            }
        }
    };

    $scope.getTooltipMsg = function(selected) {
        return selected !== undefined ? $scope.msg('help.doubleClickToEdit') : '';
    }

    $scope.save = function (enabled) {
        var action = $scope.selectedAction, i, eventKey, value;

        $scope.task.actionInputFields = {};
        $scope.task.enabled = enabled;

        $scope.task.filters = [];
        if ($scope.filters.length!=0) {
            for (i = 0; i < $scope.filters.length; i += 1) {
                value = $scope.filters[i];
                value.negationOperator = $scope.filters[i].negationOperator.value;
                $scope.task.filters.push(value);
            }
        }

        for (i = 0; i < action.eventParameters.length; i += 1) {
            eventKey = action.eventParameters[i].eventKey;
            value = action.eventParameters[i].value || '';

            $scope.task.actionInputFields[eventKey] = value;
        }

        blockUI();
        $http.post('../tasks/api/task/save', $scope.task).
            success(function () {
                var msg = enabled ? 'task.success.savedAndEnabled' : 'task.success.saved', loc, indexOf;

                unblockUI();

                motechAlert(msg, 'header.saved', function () {
                    loc = new String(window.location);
                    indexOf = loc.indexOf('#');

                    window.location = loc.substring(0, indexOf) + "#/dashboard";
                });
            }).error(function () {
                delete $scope.task.actionInputFields;
                delete $scope.task.enabled;

                alertHandler('task.error.saved', 'header.error');
            });
    };

    $scope.operators = function(event) {
        var operator = ['exist'];
        if (event && (event.type==='UNICODE' || event.type==='TEXTAREA')) {
            operator.push("equals");
            operator.push("contains");
            operator.push("startsWith");
            operator.push("endsWith");
        } else if (event && event.type==='NUMBER') {
            operator.push("gt");
            operator.push("lt");
            operator.push("equal");
        }
        return operator;
    }

    $scope.addFilter = function() {
        $scope.filters.push({})
    }

    $scope.removeNode = function(filter) {
       $scope.filters.removeObject(filter);
    }

    $scope.validateForm = function() {
        var i, param;

        if ($scope.selectedAction !== undefined) {
            for (i = 0; i < $scope.selectedAction.eventParameters.length; i += 1) {
                param = $scope.selectedAction.eventParameters[i].value;

                if (param === null || param === undefined || !param.trim().length) {
                    return false;
                }
            }
        }
        if ($scope.task.name === undefined){
            return false;
        }

        return $scope.validateFilterForm();
    }

    $scope.validateFilterForm = function () {
        var isPass = true
        for(var i = 0; i < $scope.filters.length; i++) {
            if (!$scope.filters[i].eventParameter || !$scope.filters[i].negationOperator || !$scope.filters[i].operator) {
                isPass = false;
            }
            if ($scope.filters[i].operator && $scope.filters[i].operator!='exist' && !$scope.filters[i].expression ) {
                isPass = false;
            }
        }
        return isPass;
    }

    $scope.isDisabled = function(prop) {
        if(!prop) {
            return true;
        } else {
            return false;
        }
    }

    $scope.cssClass = function(prop) {
        var msg = 'validation-area';

        if (!prop) {
            msg = msg.concat(' error');
        }

        return msg;
    }

    $scope.actionCssClass = function(prop) {
        var msg = "control-group";

        if (!prop.value) {
            msg = msg.concat(' error');
        }

        return msg;
     }

    $scope.actionNameCssClass = function(prop) {
        var msg = "control-group";

        if (!prop.name) {
            msg = msg.concat(' error');
        }

        return msg;
     }
}

function LogCtrl($scope, Tasks, Activities, $routeParams) {
    if ($routeParams.taskId != undefined) {
        var data = { taskId: $routeParams.taskId }, task;

        task = Tasks.get(data, function () {
            $scope.activities = Activities.query(data);

            setInterval(function () {
                $scope.activities = Activities.query(data);
            }, 30 * 1000);

            $scope.trigger = {
                display: $scope.get(task.trigger, 'displayName'),
                module: $scope.get(task.trigger, 'moduleName'),
                version: $scope.get(task.trigger, 'moduleVersion')
            };

            $scope.action = {
                display: $scope.get(task.action, 'displayName'),
                module: $scope.get(task.action, 'moduleName'),
                version: $scope.get(task.action, 'moduleVersion')
            };

            $scope.description = task.description;
            $scope.enabled = task.enabled;
            $scope.name = task.name;
        });
    }

    $scope.get = function (taskEvent, prop) {
        var index;

        switch (prop) {
            case 'displayName': index = 0; break;
            case 'moduleName': index = 1; break;
            case 'moduleVersion': index = 2; break;
            case 'subject': index = 3; break;
            default: index = 0; break;
        }

        return taskEvent.split(':')[index];
    };
}
