(function () {
    'use strict';

    describe('Testing MDS Routes', function () {
        beforeEach(module('mds'));

        it('Should redirect to correct view', function () {

            inject(function ($route, AVAILABLE_TABS) {
                var path = null;

                expect($route.routes[path].redirectTo).toEqual('/{0}'.format(AVAILABLE_TABS[0]));

                angular.forEach(AVAILABLE_TABS, function (tab) {
                    path = '/{0}'.format(tab);

                    expect($route.routes[path].templateUrl)
                        .toEqual('../mds/resources/partials/{0}.html'.format(tab));
                    expect($route.routes[path].controller)
                        .toEqual('{0}Ctrl'.format(tab.capitalize()));
                });
            });

        });

    });

}());
