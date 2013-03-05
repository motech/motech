'use strict';

angular.module('messageCampaign', ['motech-dashboard', 'CampaignService', 'EnrollmentService', 'ngCookies', 'bootstrap'])
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider
            .when('/campaigns', { templateUrl: '../messagecampaign/resources/partials/campaigns.html', controller: CampaignsCtrl })
            .when('/enrollments/:campaignName', { templateUrl: '../messagecampaign/resources/partials/enrollments.html', controller: EnrollmentsCtrl })
            .when('/admin', { templateUrl: '../messagecampaign/resources/partials/admin.html' })
            .otherwise({redirectTo: '/campaigns'});
    }]
);
