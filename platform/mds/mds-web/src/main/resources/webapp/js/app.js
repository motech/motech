(function () {
    'use strict';

    var mds = angular.module('data-services', [ 'motech-dashboard', 'data-services.services', 'webSecurity.services',
        'data-services.controllers', 'data-services.directives', 'data-services.utils', 'ui.directives']);

    $.ajax({
        url:      '../mds/available/mdsTabs',
        success:  function(data) {
            mds.constant('AVAILABLE_TABS', ["dataBrowser","schemaEditor","settings"]);
        },
        async:    true
    });

    mds.run(function ($rootScope, AVAILABLE_TABS) {
        $rootScope.AVAILABLE_TABS = ["dataBrowser","schemaEditor","settings"];
    });

    mds.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
        //$urlRouterProvider.when("mds", "/dataBrowser");

        $stateProvider
            .state('mds', {
                url: "/mds",
                abstract: true,
                views: {
                    "moduleToLoad": {
                        templateUrl: "../mds/resources/index.html"
                    }
                }
            })
            .state('mds.dataBrowser', {
                url: '/dataBrowser',
                parent: 'mds',
                views: {
                    'mdsview': {
                        templateUrl: '../mds/resources/partials/dataBrowser.html',
                        controller: 'MdsDataBrowserCtrl'
                    }
                }
            })
            .state('mds.entityId', {
                url: '/:entityId',
                parent: 'mds.dataBrowser',
                views: {
                    'mdsview': {
                        templateUrl: '../mds/resources/partials/dataBrowser.html',
                        controller: 'MdsDataBrowserCtrl'
                    }
                }
            })
            .state('mds.moduleName', {
                url: '/:entityId/:moduleName',
                parent: 'mds.dataBrowser',
                views: {
                    'mdsview': {
                        templateUrl: '../mds/resources/partials/dataBrowser.html',
                        controller: 'MdsDataBrowserCtrl'
                    }
                }
            })
            .state('mds.instanceId', {
                url: '/:entityId/:instanceId/:moduleName',
                parent: 'mds.dataBrowser',
                views: {
                    'mdsview': {
                        templateUrl: '../mds/resources/partials/dataBrowser.html',
                        controller: 'MdsDataBrowserCtrl'
                    }
                }
            })
            .state('mds.schemaEditor', {
                url: '/schemaEditor',
                parent: 'mds',
                views: {
                    'mdsview': {
                        templateUrl: '../mds/resources/partials/schemaEditor.html',
                        controller: 'MdsSchemaEditorCtrl'
                    }
                }
            })
            .state('mds.settings', {
                url: '/settings',
                parent: 'mds',
                views: {
                    'mdsview': {
                        templateUrl: '../mds/resources/partials/settings.html',
                        controller: 'MdsSettingsCtrl'
                    }
                }
            });
    }]);

}());
