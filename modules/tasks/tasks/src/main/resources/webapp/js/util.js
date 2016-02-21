(function () {
    'use strict';

    /* Services */

    angular.module('tasks.utils', []).factory('ManageTaskUtils', function () {
        var utils = {
            TRIGGER_PREFIX: 'trigger',
            DATA_SOURCE_PREFIX: 'ad',
            FILTER_OPERATORS: {
                'task.string': {
                    'type': 'UNICODE',
                    'options': [
                        'task.exist',
                        'task.equals',
                        'task.contains',
                        'task.startsWith',
                        'task.endsWith',
                        'task.equalsIgnoreCase'
                    ]
                },
                'task.number': {
                    'type': 'DOUBLE',
                    'options': [
                        'task.exist',
                        'task.number.equals',
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
            },
            MANIPULATION_SETTINGS: [{
                name: 'join',
                input: 'input[join-update]',
                pattern: 5
            }, {
                name: 'split',
                input: 'input[split-update]',
                pattern: 6
            }, {
                name: 'substring',
                input: 'input[substring-update]',
                pattern: 10
            }, {
                name: 'dateTime',
                input: 'input[date-update]',
                pattern: 9
            }, {
                name: 'plusDays',
                input: 'input[manipulation-kind="plusDays"]',
                pattern: 9
            }, {
                name: 'minusDays',
                input: 'input[manipulation-kind="minusDays"]',
                pattern: 10
            }, {
                name: 'plusHours',
                input: 'input[manipulation-kind="plusHours"]',
                pattern: 10
            }, {
                name: 'minusHours',
                input: 'input[manipulation-kind="minusHours"]',
                pattern: 11
            }, {
                name: 'plusMinutes',
                input: 'input[manipulation-kind="plusMinutes"]',
                pattern: 12
            }, {
                name: 'minusMinutes',
                input: 'input[manipulation-kind="minusMinutes"]',
                pattern: 13
            }, {
                name: 'format',
                input: ''
            }, {
                name: 'capitalize',
                input: ''
            }, {
                name: 'toUpper',
                input: ''
            }, {
                name: 'toLower',
                input: ''
            }, {
                name: 'URLEncode',
                input: ''
            }, {
                name: 'parseDate',
                input: 'input[parsedate-update]',
                pattern: 10
            } ],
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
                        if (channel.providesTriggers) {
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
                        subject: trigger.subject,
                        triggerListenerSubject: trigger.triggerListenerSubject
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
                        scope.$apply();
                    }
                }
            },
            dataSource: {
                select: function (scope, data, selected) {
                    data.providerName = selected.name;
                    data.providerId = selected.id;

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
            isDate2Date: function (value) {
                return value && $.inArray(value, ['DATE2DATE']) !== -1;
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
            isFirefox: function (scope) {
                return $.inArray(scope.BrowserDetect.browser, ['Firefox']) !== -1;
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
                var removeButton, span = $('<span/>');

                span.attr('unselectable', 'on');
                span.attr('contenteditable', 'false');

                if (data.fieldType !== undefined && data.fieldType === 'format') {
                    span.addClass('nonEditable triggerField pointer badge');
                } else {
                    span.addClass('popoverEvent nonEditable triggerField pointer badge');
                }

                if (data.param.type === 'UNKNOWN') {
                    span.addClass('badge-unknown');
                } else {
                    switch (data.prefix) {
                    case this.TRIGGER_PREFIX:
                        span.attr('data-eventkey', data.param.eventKey);
                        span.addClass('badge-info');
                        break;
                    case this.DATA_SOURCE_PREFIX:
                        span.addClass('badge-warning');
                        break;
                    }
                }

                if (this.isText(data.param.type)) {
                    if (data.fieldType !== undefined && data.fieldType === 'format') {
                        span.attr('data-popover', 'no');
                    } else {
                        span.attr('manipulationpopover', 'STRING');
                    }
                } else if (this.isDate(data.param.type)) {
                    if (this.isDate(data.fieldType)) {
                        span.attr('manipulationpopover', 'DATE2DATE');
                    } else {
                        span.attr('manipulationpopover', 'DATE');
                    }
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

                if (data.popover === 'true') {
                    span.attr('data-popover', 'no');
                }

                span.css('position', 'relative');

                switch (data.prefix) {
                case this.TRIGGER_PREFIX:
                    if(data.msg) {
                        span.text(data.msg(data.param.displayName));
                    } else {
                        span.text(data.param.displayName);
                    }
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

                if (span.attr('manipulationpopover') === undefined) {
                    span.attr('manipulationpopover', 'NONE');
                }
                span.append(" &nbsp;");
                span.append(" &nbsp;");
                removeButton = $('<button/>', {
                        text: 'x',
                        type: 'button'
                });
                removeButton.addClass('close');
                removeButton.addClass('badge-close');
                span.append(removeButton);

                return $('<div/>').append(span).html();
            },
            createErrorMessage: function (scope, response, warning) {
                var msg;
                if (warning) {
                   msg = scope.msg('task.success.savedWithWarning') + '\n';
                } else {
                   msg = scope.msg('task.error.enabled') + '\n';
                }

                if(jQuery.type(response) === "array") {
                    angular.forEach(response, function (r) {
                        msg += ' - ' + scope.msg(r.message, r.args) + '\n';
                    });
                } else {
                   msg += ' - ' + response + '\n';
                }

                return msg;
            },
            convertToView: function (scope, type, value) {
                var val = value || '';

                if (this.isBoolean(type) && (val === 'true' || val === 'false')) {
                    val = val === 'true';
                    val = this.createBooleanSpan(scope, val);
                }

                val = scope.createDraggableElement(val, type, 'convert');

                return val;
            },
            convertToServer: function (scope, value) {
                var val = value || '',
                    regex = new RegExp('\\{\\{ad\\.(.+?(\\.name)?)(\\..*?)\\}\\}', "g"),
                    replaced = [],
                    found,
                    ds;

                val = scope.refactorDivEditable(val);

                while ( (found = regex.exec(val)) !== null )  {
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
                        BootstrapDialog.alert({
                            type: BootstrapDialog.TYPE_DANGER,
                            message: 'Data source cannot be resolved'
                        });
                    }

                    replaced.push({
                        find: '{{ad.{0}{1}}}'.format(found[1], found[3]),
                        value: '{{ad.{0}{1}}}'.format(ds.providerId, found[3])
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
        utils.formatField = function (field) {
            if(!field){
                return "";
            }
            var str = "";
            switch(field.prefix){
                case utils.TRIGGER_PREFIX:
                    str = "{0}.{1}".format(utils.TRIGGER_PREFIX, field.eventKey);
                    break;
                case utils.DATA_SOURCE_PREFIX:
                    str = "{0}.{1}.{2}#{3}.{4}".format(utils.DATA_SOURCE_PREFIX, field.providerId, field.providerType, field.objectId, field.fieldKey);
                    break;
                default:
                    str = field.displayName;
            }
            if (field.manipulations && Array.isArray(field.manipulations)) {
                field.manipulations.forEach(function(manipulation) {
                    str += "?{0}({1})".format(manipulation.type, manipulation.argument);
                });
            }
            return str;
        };
        utils.parseField = function (str, existingFields) {
            var manipulations, field;
            if(!str){
                return false;
            }
            if(!existingFields || !Array.isArray(existingFields)){
                existingFields=[];
            }
            manipulations = str.split('?');
            str = manipulations.shift();
            field = {};
            field.displayName = str;
            existingFields.forEach(function (exField) {
                if(str === utils.formatField(exField)){
                    field = Object.assign({}, exField);
                }
            });
            field.manipulations = [];
            manipulations.forEach(function (manipulationStr) {
                var manipulation = {},
                parts = manipulationStr.split('(');
                manipulation.type = parts.shift();
                if(parts.length>0) {
                    manipulation.argument = parts[0].replace(')','');
                }
                field.manipulations.push(manipulation);
            });
            return field;
        };
        return utils;
    });

}());
