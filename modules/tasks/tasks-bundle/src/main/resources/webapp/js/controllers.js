'use strict';

/* Controllers */

function DashboardCtrl($scope, $filter, Tasks, Activities) {
    var RECENT_TASK_COUNT = 7;

    $scope.resetItemsPagination();
    $scope.allTasks = [];
    $scope.activities = [];
    $scope.hideActive = false;
    $scope.hidePaused = false;
    $scope.filteredItems = [];
    $scope.itemsPerPage = 10;
    $scope.currentFilter = 'allItems';

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
                $scope.allTasks.push(item);
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
            $scope.search();
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

        item.task.$save(dummyHandler, function(response) {
            item.task.enabled = !enabled;
            handleResponse('error.actionNotChangeTitle', 'error.actionNotChange', response);
        });
    }

    $scope.deleteTask = function (item) {
        var enabled = item.task.enabled;

        jConfirm(jQuery.i18n.prop('task.confirm.remove'), jQuery.i18n.prop("header.confirm"), function (val) {
            if (val) {
                item.task.$remove(function () {
                    $scope.allTasks.removeObject(item);
                    $scope.search();
                }, alertHandler('task.error.removed', 'header.error'));
            }
        });
    };

    var searchMatch = function (item, method, searchQuery) {
        if (!searchQuery) {
            if (method == 'pausedTaskFilter') {
                return item.task.enabled == true;
            } else if (method == 'activeTaskFilter'){
                return item.task.enabled == false;
            } else if (method == 'noItems'){
                return false;
            } else {
                return true;
            }
        } else if (method == 'pausedTaskFilter' && item.task.description) {
            return item.task.enabled == true &&
            (item.task.description.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1 ||
            item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1);
        } else if (method == 'activeTaskFilter' && item.task.description) {
            return item.task.enabled == false &&
            (item.task.description.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1 ||
            item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1);
        } else if (method == 'activeTaskFilter' && item.task.description) {
            return item.task.description.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1 ||
            item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
        } else if (method == 'allItems' && item.task.description) {
            return item.task.description.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1 ||
            item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
        } else if (method == 'pausedTaskFilter') {
            return item.task.enabled == true && item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
        } else if (method == 'activeTaskFilter') {
            return item.task.enabled == false && item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
        } else if (method == 'noItems'){
            return false;
        } else
            return item.task.name.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;

    };

    $scope.search = function () {

        $scope.filteredItems = $filter('filter')($scope.allTasks, function (item) {
            if (item) {
                if (searchMatch(item, $scope.currentFilter, $scope.query))
                    return true;
                }
                return false;
            });
        $scope.setCurrentPage(0);
        $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
    };


    $scope.setHideActive = function () {
        if($scope.hideActive == true) {
            $scope.hideActive = false;
            if($scope.hidePaused == true) {
                $scope.setFilter('pausedTaskFilter');
            } else {
                $scope.setFilter('allItems');
            }
            $('.setHideActive').find('i').removeClass("icon-ban-circle").addClass('icon-ok');
        } else {
            $scope.hideActive = true;
            if($scope.hidePaused == true) {
                $scope.setFilter('noItems');
            } else {
                $scope.setFilter('activeTaskFilter');
            }
            $('.setHideActive').find('i').removeClass("icon-ok").addClass('icon-ban-circle');
        }
    }

    $scope.setHidePaused = function () {
        if($scope.hidePaused == true) {
            $scope.hidePaused = false;
            if($scope.hideActive == true) {
                $scope.setFilter('activeTaskFilter');
            } else {
                $scope.setFilter('allItems');
            }
            $('.setHidePaused').find('i').removeClass("icon-ban-circle").addClass('icon-ok');
        } else {
            $scope.hidePaused = true;
            if($scope.hideActive == true) {
                $scope.setFilter('noItems');
            } else {
                $scope.setFilter('pausedTaskFilter');
            }
            $('.setHidePaused').find('i').removeClass("icon-ok").addClass('icon-ban-circle');
        }
    }

    $scope.setFilter = function (method) {
        $scope.currentFilter = method;
        $scope.search();
    }

}

