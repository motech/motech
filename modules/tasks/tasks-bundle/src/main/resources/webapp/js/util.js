(function () {
    'use strict';

    /* Services */

    angular.module('manageTaskUtils', []).factory('ManageTaskUtils', function () {
        return {
            TRIGGER_PREFIX: 'trigger',
            DATA_SOURCE_PREFIX: 'ad',
            find: function (data) {
                var where = (data && data.where) || [],
                    unique = (data && data.unique === false) ? false : true,
                    found = [],
                    by = [],
                    isTrue,
                    item,
                    i,
                    j;

                if (data && data.by) {
                    if (data.by.isArray) {
                        by = data.by;
                    } else if (data.by.what && data.by.equalTo) {
                        by = [data.by];
                    }
                }

                for (i = 0; i < where.length; i += 1) {
                    isTrue = (by.length > 0) ? true : false;
                    item = where[i];

                    for (j = 0; j < by.length; j += 1) {
                        if (data.msg !== undefined) {
                            isTrue = isTrue && (item[by[j].what] === by[j].equalTo || data.msg(item[by[j].what]) === by[j].equalTo);
                        } else {
                            isTrue = isTrue && item[by[j].what] === by[j].equalTo;
                        }
                    }

                    if (isTrue) {
                        found.push(item);
                    }
                }

                return unique ? found[0] : found;
            },
            channels: {
                withTriggers: function (channels) {
                    var array = [];

                    angular.forEach(channels, function (channel) {
                        if (channel.triggerTaskEvents && channel.triggerTaskEvents.length) {
                            array.push(channel);
                        }
                    });

                    return array;
                },
                withActions: function (channels) {
                    var array = [];

                    angular.forEach(channels, function (channel) {
                        if (channel.actionTaskEvents && channel.actionTaskEvents.length) {
                            array.push(channel);
                        }
                    });

                    return array;
                }
            },
            trigger: {
                select: function (scope, channel, trigger) {
                    if (!scope.task) {
                        scope.task = {};
                    }

                    scope.task.trigger = {
                        displayName: trigger.displayName,
                        channelName: channel.displayName,
                        moduleName: channel.moduleName,
                        moduleVersion: channel.moduleVersion,
                        subject: trigger.subject
                    };

                    angular.element("#trigger-" + channel.moduleName).parent('li').addClass('selectedTrigger').addClass('active');

                    if (angular.element("#collapse-trigger").collapse) {
                        angular.element("#collapse-trigger").collapse('hide');
                    }

                    scope.selectedTrigger = trigger;

                    if (!scope.$$phase) {
                        scope.$apply();
                    }
                },
                remove: function (scope) {
                    var li = angular.element("#trigger-" + scope.task.trigger.moduleName).parent('li');

                    li.removeClass('selectedTrigger');
                    li.removeClass("active");

                    delete scope.task.trigger;

                    if (!scope.$$phase) {
                        scope.$apply();
                    }
                }
            },
            action: {
                select: function (scope, idx, action) {
                    scope.task.actions[idx] = {
                        displayName: action.displayName,
                        channelName: scope.selectedActionChannel[idx].displayName,
                        moduleName: scope.selectedActionChannel[idx].moduleName,
                        moduleVersion: scope.selectedActionChannel[idx].moduleVersion
                    };

                    if (action.subject) {
                        scope.task.actions[idx].subject = action.subject;
                    }

                    if (action.serviceInterface && action.serviceMethod) {
                        scope.task.actions[idx].serviceInterface = action.serviceInterface;
                        scope.task.actions[idx].serviceMethod = action.serviceMethod;
                    }

                    scope.selectedAction[idx] = cloneObj(action);

                    if (!scope.$$phase) {
                        scope.$eval();
                    }
                }
            },
            dataSource: {
                select: function (scope, data, selected) {
                    data.providerName = selected.name;
                    data.providerId = selected._id;

                    delete data.displayName;
                    delete data.type;
                    delete data.lookup;
                    delete data.failIfDataNotFound;

                    if (!scope.$$phase) {
                        scope.$apply(data);
                    }
                },
                selectObject: function (scope, data, selected) {
                    data.displayName = selected.displayName;
                    data.type = selected.type;

                    delete data.lookup;
                    delete data.failIfDataNotFound;

                    if (!scope.$$phase) {
                        scope.$apply(data);
                    }
                }
            },
            isText: function (value) {
                return value && $.inArray(value, ['UNICODE', 'TEXTAREA']) !== -1;
            },
            isNumber: function (value) {
                return value && $.inArray(value, ['INTEGER', 'LONG', 'DOUBLE']) !== -1;
            },
            isDate: function (value) {
                return value && $.inArray(value, ['DATE']) !== -1;
            },
            isBoolean: function (value) {
                return value && $.inArray(value, ['BOOLEAN']) !== -1;
            },
            isChrome: function (scope) {
                return $.inArray(scope.BrowserDetect.browser, ['Chrome']) !== -1;
            },
            isIE: function (scope) {
                return $.inArray(scope.BrowserDetect.browser, ['Explorer']) !== -1;
            },
            canHandleModernDragAndDrop: function (scope) {
                return this.isChrome(scope) || this.isIE(scope);
            },
            needExpression: function (param) {
                return param && $.inArray(param, ['task.exist', 'task.afterNow', 'task.beforeNow']) === -1;
            },
            createBooleanSpan: function (scope, value) {
                var badgeType = (value ? 'success' : 'important'),
                    msg = (value ? scope.msg('yes') : scope.msg('no')),
                    span = $('<span/>');

                span.attr('contenteditable', 'false');
                span.attr('data-value', value);
                span.attr('data-prefix', 'other');
                span.addClass('badge badge-' + badgeType);
                span.text(msg);

                return $('<div/>').append(span).html();
            },
            createDraggableSpan: function (data) {
                var span = $('<span/>');

                span.attr('unselectable', 'on');
                span.attr('contenteditable', 'false');

                span.css('position', 'relative');

                span.addClass('popoverEvent nonEditable triggerField pointer badge');

                if (data.param.type === 'UNKNOWN') {
                    span.addClass('badge-unknown');
                } else {
                    switch (data.prefix) {
                    case this.TRIGGER_PREFIX:
                        span.addClass('badge-info');
                        break;
                    case this.DATA_SOURCE_PREFIX:
                        span.addClass('badge-warning');
                        break;
                    }
                }

                if (this.isText(data.param.type) || this.isDate(data.param.type)) {
                    span.attr('manipulationpopover', '');
                }

                if (data.manipulations && data.manipulations.length > 0) {
                    span.attr('manipulate', data.manipulations.join(" "));
                }

                span.attr('data-prefix', data.prefix);
                span.attr('data-type', data.param.type);
                span.attr('data-object', data.param.displayName);

                if (data.providerName) {
                    span.attr('data-source', data.providerName);
                }

                if (data.object) {
                    span.attr('data-object-id', data.object.id);
                    span.attr('data-object-type', data.object.type);
                    span.attr('data-field', data.object.field);
                }

                switch (data.prefix) {
                case this.TRIGGER_PREFIX:
                    span.text(data.msg(data.param.displayName));
                    break;
                case this.DATA_SOURCE_PREFIX:
                    span.text("{0}.{1}#{2}.{3}".format(
                        data.msg(data.providerName),
                        data.msg(data.object.displayName),
                        data.object.id,
                        data.msg(data.param.displayName)
                    ));
                    break;
                default:
                    span.text(data.param.displayName);
                }

                return $('<div/>').append(span).html();
            },
            createErrorMessage: function (scope, response) {
                var msg = scope.msg('task.error.saved') + '\n';

                angular.forEach(response, function (r) {
                    msg += ' - ' + scope.msg(r.message, r.args) + '\n';
                });

                return msg;
            },
            getFilterOperators: function () {
                return {
                    'task.string': {
                        'type': 'UNICODE',
                        'options': [
                            'task.exist',
                            'task.equals',
                            'task.contains',
                            'task.startsWith',
                            'task.endsWith'
                        ]
                    },
                    'task.number': {
                        'type': 'DOUBLE',
                        'options': [
                            'task.exist',
                            'task.equal',
                            'task.gt',
                            'task.lt'
                        ]
                    },
                    'task.date': {
                        'type': 'DATE',
                        'options': [
                            'task.exist',
                            'task.equals',
                            'task.after',
                            'task.afterNow',
                            'task.before',
                            'task.beforeNow',
                            'task.lessDaysFromNow',
                            'task.moreDaysFromNow'
                        ]
                    }
                };
            },
            convertToView: function (scope, type, value) {
                var regex = new RegExp('\\{\\{ad\\.(.+?)(\\..*?)\\}\\}', "g"),
                    val = value || '',
                    replaced = [],
                    found,
                    ds;

                if (this.canHandleModernDragAndDrop(scope)) {
                    if (this.isBoolean(type) && (val === 'true' || val === 'false')) {
                        val = val === 'true';
                        val = this.createBooleanSpan(scope, val);
                    }

                    val = scope.createDraggableElement(val);
                } else {
                    while ((found = regex.exec(val)) !== null) {
                        ds = this.find({
                            where: scope.task.taskConfig.steps,
                            by: [{
                                what: '@type',
                                equalTo: 'DataSource'
                            }, {
                                what: 'providerId',
                                equalTo: found[1]
                            }]
                        });

                        replaced.push({
                            find: '{{ad.{0}{1}}}'.format(found[1], found[2]),
                            value: '{{ad.{0}{1}}}'.format(scope.msg(ds.providerName), found[2])
                        });
                    }

                    angular.forEach(replaced, function (r) {
                        val = val.replace(r.find, r.value);
                    });
                }

                return val;
            },
            convertToServer: function (scope, value) {
                var val = value || '',
                    regex = new RegExp('\\{\\{ad\\.(.+?)(\\..*?)\\}\\}', "g"),
                    replaced = [],
                    found,
                    ds;

                if (this.canHandleModernDragAndDrop(scope)) {
                    val = scope.refactorDivEditable(val);
                }

                while ((found = regex.exec(val)) !== null) {
                    ds = this.find({
                        msg: scope.msg,
                        where: scope.task.taskConfig.steps,
                        by: [{
                            what: '@type',
                            equalTo: 'DataSource'
                        }, {
                            what: 'providerName',
                            equalTo: found[1]
                        }]
                    });

                    if (ds === undefined) {
                        jAlert('Data source cannot be resolved', 'Error');
                    }

                    replaced.push({
                        find: '{{ad.{0}{1}}}'.format(found[1], found[2]),
                        value: '{{ad.{0}{1}}}'.format(ds.providerId, found[2])
                    });
                }

                angular.forEach(replaced, function (item) {
                    val = val.replace(item.find, item.value);
                });

                return val;
            },
            doQuery: function (q, resource) {
                var defer = q.defer(), result;

                result = resource.query(function() {
                    defer.resolve(result);
                });

                return defer.promise;
            }
        };
    });

}());
