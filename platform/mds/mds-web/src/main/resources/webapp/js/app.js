(function () {
    'use strict';

    var mds = angular.module('mds', [ 'motech-dashboard', 'mds.services', 'webSecurity.services',
        'mds.controllers', 'mds.directives', 'mds.utils', 'ngCookies', 'ui.directives',
        'ngRoute']);

    $.ajax({
        url:      '../mds/available/mdsTabs',
        success:  function(data) {
            mds.constant('AVAILABLE_TABS', data);
        },
        async:    false
    });

    mds.run(function ($rootScope, AVAILABLE_TABS) {
        $rootScope.AVAILABLE_TABS = AVAILABLE_TABS;
    });

    mds.config(function ($routeProvider, AVAILABLE_TABS) {
        angular.forEach(AVAILABLE_TABS, function (tab) {
            $routeProvider.when(
                '/mds/{0}'.format(tab),
                {
                    templateUrl: '../mds/resources/partials/{0}.html'.format(tab),
                    controller: '{0}Ctrl'.format(tab.capitalize())
                }
            );
        });
    });
}());
