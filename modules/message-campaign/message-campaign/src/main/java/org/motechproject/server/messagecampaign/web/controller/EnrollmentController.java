package org.motechproject.server.messagecampaign.web.controller;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.web.model.EnrollmentList;
import org.motechproject.server.messagecampaign.web.model.EnrollmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/enrollments")
public class EnrollmentController {

    @Autowired
    private org.motechproject.server.messagecampaign.web.api.EnrollmentRestController enrollmentController;

    @RequestMapping(value = "/{campaignName}/users", method = RequestMethod.POST)
    @PreAuthorize("permitAll")
    @ResponseStatus(HttpStatus.OK)
    public void enrollOrUpdateUser(@PathVariable String campaignName,
                                   @RequestParam String externalId,
                                   @RequestParam String enrollmentId) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setEnrollmentId(enrollmentId);
        enrollmentRequest.setReferenceDate(DateUtil.today());
        enrollmentController.enrollOrUpdateUser(campaignName, externalId, enrollmentRequest);
    }


    @RequestMapping(value = "/{campaignName}/users/{externalId}", method = RequestMethod.DELETE)
    @PreAuthorize("permitAll")
    @ResponseStatus(HttpStatus.OK)
    public void removeEnrollment(@PathVariable String campaignName, @PathVariable String externalId) {
        enrollmentController.removeEnrollment(campaignName, externalId);
    }
    

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @PreAuthorize("permitAll")
    public @ResponseBody EnrollmentList getAllEnrollments(
            @RequestParam(required = false) String externalId, @RequestParam(required = false) String campaignName) {
        return enrollmentController.getAllEnrollments(CampaignEnrollmentStatus.ACTIVE.name(), externalId, campaignName);
    }
}

