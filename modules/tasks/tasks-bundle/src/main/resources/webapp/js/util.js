(function () {
    'use strict';

    /* Services */

    angular.module('manageTaskUtils', []).factory('ManageTaskUtils', function () {
        return {
            FILTER_SET_PATH: '../tasks/partials/widgets/filter-set.html',
            FILTER_SET_ID: '#filter-set',
            DATA_SOURCE_PATH: '../tasks/partials/widgets/data-source.html',
            DATA_SOURCE_PREFIX_ID: '#data-source-',
            BUILD_AREA_ID: "#build-area",
            TRIGGER_PREFIX: 'trigger',
            DATA_SOURCE_PREFIX: 'ad',
            find: function (data) {
                var where = (data && data.where) || [],
                    found;

                angular.forEach(where, function (item) {
                    var isTrue = true;

                    if (!found) {
                        if (data.by && data.by.isArray) {
                            angular.forEach(data.by, function (b) {
                                if (data.msg !== undefined) {
                                    isTrue = isTrue && (item[b.what] === b.equalTo || data.msg(item[b.what]) === b.equalTo);
                                } else {
                                    isTrue = isTrue && item[b.what] === b.equalTo;
                                }
                            });
                        } else if (data.by !== undefined && data.by.what !== undefined) {
                            if (data.msg !== undefined) {
                                isTrue = item[data.by.what] === data.by.equalTo || data.msg(item[data.by.what]) === data.by.equalTo;
                            } else {
                                isTrue = item[data.by.what] === data.by.equalTo;
                            }
                        } else {
                            isTrue = false;
                        }

                        if (isTrue) {
                            found = item;
                        }
                    }
                });

                return found;
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
                select: function (scope, action) {
                    if (!scope.task) {
                        scope.task = {};
                    }

                    scope.task.action = {
                        displayName: action.displayName,
                        channelName: scope.selectedActionChannel.displayName,
                        moduleName: scope.selectedActionChannel.moduleName,
                        moduleVersion: scope.selectedActionChannel.moduleVersion
                    };

                    if (action.subject) {
                        scope.task.action.subject = action.subject;
                    }

                    if (action.serviceInterface && action.serviceMethod) {
                        scope.task.action.serviceInterface = action.serviceInterface;
                        scope.task.action.serviceMethod = action.serviceMethod;
                    }

                    scope.selectedAction = action;

                    if (!scope.$$phase) {
                        scope.$apply();
                    }
                }
            },
            dataSource: {
                select: function (scope, data, selected) {
                    data.dataSourceName = selected.name;
                    data.dataSourceId = selected._id;

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
                    span.addClass('badge-important');
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

                if (data.dataSourceName) {
                    span.attr('data-source', data.dataSourceName);
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
                        data.msg(data.dataSourceName),
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
                            where: scope.selectedDataSources,
                            by: {
                                what: 'dataSourceId',
                                equalTo: found[1]
                            }
                        });

                        replaced.push({
                            find: '{{ad.{0}{1}}}'.format(found[1], found[2]),
                            value: '{{ad.{0}{1}}}'.format(scope.msg(ds.dataSourceName), found[2])
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
                        where: scope.selectedDataSources,
                        by: {
                            what: 'dataSourceName',
                            equalTo: found[1]
                        }
                    });

                    replaced.push({
                        find: '{{ad.{0}{1}}}'.format(found[1], found[2]),
                        value: '{{ad.{0}{1}}}'.format(ds.dataSourceId, found[2])
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
