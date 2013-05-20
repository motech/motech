(function () {
    'use strict';

    /* ManageTaskCtrl tests */

    describe('ManageTaskCtrl', function () {
        var $httpBackend, scope, channels, dataSources;

        beforeEach(module('motech-tasks'));

        beforeEach(function() {
            this.addMatchers({
                toEqualData: function(expected) {
                    return angular.equals(this.actual, expected);
                }
            });

            this.httpCall = function() {
                expect(scope.channels).toEqual(undefined);
                expect(scope.dataSources).toEqual(undefined);
                $httpBackend.flush();
            };
        });

        beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
            channels = [{
                actionTaskEvents: [
                    {
                        displayName: 'Action 1',
                        subject: 'Action_1',
                        actionParameters: [
                        ]
                    }
                ],
                displayName: 'test-action',
                moduleName: 'test-action',
                moduleVersion: '0.20'
            }, {
                triggerTaskEvents: [
                    {
                        displayName: 'Trigger 1',
                        subject: 'TRIGGER_1',
                        eventParameters: [
                            {
                                displayName: 'Key 1',
                                type: 'UNICODE',
                                eventKey: 'key_1'
                            },
                            {
                                displayName: 'Key 2',
                                type: 'UNICODE',
                                eventKey: 'key_2'
                            }
                        ]
                    }
                ],
                displayName: 'test-trigger',
                moduleName: 'test-trigger',
                moduleVersion: '0.20'
            }];

            dataSources = [{
                name: 'ds-1',
                _id: '123',
                objects: [{
                    displayName: 'obj.1',
                    type: 'TestType1',
                    lookupFields: [ 'lookup1' ],
                    fields: [{
                        displayName: 'fieldDisplayName1',
                        fieldKey: 'filedKey1',
                        type: 'UNICODE'
                    }]
                }]
            }, {
                name: 'ds-2',
                _id: '456',
                objects: [{
                    displayName: 'obj.2',
                    type: 'TestType2',
                    lookupFields: [ 'lookup2' ],
                    fields: [{
                        displayName: 'fieldDisplayName2',
                        fieldKey: 'fieldKey2',
                        type: 'DATE'
                    }]
                }]
            }];

            $httpBackend = _$httpBackend_;
            $httpBackend.expectGET('../tasks/api/channel').respond(channels);
            $httpBackend.expectGET('../tasks/api/datasource').respond(dataSources);

            scope = $rootScope.$new();
            $controller('ManageTaskCtrl', { $scope: scope });

            this.httpCall();
        }));

        it('Should fetch channels and data sources', function () {
            expect(scope.task).not.toEqual(undefined);
            expect(scope.channels).toEqualData(channels);
            expect(scope.dataSources).toEqualData(dataSources);
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

            scope.selectTrigger(channel, trigger);

            expect(scope.task.trigger).toEqual(triggerInfo);

            channel.moduleName = 'test-module';
            triggerInfo.moduleName = channel.moduleName;

            scope.selectTrigger(channel, trigger);

            expect(angular.element('#popup_message').text()).toEqual('[task.confirm.trigger]');
            angular.element('#popup_ok').click();

            expect(scope.task.trigger).toEqual(triggerInfo);
        });

        it('Should remove trigger', function () {
            var event = {
                stopPropagation: function() { }
            };

            scope.task = {
                trigger: {
                    displayName: 'triggerDisplayName',
                    channelName: 'displayName',
                    moduleName: 'moduleName',
                    moduleVersion: '0.10.0',
                    subject: 'subject'
                }
            };

            scope.removeTrigger(event);

            expect(angular.element('#popup_message').text()).toEqual('[task.confirm.trigger]');
            angular.element('#popup_ok').click();

            expect(scope.task.trigger).toEqual(undefined);
        });

        it('Should add action', function () {
            scope.addAction();

            expect(scope.task).not.toEqual(undefined);
            expect(scope.task.action).toEqual({});
        });

        it('Should remove action', function () {
            scope.removeAction();

            expect(angular.element('#popup_message').text()).toEqual('[task.confirm.action]');
            angular.element('#popup_ok').click();

            expect(scope.task.action).toEqual(undefined);
        });

        it('Should select action channel', function () {
            var channel = { displayName: 'displayName', moduleName: 'moduleName', moduleVersion: '0.10.0' };

            scope.selectActionChannel(channel);

            expect(scope.selectedActionChannel).toEqual(channel);

            channel.moduleName = 'test-module';

            scope.task = {
                action: {
                    displayName: 'task action'
                }
            };

            scope.selectedAction = {
                displayName: 'selected action'
            };

            scope.selectActionChannel(channel);

            expect(angular.element('#popup_message').text()).toEqual('[task.confirm.action]');
            angular.element('#popup_ok').click();

            expect(scope.selectedActionChannel).toEqual(channel);
            expect(scope.task.action).toEqual({});
            expect(scope.selectedAction).toEqual(undefined);
        });

        it('Should get available actions', function () {
            expect(scope.getActions()).toEqual([]);

            scope.selectedActionChannel = channels[1];

            expect(scope.getActions()).toEqual([]);

            scope.selectedActionChannel = channels[0];

            expect(scope.getActions()).toEqual(channels[0].actionTaskEvents);
        });

        it('Should select action', function () {
            var actionInfo = {
                displayName : 'Action 1',
                channelName : 'test-action',
                moduleName : 'test-action',
                moduleVersion : '0.20',
                subject : 'Action_1'
            };

            scope.selectedActionChannel = channels[0];

            scope.selectAction(channels[0].actionTaskEvents[0]);

            expect(scope.task.action).toEqual(actionInfo);
        });

        it('Should return proper operators', function () {
            var string = [ 'exist', 'equals', 'contains', 'startsWith', 'endsWith' ],
                number = [ 'exist', 'gt', 'lt', 'equal' ],
                other = [ 'exist' ];

            expect(scope.operators({ type: 'UNICODE' })).toEqual(string);
            expect(scope.operators({ type: 'INTEGER' })).toEqual(number);

            expect(scope.operators(null)).toEqual(other);
            expect(scope.operators(undefined)).toEqual(other);
            expect(scope.operators({})).toEqual(other);
        });

        it('Should refactor editable div', function () {
            var value = '<span unselectable="on" contenteditable="false" style="position: relative;" class="popoverEvent nonEditable triggerField pointer badge badge-info" manipulationpopover="" data-prefix="trigger" data-type="UNICODE" data-object="Key 1">Key 1</span> - <span unselectable="on" contenteditable="false" style="position: relative;" class="popoverEvent nonEditable triggerField pointer badge badge-warning" manipulationpopover="" data-prefix="ad" data-type="UNICODE" data-object="fieldDisplayName1" data-source="ds-1" data-object-id="0" data-object-type="TestType1" data-field="filedKey1">ds-1.obj.1#0.fieldDisplayName1</span>',
                expected = '{{trigger.key_1}} - {{ad.ds-1.TestType1#0.filedKey1}}';

            scope.BrowserDetect = { browser: 'Explorer' };
            scope.msg = function (key) { return key; };

            scope.selectedTrigger = channels[1].triggerTaskEvents[0];

            expect(scope.refactorDivEditable(value)).toEqual(expected);
        });

        it('Should create draggable element', function () {
            var value = '{{trigger.key_1}} - {{ad.123.TestType1#0.filedKey1}}',
                expected = '<span unselectable="on" contenteditable="false" style="position: relative;" class="popoverEvent nonEditable triggerField pointer badge badge-info" manipulationpopover="" data-prefix="trigger" data-type="UNICODE" data-object="Key 1">Key 1</span> - <span unselectable="on" contenteditable="false" style="position: relative;" class="popoverEvent nonEditable triggerField pointer badge badge-warning" manipulationpopover="" data-prefix="ad" data-type="UNICODE" data-object="fieldDisplayName1" data-source="ds-1" data-object-id="0" data-object-type="TestType1" data-field="filedKey1">ds-1.obj.1#0.fieldDisplayName1</span>';

            scope.BrowserDetect = { browser: 'Explorer' };
            scope.msg = function (key) { return key; };

            scope.selectedTrigger = channels[1].triggerTaskEvents[0];
            scope.selectedDataSources = [{
                id: 0,
                dataSourceName: 'ds-1',
                dataSourceId: '123',
                displayName: 'obj.1',
                type: 'TestType1',
                lookup: {
                    field: 'filedKey1',
                    value: 'def'
                }
            }];


            expect(scope.createDraggableElement(value)).toEqual(expected);
        });

        it('Should return correct boolean value according to property value for action css class', function () {
            scope.BrowserDetect = { browser: 'FireFox' };

            expect(scope.actionCssClass(undefined)).toEqual(false);
            expect(scope.actionCssClass(null)).toEqual(false);
            expect(scope.actionCssClass({ value: '' })).toEqual(false);

            scope.selectedTrigger = channels[1].triggerTaskEvents[0];

            expect(scope.actionCssClass({ value: '' })).toEqual(true);
            expect(scope.actionCssClass({ value: '{{trigger.value}}' })).toEqual(false);

            scope.BrowserDetect = { browser: 'Chrome' };

            expect(scope.actionCssClass({ value: '<br/>' })).toEqual(true);
            expect(scope.actionCssClass({ value: '<span>Value</span>' })).toEqual(false);
        });

        it('Should get boolean value', function () {
            expect(scope.getBooleanValue('false')).toEqual(null);
            expect(scope.getBooleanValue('true')).toEqual(null);
            expect(scope.getBooleanValue('abc')).toEqual('abc');
            expect(scope.getBooleanValue('abc')).not.toEqual(null);
        });

        it('Should set boolean value', function () {
            var yes = '<span contenteditable="false" data-value="true" data-prefix="other" class="badge badge-success">Yes</span>',
                no = '<span contenteditable="false" data-value="false" data-prefix="other" class="badge badge-important">No</span>';

            scope.selectedAction = {
                actionParameters: [ {} ]
            };

            scope.msg = function (key) {
                return key.charAt(0).toUpperCase() + key.slice(1);
            };

            scope.setBooleanValue(0, true);
            expect(scope.selectedAction.actionParameters[0].value).toEqual(yes);

            scope.setBooleanValue(0, false);
            expect(scope.selectedAction.actionParameters[0].value).toEqual(no);
        });

        it('Should check boolean', function () {
            scope.selectedAction = {
                actionParameters: [
                    { value: '<span contenteditable="false" data-value="true" data-prefix="other" class="badge badge-success">Yes</span>' },
                    { value: '<span contenteditable="false" data-value="false" data-prefix="other" class="badge badge-important">No</span>' },
                    { }
                ]
            };

            scope.BrowserDetect = { browser: 'Chrome' };

            expect(scope.checkedBoolean(0, 'true')).toEqual(true);
            expect(scope.checkedBoolean(1, 'true')).toEqual(false);
            expect(scope.checkedBoolean(2, 'true')).toEqual(false);
            expect(scope.checkedBoolean(0, 'false')).toEqual(false);
            expect(scope.checkedBoolean(1, 'false')).toEqual(true);
            expect(scope.checkedBoolean(2, 'false')).toEqual(false);
        });

    });

}());
