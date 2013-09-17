(function () {
    'use strict';

    var seuss = angular.module('seuss', ['motech-dashboard', 'ngCookies', 'bootstrap']);

    seuss.constant('AVAILABLE_TABS', ['schemaEditor', 'dataBrowser', 'settings']);

    seuss.run(function ($rootScope, AVAILABLE_TABS) {
        $rootScope.AVAILABLE_TABS = AVAILABLE_TABS;
    });

    seuss.config(function ($routeProvider, AVAILABLE_TABS) {
        angular.forEach(AVAILABLE_TABS, function (tab) {
            $routeProvider.when(
                '/{0}'.format(tab),
                {
                    templateUrl: '../seuss/resources/partials/{0}.html'.format(tab),
                    controller: '{0}Ctrl'.format(tab.capitalize())
                }
            );
        });

        $routeProvider.otherwise({
            redirectTo: '/{0}'.format(AVAILABLE_TABS[0])
        });
    });
}());
