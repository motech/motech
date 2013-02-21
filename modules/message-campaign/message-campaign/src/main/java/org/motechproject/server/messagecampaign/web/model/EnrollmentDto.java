package org.motechproject.server.messagecampaign.web.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.web.util.LocalDateSerializer;
import org.motechproject.server.messagecampaign.web.util.TimeSerializer;

import java.util.Objects;

public class EnrollmentDto {

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String externalId;

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String campaignName;

    @JsonProperty
    @JsonSerialize(using = TimeSerializer.class)
    private Time startTime;

    @JsonProperty
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate referenceDate;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public EnrollmentDto() {
    }

    public EnrollmentDto(CampaignEnrollment enrollment) {
        externalId = enrollment.getExternalId();
        campaignName = enrollment.getCampaignName();
        referenceDate = enrollment.getReferenceDate();
        startTime = enrollment.getDeliverTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnrollmentDto)) {
            return false;
        }

        EnrollmentDto that = (EnrollmentDto) o;

        return Objects.equals(externalId, that.externalId) && Objects.equals(campaignName, that.campaignName)
                && Objects.equals(referenceDate, that.referenceDate) && Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        int result = externalId != null ? externalId.hashCode() : 0;
        result = 31 * result + (campaignName != null ? campaignName.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (referenceDate != null ? referenceDate.hashCode() : 0);
        return result;
    }
}