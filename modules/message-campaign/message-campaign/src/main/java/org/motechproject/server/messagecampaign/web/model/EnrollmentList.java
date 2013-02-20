package org.motechproject.server.messagecampaign.web.model;


import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentList {

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String campaignName;

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String externalId;

    @JsonProperty
    private List<EnrollmentDto> enrollments = new ArrayList<>();

    public EnrollmentList() {
    }

    public EnrollmentList(List<CampaignEnrollment> enrollments) {
        addEnrollments(enrollments);
    }

    public final void addEnrollments(List<CampaignEnrollment> enrollments) {
        for (CampaignEnrollment enrollment : enrollments) {
            this.enrollments.add(new EnrollmentDto(enrollment));
        }
    }

    public void setCommonCampaignName(String campaignName) {
        this.campaignName = campaignName;
        for (EnrollmentDto enrollmentDto : enrollments) {
            enrollmentDto.setCampaignName(null);
        }
    }

    public void setCommonExternalId(String externalId) {
        this.externalId = externalId;
        for (EnrollmentDto enrollmentDto : enrollments) {
            enrollmentDto.setExternalId(null);
        }
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<EnrollmentDto> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<EnrollmentDto> enrollments) {
        this.enrollments = enrollments;
    }
}
