(function () {
    'use strict';

    var mds = angular.module('data-services', [ 'motech-dashboard', 'data-services.services', 'webSecurity.services',
        'data-services.controllers', 'data-services.directives', 'data-services.utils', 'ui.directives', 'uiServices']);

    $.ajax({
        url:      '../mds/available/mdsTabs',
        success:  function(data) {
            mds.constant('MDS_AVAILABLE_TABS', data);
        },
        async:    false
    });

    mds.run(function ($rootScope, MDS_AVAILABLE_TABS) {
        $rootScope.MDS_AVAILABLE_TABS = MDS_AVAILABLE_TABS;
    });

    mds.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.when("mds", "/dataBrowser");

        $stateProvider
            .state('mds', {
                url: "/mds",
                abstract: true,
                views: {
                    "moduleToLoad": {
                        templateUrl: "../mds/resources/index.html"
                    }
                },
                resolve: {
                    loadMyService: ['$ocLazyLoad', function($ocLazyLoad) {
                        return $ocLazyLoad.load('webSecurity.services');
                    }]
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
            })
            .state('mds.entityId', {
                url: '/dataBrowser/:entityId',
                parent: 'mds',
                views: {
                    'mdsview': {
                        templateUrl: '../mds/resources/partials/dataBrowser.html',
                        controller: 'MdsDataBrowserCtrl'
                    }
                }
            })
            .state('mds.moduleName', {
                url: '/dataBrowser/:entityId/:moduleName',
                parent: 'mds',
                views: {
                    'mdsEmbeddedView': {
                        templateUrl: '../mds/resources/partials/dataBrowser.html',
                        controller: 'MdsDataBrowserCtrl'
                    }
                }
            })
            .state('mds.instanceId', {
                url: '/dataBrowser/:entityId/:instanceId/:moduleName',
                parent: 'mds',
                views: {
                    'mdsEmbeddedView': {
                        templateUrl: '../mds/resources/partials/dataBrowser.html',
                        controller: 'MdsDataBrowserCtrl'
                    }
                }
            });
    }]);

}());