function ManageTaskCtrl($scope, Channels, Tasks, DataSources, $routeParams, $http) {
    $scope.currentPage = {
        channels: 0,
        dataSource: 0,
        dataSourceObject: 0
    };

    $scope.pageSize = {
        channels : 10,
        dataSource: 1,
        dataSourceObject: 1
    };

    $scope.task = {};
    $scope.filters = [];
    $scope.negationOperators = [{key:'info.filter.is',value:'true'}, {key:'info.filter.isNot',value:'false'}];
    $scope.selectedDataSources = [];
    $scope.availableDataSources = [];
    $scope.allDataSources = DataSources.query(function () {
        $.merge($scope.availableDataSources, $scope.allDataSources);
    });

    $scope.channels = Channels.query(function (){
        if ($routeParams.taskId != undefined) {
            $scope.task = Tasks.get({ taskId: $routeParams.taskId }, function () {
                var trigger = $scope.task.trigger.split(':'),
                    action = $scope.task.action.split(':'),
                    regex = new RegExp('\\{\\{ad\\.(.+?)(\\..*?)\\}\\}', "g"),
                    found, replaced = [], dataSource, dataSourceId, ds, object, obj, eventKey, value, i, j;

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

                for (dataSourceId in $scope.task.additionalData) {
                    ds = $scope.findDataSourceById($scope.allDataSources, dataSourceId);
                    dataSource = { '_id': ds._id, 'name': ds.name, 'objects': [], 'available': ds.objects};

                    for (i = 0; i < $scope.task.additionalData[dataSourceId].length; i += 1) {
                        object = $scope.task.additionalData[dataSourceId][i];
                        obj = $scope.findObject(ds, object.type);

                        dataSource.objects.push({
                            id: object.id,
                            displayName: obj.displayName,
                            type: object.type,
                            fields: obj.fields,
                            lookupFields: obj.lookupFields,
                            lookup: {
                                displayName: $scope.findTriggerEventParameter(object.lookupValue).displayName,
                                by: object.lookupValue,
                                field: object.lookupField
                            }
                        });
                    }

                    $scope.selectedDataSources.push(dataSource);
                    $scope.availableDataSources.removeObject($scope.findDataSourceById($scope.availableDataSources, dataSourceId));
                }

                for (i = 0; i < $scope.selectedAction.eventParameters.length; i += 1) {
                    eventKey = $scope.selectedAction.eventParameters[i].eventKey;
                    value = $scope.task.actionInputFields[eventKey];

                    if ($scope.BrowserDetect.browser != 'Chrome') {
                        while ((found = regex.exec(value)) !== null) {
                            replaced.push({
                                find: '{{ad.' + found[1] + found[2] + '}}',
                                value: '{{ad.' + $scope.msg($scope.findDataSourceById($scope.selectedDataSources, found[1]).name) + found[2] + '}}'
                            });
                        }

                        for (j = 0; j < replaced.length; j += 1) {
                            value = value.replace(replaced[j].find, replaced[j].value);
                        };

                        $scope.selectedAction.eventParameters[i].value = value;
                    } else {
                        $scope.selectedAction.eventParameters[i].value = $scope.createDraggableElement(value);
                    }
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

    $scope.numberOfSelectedDataSources = function () {
        return Math.ceil($scope.selectedDataSources.length/$scope.pageSize.dataSource);
    }

    $scope.numberOfDataSourceObjects = function (dataSource) {
        return Math.ceil(dataSource.objects.length/$scope.pageSize.dataSourceObject);
    }

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
        var action = $scope.selectedAction,
            regex = new RegExp('\\{\\{ad\\.(.+?)(\\..*?)\\}\\}', "g"),
            eventKey, value, found, replaced = [], i, j;

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

        $scope.task.additionalData = {};
        for (i = 0; i < action.eventParameters.length; i += 1) {
            if ($scope.BrowserDetect.browser != 'Chrome') {
                angular.forEach($scope.selectedAction.eventParameters, function (param, index) {
                    var regex = new RegExp("\\{\\{ad\\..*?\\}\\}", "g"),
                        spans = [], r;

                    while ((r = regex.exec(param.value)) !== null) {
                        $.merge(spans, r);
                    }

                    angular.forEach(spans, function (span, index) {
                        var cuts = {}, source, type, id, exists = false, ds, object, i;

                        cuts.first = span.indexOf('.');
                        cuts.second = span.indexOf('.', cuts.first + 1);
                        cuts.third = span.indexOf('.', cuts.second + 1);

                        source = span.substring(cuts.first + 1, cuts.second);
                        type = span.substring(cuts.second + 1, cuts.third);
                        id = +type.substring(type.lastIndexOf('#') + 1);
                        type = type.substring(0, type.lastIndexOf('#'));

                        ds = $scope.findDataSourceByName($scope.selectedDataSources, source);

                        if ($scope.task.additionalData[ds._id] === undefined) {
                            $scope.task.additionalData[ds._id] = [];
                        }

                        for (i = 0; i < $scope.task.additionalData[ds._id].length; i += 1) {
                            if ($scope.task.additionalData[ds._id][i].id === id) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            object = $scope.findObject(ds, type, id);

                            $scope.task.additionalData[ds._id].push({
                                id: object.id,
                                type: object.type,
                                lookupField: object.lookup.field,
                                lookupValue: object.lookup.by
                            });
                        }

                    });
                });
            } else {
                $('<div>' + action.eventParameters[i].value + "</div>").find('span[data-prefix="ad"]').each(function(index, value) {
                    var span = $(value), source = span.data('source'),
                        objectType = span.data('object-type'), objectId = span.data('object-id'),
                        exists = false, dataSource, object, i;

                    dataSource = $scope.findDataSourceByName($scope.selectedDataSources, source);

                    if ($scope.task.additionalData[dataSource._id] === undefined) {
                        $scope.task.additionalData[dataSource._id] = [];
                    }

                    for (i = 0; i < $scope.task.additionalData[dataSource._id].length; i += 1) {
                        if ($scope.task.additionalData[dataSource._id][i].id === objectId) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        object = $scope.findObject(dataSource, objectType, objectId);

                        $scope.task.additionalData[dataSource._id].push({
                            id: object.id,
                            type: object.type,
                            lookupField: object.lookup.field,
                            lookupValue: object.lookup.by
                        });
                    }
                });
            }
        }

        for (i = 0; i < action.eventParameters.length; i += 1) {
            eventKey = action.eventParameters[i].eventKey;

            if ($scope.BrowserDetect.browser != 'Chrome') {
                value = action.eventParameters[i].value || '';
            } else {
                value = $scope.refactorDivEditable(action.eventParameters[i].value  || '');
            }

            while ((found = regex.exec(value)) !== null) {
                replaced.push({
                    find: '{{ad.' + found[1] + found[2] + '}}',
                    value: '{{ad.' + $scope.findDataSourceByName($scope.selectedDataSources, found[1])._id + found[2] + '}}'
                });
            }

            for (j = 0; j < replaced.length; j += 1) {
                value = value.replace(replaced[j].find, replaced[j].value);
            };

            $scope.task.actionInputFields[eventKey] = value;
        }

        blockUI();

        if ($routeParams.taskId === undefined) {
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
        } else {
            $scope.task.$save(function () {
                var loc, indexOf;

                unblockUI();

                motechAlert('task.success.saved', 'header.saved', function () {
                    loc = new String(window.location);
                    indexOf = loc.indexOf('#');

                    window.location = loc.substring(0, indexOf) + "#/dashboard";
                });
            }, function () {
                delete $scope.task.actionInputFields;
                delete $scope.task.enabled;
                delete $scope.task.additionalData;

                alertHandler('task.error.saved', 'header.error');
            });
        }
    };

    $scope.refactorDivEditable = function (value) {
        var result = $('<div>' + value + '</div>');

        result.find('span[data-prefix]').replaceWith(function() {
            var eventKey = '', source = $(this).data('source'),
                type = $(this).data('object-type'), objectDisplayName = $(this).data('object'),
                prefix = $(this).data('prefix'), field = $(this).data('field'), id = $(this).data('object-id'),
                val;

            if (prefix === 'trigger') {
                for (var i = 0; i < $scope.selectedTrigger.eventParameters.length; i += 1) {
                    if ($scope.msg($scope.selectedTrigger.eventParameters[i].displayName) == $(this).text()) {
                        eventKey = $scope.selectedTrigger.eventParameters[i].eventKey;
                    }
                }
            } else if (prefix === 'ad') {
                eventKey = field;
            }

            var manipulation = this.attributes.getNamedItem('manipulate')!=null ? this.attributes.getNamedItem('manipulate').value : '';
            if (manipulation && manipulation != "" ) {
                if (this.attributes.getNamedItem('data-type').value == 'UNICODE' || this.attributes.getNamedItem('data-type').value == 'TEXTAREA') {
                    var man = manipulation.split(" ");
                    for (var i = 0; i<man.length; i++) {
                        eventKey = eventKey +"?" + man[i];
                    }
                } else if (this.attributes.getNamedItem('data-type').value == 'DATE') {
                    eventKey = eventKey + "?" + manipulation;
                }
            }

            if (prefix === 'trigger') {
                val = '{{' + prefix + '.' + eventKey + '}}';
            } else if (prefix === 'ad') {
                val = '{{' + prefix + '.' + $scope.msg(source) + '.' + type + '#' + id + '.' + eventKey + '}}';
            }

            return val;
        });

        result.find('em').remove();

        if ($.browser.webkit) {
          result.find("div").replaceWith(function() { return "\n" + this.innerHTML; });
        }

        if ($.browser.msie) {
          result.find("p").replaceWith(function() { return this.innerHTML + "<br>"; });
        }

        if ($.browser.mozilla || $.browser.opera || $.browser.msie || $.browser.webkit) {
          result.find("br").replaceWith("\n");
        }

        return result.text();
    }

    $scope.createDraggableElement = function (value) {
        value = value.replace(/{{.*?}}/g, $scope.buildSpan);
        return value;
    }

    $scope.buildSpan = function(eventParameterKey) {
        var key = eventParameterKey.slice(eventParameterKey.indexOf('.') + 1, -2).split("?"),
            prefix = eventParameterKey.slice(2, eventParameterKey.indexOf('.')),
            span = "", param, type, field, cuts, dataSource, dataSourceId, object, id;

        eventParameterKey = key[0];
        key.remove(0);
        var manipulation = key;

        if (prefix === 'trigger') {
            param = $scope.findTriggerEventParameter(eventParameterKey);
            span = '<span ' + (param.type != 'NUMBER' ? 'manipulationpopover' : '') +' contenteditable="false" class="popoverEvent nonEditable badge badge-info triggerField ng-scope ng-binding pointer"' +
                   '" data-prefix="' + prefix + '" data-type="' + param.type + '" style="position: relative;" ' +
                   (manipulation.length == 0 ? "" : 'manipulate="' + manipulation.join(" ") + '"') + '>' + $scope.msg(param.displayName) + '</span>';
        } else if (prefix === 'ad') {
            cuts = eventParameterKey.split('.');

            dataSourceId = cuts[0];
            type = cuts[1].split('#');
            id = type.last();

            cuts.remove(0, 1);
            type.removeObject(id);

            field = cuts.join('.');
            type = type.join('#');

            dataSource = $scope.findDataSourceById($scope.selectedDataSources, dataSourceId);
            object = $scope.findObject(dataSource, type);
            param = $scope.findObjectField(object, field);

            span = '<span ' + (param.type != 'NUMBER' ? 'manipulationpopover' : '') +' contenteditable="false" class="popoverEvent nonEditable badge badge-warning triggerField ng-scope ng-binding pointer" data-type="' + param.type +
                   '" data-prefix="' + prefix + '" data-source="' + dataSource.name + '" data-object="' + param.displayName +'" data-object-type="' + type + '" data-field="' + field +
                   '" data-object-id="' + id + '" style="position: relative;" ' + (manipulation.length == 0 ? "" : 'manipulate="' + manipulation.join(" ") + '"') + '>' +
                   $scope.msg(dataSource.name) + '.' + $scope.msg(object.displayName) + '#' + id + '.' + $scope.msg(param.displayName) + '</span>';
        }

        return span;
    }

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
                if ($scope.BrowserDetect.browser != 'Chrome') {
                    param = $scope.selectedAction.eventParameters[i].value;
                } else {
                    param = $scope.refactorDivEditable($scope.selectedAction.eventParameters[i].value || '');
                }
                if (param === null || param === undefined || param === "\n" || !param.trim().length) {
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
        var msg = "control-group", value = $scope.refactorDivEditable(prop.value || '');

        if (value.length === 0 || value==="\n") {
            msg = msg.concat(' error');
        }

        return msg;
    }

    $scope.addDataSource = function () {
        $scope.selectedDataSources.push({
            '_id': $scope.availableDataSources[0]._id,
            'name': $scope.availableDataSources[0].name,
            'objects': [],
            'available': $scope.availableDataSources[0].objects
        });

        $scope.availableDataSources.remove(0);
        $scope.currentPage.dataSource = $scope.selectedDataSources.length - 1;
    }

    $scope.changeDataSource = function (dataSource, available) {
        var regex = new RegExp('\\{\\{ad\\.' + $scope.msg(dataSource.name) + '\\..*?\\}\\}', "g"),
            spans = 0,
            change = function (ds, a) {
                $scope.availableDataSources.removeObject(a);
                $scope.availableDataSources.push($scope.findDataSourceByName($scope.allDataSources, ds.name));

                ds._id = a._id;
                ds.name = a.name;
                ds.objects = [];
                ds.available = a.objects;
            };

        if ($scope.BrowserDetect.browser != 'Chrome') {
            angular.forEach($scope.selectedAction.eventParameters, function (param, key) {
                var count;

                while ((count = regex.exec(param.value)) !== null) {
                    spans = spans + count.length;
                }
            });
        } else {
            spans = $('.actionField span[data-source="' + dataSource.name + '"]').length;
        }

        if (spans > 0) {
            motechConfirm('task.confirm.changeDataSource', 'header.confirm', function (r) {
                if (r) {
                    if ($scope.BrowserDetect.browser != 'Chrome') {
                        angular.forEach($scope.selectedAction.eventParameters, function (param, key) {
                            if (param.value !== undefined) {
                                param.value = param.value.replace(regex, '');
                            }
                        });
                    } else {
                        $('.actionField span[data-source="' + dataSource.name + '"]').remove();
                        $('.actionField').change();
                    }

                    change(dataSource, available);
                    $scope.$apply();
                }
            });
        } else {
            change(dataSource, available);
        }
    }

    $scope.selectObject = function (dataSourceName, object, selected) {
        var regex = new RegExp('\\{\\{ad\\.' + $scope.msg(dataSourceName) + '\\.' + object.type + '\\#' + object.id + '.*?\\}\\}', "g"),
            spans = 0,
            change = function (obj, sel) {
                obj.displayName = sel.displayName;
                obj.type = sel.type;
                obj.fields = sel.fields;
                obj.lookupFields = sel.lookupFields;
                obj.lookup.field = sel.lookupFields[0];
            };

        if ($scope.BrowserDetect.browser != 'Chrome') {
            angular.forEach($scope.selectedAction.eventParameters, function (param, key) {
                var count;

                while ((count = regex.exec(param.value)) !== null) {
                    spans = spans + count.length;
                }
            });
        } else {
            spans = $('.actionField span[data-source="' + dataSourceName + '"][data-object-type="' + object.type + '"][data-object-id="' + object.id + '"]').length;
        }

        if (spans > 0) {
            motechConfirm('task.confirm.changeDataSource', 'header.confirm', function (r) {
                if (r) {
                    if ($scope.BrowserDetect.browser != 'Chrome') {
                        angular.forEach($scope.selectedAction.eventParameters, function (param, key) {
                            if (param.value !== undefined) {
                                param.value = param.value.replace(regex, '');
                            }
                        });
                    } else {
                        $('.actionField span[data-source="' + dataSourceName + '"][data-object-type="' + object.type + '"][data-object-id="' + object.id + '"]').remove();
                        $('.actionField').change();
                    }

                    change(object, selected);
                    $scope.$apply();
                }
            });
        } else {
            change(object, selected);
        }
    }

    $scope.selectLookup = function (object, lookup) {
        object.lookup.displayName = lookup.displayName;
        object.lookup.by = lookup.eventKey;
    }

    $scope.addObject = function (dataSource) {
        var first = dataSource.available[0],
            last = dataSource.objects.last(),
            parent = $scope;

        dataSource.objects.push({
            id: (last === undefined ? 0 : last.id) + 1,
            displayName: first.displayName,
            type: first.type,
            fields: first.fields,
            lookupFields: first.lookupFields,
            lookup: {
                displayName: $scope.selectedTrigger.eventParameters[0].displayName,
                by: $scope.selectedTrigger.eventParameters[0].eventKey,
                field: first.lookupFields[0]
            }
        });

        $scope.currentPage.dataSourceObject = dataSource.objects.length - 1;

        while (parent.msg === undefined) {
            parent = parent.$parent;
        }

        $('#addObjectNotification').notify({
            message: { text: parent.msg('notification.addObject', parent.msg(dataSource.name)) },
            type: 'blackgloss'
        }).show();
    }

    $scope.findTriggerEventParameter = function (eventKey) {
        var i, found;

        for (i = 0; i < $scope.selectedTrigger.eventParameters.length; i += 1) {
            if ($scope.selectedTrigger.eventParameters[i].eventKey === eventKey) {
                found = $scope.selectedTrigger.eventParameters[i];
                break;
            }
        }

        return found;
    }

    $scope.findDataSourceByName = function (dataSources, name) {
        var found;

        angular.forEach(dataSources, function (ds) {
            if (ds.name === name || $scope.msg(ds.name) === name) {
                found = ds;
            }
        });

        return found;
    }

    $scope.findDataSourceById = function (dataSources, dataSourceId) {
        var found;

        angular.forEach(dataSources, function (ds) {
            if (ds._id === dataSourceId) {
                found = ds;
            }
        });

        return found;
    }

    $scope.findObject = function (dataSource, type, id) {
        var found;

        angular.forEach(dataSource.objects, function (obj) {
            var expression = obj.type === type;

            if (expression && id !== undefined) {
                expression = expression && obj.id === id;
            }

            if (expression) {
                found = obj;
            }
        });

        return found;
    }

    $scope.findObjectField = function (object, field) {
        var found;

        angular.forEach(object.fields, function (f) {
            if (f.fieldKey === field) {
                found = f;
            }
        });

        return found;
    }

    $scope.actionNameCssClass = function(prop) {
        var msg = "control-group";

        if (!prop.name) {
            msg = msg.concat(' error');
        }

        return msg;
     }
}

function LogCtrl($scope, Tasks, Activities, $routeParams, $filter) {

    $scope.resetItemsPagination();
    $scope.filteredItems = [];
    $scope.limitPages = [10, 20, 50, 100];
    $scope.itemsPerPage = $scope.limitPages[0];
    $scope.histories = ['ALL', 'WARNING', 'SUCCESS', 'ERROR'];
    $scope.filterHistory = $scope.histories[0];

    if ($routeParams.taskId != undefined) {
        var data = { taskId: $routeParams.taskId }, task;

        task = Tasks.get(data, function () {
            $scope.activities = Activities.query(data, $scope.search);

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

    $scope.changeItemsPerPage = function(){
        $scope.setCurrentPage(0);
        $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
    }

    $scope.changeFilterHistory = function(){
        $scope.search();
    }

    var searchMatch = function (activity, filterHistory) {
        if (filterHistory == $scope.histories[0]) {
            return true;
        } else {
            return activity.activityType == filterHistory;
        }
    };

    $scope.search = function () {
        $scope.filteredItems = $filter('filter')($scope.activities, function (activity) {
            if (activity) {
                if (searchMatch(activity, $scope.filterHistory))
                    return true;
                }
                return false;
            });

        $scope.setCurrentPage(0);
        $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
    };

}
