'use strict';

angular.module('CampaignService', ['ngResource']).factory('Campaigns', function ($resource) {
    return $resource('../messagecampaign/campaigns');
});

angular.module('EnrollmentService', ['ngResource']).factory('Enrollments', function ($resource) {
    return $resource('../messagecampaign/enrollments/users?enrollmentStatus=:enrollmentStatus&campaignName=:campaignName',
        {}, { query:{method:'GET', isArray:false}});
});
