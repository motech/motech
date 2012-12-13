'use strict';

/* Controllers */

function DashboardCtrl($scope, Tasks, Activities) {
    $scope.activeTasks = [];
    $scope.pausedTasks = [];

    var tasks = Tasks.query(function () {
        var item, task, i;

        for (i = 0; i < tasks.length; i += 1) {
            task = tasks[i];
            item = {
                task: task,
                success: Activities.query({taskId: task._id, type: 'success'}),
                error: Activities.query({taskId: task._id, type: 'error'})
            }

            if (task.enabled) {
                $scope.activeTasks.push(item);
            } else {
                $scope.pausedTasks.push(item);
            }
        }
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
}

function ManageTaskCtrl($scope, Channels, Tasks, $routeParams, $http) {
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.task = {};
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

}
