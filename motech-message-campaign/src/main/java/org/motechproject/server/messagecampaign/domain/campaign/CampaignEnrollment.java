package org.motechproject.server.messagecampaign.domain.campaign;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.model.Time;

@TypeDiscriminator("doc.type === 'CampaignEnrollment'")
public class CampaignEnrollment extends MotechBaseDataObject {

    @JsonProperty
    private String externalId;
    @JsonProperty
    private String campaignName;
    @JsonProperty
    private CampaignEnrollmentStatus status;
    @JsonProperty
    private LocalDate referenceDate;
    @JsonProperty
    private Time deliverTime;

    private CampaignEnrollment() {
    }

    public CampaignEnrollment(String externalId, String campaignName) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.status = CampaignEnrollmentStatus.ACTIVE;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public CampaignEnrollment setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
        return this;
    }

    public CampaignEnrollment copyFrom(CampaignEnrollment enrollment) {
        this.referenceDate = enrollment.getReferenceDate();
        return this;
    }

    public CampaignEnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignEnrollmentStatus status) {
        this.status = status;
    }

    public Time getDeliverTime() {
        return deliverTime;
    }

    public CampaignEnrollment setDeliverTime(Time deliverTime) {
        this.deliverTime = deliverTime;
        return this;
    }
}
