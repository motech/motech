package org.motechproject.server.messagecampaign.domain.campaign;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.model.Time;

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
    @JsonProperty
    private Time referenceTime;

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
        this.status = enrollment.getStatus();
        this.deliverTime = enrollment.getDeliverTime();
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

    public CampaignEnrollment setDeliverTime(int hour, int minute) {
        this.deliverTime = new Time(hour, minute);
        return this;
    }

    public Time getReferenceTime() {
        return referenceTime;
    }

    public CampaignEnrollment setReferenceTime(int hour, int minute) {
        this.referenceTime = new Time(hour, minute);
        return this;
    }

    public CampaignEnrollment setReferenceTime(Time referenceTime) {
        this.referenceTime = referenceTime;
        return this;
    }

    @JsonIgnore
    public boolean isActive() {
        return  status.equals(CampaignEnrollmentStatus.ACTIVE);
    }
}
