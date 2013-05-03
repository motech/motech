'use strict';

/* Message controller tests */

describe("Status Message Controller", function() {

    var scope, ctrl, $httpBackend;

    var infoMsg, errorMsg, response;

    beforeEach(function(){
        this.addMatchers({
            toEqualData: function(expected) {
                return angular.equals(this.actual, expected);
            }
        });

        this.httpCall = function() {
            expect(scope.messages).toEqual([]);
            $httpBackend.flush();
        }
    });

    beforeEach(module('messageServices'));
    beforeEach(module('localization'));
    beforeEach(module('ngCookies'));

    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {

        infoMsg = { level: 'INFO', text: 'First', _id: 1 };
        errorMsg = { level: 'ERROR', text: 'First', _id: 2 };
        response = [ infoMsg, errorMsg ];

        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('../admin/api/messages').
        respond(response);

        scope = $rootScope.$new();
        ctrl = $controller(StatusMsgCtrl, {$scope: scope});
    }));


    it("Should fetch 2 messages", function() {
        this.httpCall();

        expect(scope.messages).toEqualData(response);
    });

    it("Should return error class for ERROR messages", function() {
        this.httpCall();

        expect(scope.getCssClass(scope.messages[0])).toEqual("msg");
        expect(scope.getCssClass(scope.messages[1])).toEqual("msg error");
    });

    it("should add message to ignored after remove", function() {
        this.httpCall();

        scope.remove(scope.messages[1]);

        expect(scope.messages.length).toEqual(1);
        expect(scope.messages[0]).toEqualData(infoMsg);
        expect(scope.ignoredMessages.length).toEqual(1);
        expect(scope.ignoredMessages[0]).toEqual(errorMsg._id);
    });
});