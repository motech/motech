'use strict';

/* BundleListCtrl tests */

describe("Bundle List Controller", function() {

    var scope, ctrl, $httpBackend;

    var firstBundle, secondBundle, bundlesResponse;

    beforeEach(function() {
        this.addMatchers({
            toEqualData: function(expected) {
                return angular.equals(this.actual, expected);
            }
        });

        this.httpCall = function() {
            expect(scope.bundles).toEqual([]);
            $httpBackend.flush();
        }
    });

    beforeEach(module('bundleServices'));
    beforeEach(module('localization'));

    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {

        firstBundle = { bundleId: 1, state: 'ACTIVE', symbolicName: "First bundle" };
        secondBundle = { bundleId: 2, state: 'RESOLVED', symbolicName: "Second bundle" };
        bundlesResponse = [ firstBundle, secondBundle ];

        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('../admin/api/settings/bundles/list').respond([]);
        $httpBackend.expectGET('../admin/api/bundles').respond(bundlesResponse);

        scope = $rootScope.$new();
        ctrl = $controller(BundleListCtrl, {$scope: scope});
    }));


    it("Should fetch 2 bundles", function() {
        this.httpCall();

        expect(scope.bundles).toEqualData(bundlesResponse);
    });

    it("Should return dull class for resolved bundles", function() {
        this.httpCall();

        expect(scope.getIconClass(scope.bundles[0])).not.toEqual("dullImage");
        expect(scope.getIconClass(scope.bundles[1])).toEqual("dullImage");
    });

    it("Active and resolved bundles should be stable", function() {
        this.httpCall();

        for (bundle in scope.bundles) {
            expect(scope.bundleStable(bundle)).toEqual(true);
        }
    });
});