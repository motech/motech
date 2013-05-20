(function () {
    'use strict';

    /* ManageTaskUtils tests */

    describe("ManageTaskUtils", function() {
        var scope, utils;

        beforeEach(module('manageTaskUtils'));

        beforeEach(inject(function($rootScope, $injector) {
            scope = $rootScope.$new();
            utils = $injector.get('ManageTaskUtils');
        }));

        it('Should contain constants variables', function () {
            expect(utils.FILTER_SET_PATH).toEqual('../tasks/partials/widgets/filter-set.html');
            expect(utils.FILTER_SET_ID).toEqual('#filter-set');
            expect(utils.DATA_SOURCE_PATH).toEqual('../tasks/partials/widgets/data-source.html');
            expect(utils.DATA_SOURCE_PREFIX_ID).toEqual('#data-source-');
            expect(utils.BUILD_AREA_ID).toEqual('#build-area');
            expect(utils.TRIGGER_PREFIX).toEqual('trigger');
            expect(utils.DATA_SOURCE_PREFIX).toEqual('ad');
        });

        it('Should find object in list when criteria are object', function () {
            var expected = { number: 2 },
                list = [ { number: 1 }, expected ],
                by = { what: 'number', equalTo: 2 },
                data = { where: list, by: by };

            expect(utils.find(data)).toEqual(expected);

            data.msg = function (key) { return key.toString(); };

            expect(utils.find(data)).toEqual(expected);

            data.by.equalTo = '2';
            expected.number = '2';

            expect(utils.find(data)).toEqual(expected);
        });

        it('Should find object in list when criteria are array', function () {
            var expected = { number: 2, text: 'abc' },
                list = [ { number: 1, text: 'def'}, expected ],
                by = [ { what: 'text', equalTo: 'abc' }, { what: 'number', equalTo: 2 } ],
                data = { where: list, by: by };

            expect(utils.find(data)).toEqual(expected);

            data.msg = function (key) { return key.toUpperCase(); };

            expect(utils.find(data)).toEqual(expected);

            data.by[0].equalTo = 'ABC';

            expect(utils.find(data)).toEqual(expected);
        });

        it('Should not find object in list when criteria are invalid', function () {
            var list = [ { number: 1 }, { number: 2 } ],
                data = { where: list };

            expect(utils.find(null)).toEqual(undefined);
            expect(utils.find(undefined)).toEqual(undefined);
            expect(utils.find({})).toEqual(undefined);
            expect(utils.find(data)).toEqual(undefined);

            data.by = { };

            expect(utils.find(data)).toEqual(undefined);

            data.by = { what: 'number' };

            expect(utils.find(data)).toEqual(undefined);

            data.by = { equalTo: 1 };

            expect(utils.find(data)).toEqual(undefined);
        });

        it('Should return array with trigger channels', function () {
            var expected = { triggerTaskEvents: [ { 'displayName': 'test' } ] },
                channels = [ { }, { triggerTaskEvents: [] } ];


            expect(utils.channels.withTriggers(null)).toEqual([]);
            expect(utils.channels.withTriggers(undefined)).toEqual([]);
            expect(utils.channels.withTriggers([])).toEqual([]);
            expect(utils.channels.withTriggers(channels)).toEqual([]);

            channels[0] = expected;

            expect(utils.channels.withTriggers(channels)).toEqual([expected]);
        });

        it('Should return array with action channels', function () {
            var expected = { actionTaskEvents: [ { 'displayName': 'test' } ] },
                channels = [ { }, { actionTaskEvents: [] } ];


            expect(utils.channels.withActions(null)).toEqual([]);
            expect(utils.channels.withActions(undefined)).toEqual([]);
            expect(utils.channels.withActions([])).toEqual([]);
            expect(utils.channels.withActions(channels)).toEqual([]);

            channels[0] = expected;

            expect(utils.channels.withActions(channels)).toEqual([expected]);
        });

        it('Should select trigger', function () {
            var channel = { displayName: 'displayName', moduleName: 'moduleName', moduleVersion: '0.10.0' },
                trigger = { displayName: 'triggerDisplayName', subject: 'subject' },
                triggerInfo = {
                    displayName: 'triggerDisplayName',
                    channelName: 'displayName',
                    moduleName: 'moduleName',
                    moduleVersion: '0.10.0',
                    subject: 'subject'
                };

            utils.trigger.select(scope, channel, trigger);

            expect(scope.task).not.toEqual(undefined);

            expect(scope.task.trigger).toEqual(triggerInfo);
            expect(scope.selectedTrigger).toEqual(trigger);
        });

        it('Should remove trigger', function () {
            scope.task = {
                trigger: {
                    displayName: 'displayName'
                }
            };

            utils.trigger.remove(scope);

            expect(scope.task).not.toEqual(undefined);
            expect(scope.task.trigger).toEqual(undefined);
        });

        it('Should select action', function () {
            var action = {
                    displayName: 'actionDisplayName'
                },
                actionInfo = {
                    displayName: 'actionDisplayName',
                    channelName: 'displayName',
                    moduleName: 'moduleName',
                    moduleVersion: '0.10.0'
                };

            scope.selectedActionChannel = {
                displayName: 'displayName',
                moduleName: 'moduleName',
                moduleVersion: '0.10.0'
            };

            utils.action.select(scope, action);

            expect(scope.task).not.toEqual(undefined);

            expect(scope.task.action).toEqual(actionInfo);
            expect(scope.selectedAction).toEqual(action);

            action.subject = 'subject';
            actionInfo.subject = 'subject';

            utils.action.select(scope, action);

            expect(scope.task).not.toEqual(undefined);

            expect(scope.task.action).toEqual(actionInfo);
            expect(scope.selectedAction).toEqual(action);

            action.serviceInterface = 'serviceInterface';

            utils.action.select(scope, action);

            expect(scope.task).not.toEqual(undefined);

            expect(scope.task.action).toEqual(actionInfo);
            expect(scope.selectedAction).toEqual(action);

            action.serviceMethod = 'serviceMethod';
            actionInfo.serviceInterface = 'serviceInterface';
            actionInfo.serviceMethod = 'serviceMethod';

            utils.action.select(scope, action);

            expect(scope.task).not.toEqual(undefined);

            expect(scope.task.action).toEqual(actionInfo);
            expect(scope.selectedAction).toEqual(action);
        });

        it('Should select data source', function () {
            var data = {
                    dataSourceName: 'abc',
                    dataSourceId: '123',
                    displayName: 'displayName',
                    type: 'type',
                    lookup: {
                        field: 'id',
                        value: 'true'
                    }
                },
                selected = {
                    dataSourceName: 'def',
                    dataSourceId: '456'
                };

            utils.dataSource.select(scope, data, selected);

            expect(data.dataSourceName).toEqual(selected.name);
            expect(data.dataSourceId).toEqual(selected._id);

            expect(data.displayName).toEqual(undefined);
            expect(data.type).toEqual(undefined);
            expect(data.lookup).toEqual(undefined);
        });

        it('Should select data source object', function () {
            var data = {
                    displayName: 'displayName',
                    type: 'type'
                },
                selected = {
                    displayName: 'selectedDisplayName',
                    type: 'selectedType'
                };

            utils.dataSource.selectObject(scope, data, selected);

            expect(data.displayName).toEqual(selected.displayName);
            expect(data.type).toEqual(selected.type);
        });

        it('Should pass isXXX', function () {
            expect(utils.isText('UNICODE')).toEqual(true);
            expect(utils.isText('TEXTAREA')).toEqual(true);
            expect(utils.isText('INTEGER')).toEqual(false);


            expect(utils.isNumber('INTEGER')).toEqual(true);
            expect(utils.isNumber('LONG')).toEqual(true);
            expect(utils.isNumber('DOUBLE')).toEqual(true);
            expect(utils.isNumber('UNICODE')).toEqual(false);

            expect(utils.isDate('DATE')).toEqual(true);
            expect(utils.isDate('UNICODE')).toEqual(false);

            expect(utils.isBoolean('BOOLEAN')).toEqual(true);
            expect(utils.isBoolean('UNICODE')).toEqual(false);

            scope.BrowserDetect = { browser: 'Chrome' };
            expect(utils.isChrome(scope)).toEqual(true);

            scope.BrowserDetect = { browser: 'Explorer' };
            expect(utils.isChrome(scope)).toEqual(false);

            scope.BrowserDetect = { browser: 'Explorer' };
            expect(utils.isIE(scope)).toEqual(true);

            scope.BrowserDetect = { browser: 'Chrome' };
            expect(utils.isIE(scope)).toEqual(false);
        });

        it('Should can handle modern drag and drop', function () {
            scope.BrowserDetect = { browser: 'Chrome' };
            expect(utils.canHandleModernDragAndDrop(scope)).toEqual(true);

            scope.BrowserDetect = { browser: 'Explorer' };
            expect(utils.canHandleModernDragAndDrop(scope)).toEqual(true);
        });

        it('Should can not handle modern drag and drop', function () {
            scope.BrowserDetect = { browser: 'FireFox' };
            expect(utils.canHandleModernDragAndDrop(scope)).toEqual(false);
        });

        it('Should create boolean span', function () {
            var yes = '<span contenteditable="false" data-value="true" data-prefix="other" class="badge badge-success">Yes</span>',
                no = '<span contenteditable="false" data-value="false" data-prefix="other" class="badge badge-important">No</span>';

            scope.msg = function (key) {
                return key.charAt(0).toUpperCase() + key.slice(1);
            }

            expect(utils.createBooleanSpan(scope, true)).toEqual(yes);
            expect(utils.createBooleanSpan(scope, false)).toEqual(no);
        });

        it('Should create draggable span', function () {
            var data = {
                    prefix: 'other',
                    param: {
                        type: 'UNKNOWN',
                        displayName: 'displayName'
                    }
                },
                spanOther = '<span unselectable="on" contenteditable="false" style="position: relative;" class="popoverEvent nonEditable triggerField pointer badge badge-important" data-prefix="other" data-type="UNKNOWN" data-object="displayName">displayName</span>',
                spanTrigger = '<span unselectable="on" contenteditable="false" style="position: relative;" class="popoverEvent nonEditable triggerField pointer badge badge-info" manipulationpopover="" manipulate="toLower capitalize" data-prefix="trigger" data-type="UNICODE" data-object="displayName">displayname</span>',
                spanDataSource = '<span unselectable="on" contenteditable="false" style="position: relative;" class="popoverEvent nonEditable triggerField pointer badge badge-warning" manipulationpopover="" manipulate="toLower capitalize" data-prefix="ad" data-type="DATE" data-object="displayName" data-source="DS" data-object-id="0" data-object-type="TestType" data-field="field">ds.object#0.displayname</span>';

            expect(utils.createDraggableSpan(data)).toEqual(spanOther);

            data.prefix = 'trigger';
            data.param.type = 'UNICODE';
            data.manipulations = ['toLower', 'capitalize'];
            data.msg = function (key) { return key.toLowerCase(); };

            expect(utils.createDraggableSpan(data)).toEqual(spanTrigger);

            data.prefix = 'ad';
            data.param.type = 'DATE';
            data.dataSourceName = 'DS';
            data.object = {
                id: 0,
                displayName: 'object',
                type: 'TestType',
                field: 'field'
            };

            expect(utils.createDraggableSpan(data)).toEqual(spanDataSource);
        });

        it('Should create error message', function () {
            var response = [ { message: '{0}', args: [ 'abc' ] }, { message: '{0}-{1}', args: [ 'def', '1234' ] } ],
                msg = 'task.error.saved\n - abc\n - def-1234\n';

            scope.msg = function (key, args) {
                if (args === undefined) {
                    return key;
                } else if (args.length === 1) {
                    return key.format(args[0]);
                } else if (args.length === 2) {
                    return key.format(args[0], args[1]);
                }
            };

            expect(utils.createErrorMessage(scope, response)).toEqual(msg);
        });

        it('Should convert value to proper view value', function () {
            scope.BrowserDetect = { browser: 'Chrome' };

            scope.createDraggableElement = function (val) {
                return '<div>' + val + '</div>';
            };

            scope.msg = function (key) {
                return key.charAt(0).toUpperCase() + key.slice(1);
            }

            expect(utils.convertToView(scope, 'UNICODE', '{{trigger.value}}')).toEqual('<div>{{trigger.value}}</div>');
            expect(utils.convertToView(scope, 'BOOLEAN', 'false')).toEqual('<div><span contenteditable="false" data-value="false" data-prefix="other" class="badge badge-important">No</span></div>');
            expect(utils.convertToView(scope, 'BOOLEAN', '{{trigger.value}}')).toEqual('<div>{{trigger.value}}</div>');

            scope.BrowserDetect.browser = 'FireFox';
            scope.selectedDataSources = [{
                dataSourceId: 'a234',
                dataSourceName: 'abc'
            }];

            expect(utils.convertToView(scope, 'UNICODE', '{{ad.a234.obj#1.field}} abc')).toEqual('{{ad.Abc.obj#1.field}} abc');
            expect(utils.convertToView(scope, 'NUMBER', '{{trigger.value}}')).toEqual('{{trigger.value}}');
        });

        it('Should convert value to proper server value', function () {
            scope.BrowserDetect = { browser: 'Chrome' };

            scope.refactorDivEditable = function (val) {
                return $(val).text();
            };

            scope.msg = function (key) {
                return key.charAt(0).toUpperCase() + key.slice(1);
            };

            scope.selectedDataSources = [{
                dataSourceId: 'a234',
                dataSourceName: 'abc'
            }];

            expect(utils.convertToServer(scope, '<div>{{trigger.value}}</div>')).toEqual('{{trigger.value}}');
            expect(utils.convertToServer(scope, '<div>{{ad.Abc.obj#1.field}} abc</div>')).toEqual('{{ad.a234.obj#1.field}} abc');

            scope.BrowserDetect.browser = 'FireFox';

            expect(utils.convertToServer(scope, '{{ad.Abc.obj#1.field}} abc')).toEqual('{{ad.a234.obj#1.field}} abc');
            expect(utils.convertToServer(scope, '{{trigger.value}}')).toEqual('{{trigger.value}}');
        });

    });

}());