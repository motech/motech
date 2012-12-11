package org.motechproject.server.messagecampaign.contract;

import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;

/**
 * \ingroup MessageCampaign
 *
 * This is the document to enroll an entity into a campaign
 */
public class CampaignRequest {
    private String externalId;
    private String campaignName;
    private Time startTime;
    private LocalDate referenceDate;
    private Time referenceTime;

    public CampaignRequest() {
    }

    /**
     * Creates a request for enrollment into a campaign. Holds all fields required for the enrollment.
     * @param externalId - a client defined id to identify the enrollment
     * @param campaignName - the campaign into which the entity should be enrolled
     * @param referenceDate - the date the campaign has started for this enrollment. it can be in the past resulting in a delayed enrollment.
     * @param referenceTime - time to raise alert when repeat interval is less than a day
     * @param startTime - time of the day at which the alert must be raised. This overrides the campaign's deliverTime.
     */
    public CampaignRequest(String externalId, String campaignName, LocalDate referenceDate, Time referenceTime, Time startTime) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.referenceDate = referenceDate;
        this.referenceTime = referenceTime;
        this.startTime = startTime;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String campaignName() {
        return this.campaignName;
    }

    public String externalId() {
        return this.externalId;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time deliverTime() {
        return this.startTime;
    }

    public LocalDate referenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public Time referenceTime() {
        return referenceTime;
    }

    public void setReferenceTime(Time referenceTime) {
        this.referenceTime = referenceTime;
    }

    @Override
    public String toString() {
        return "CampaignRequest{" +
                "externalId='" + externalId + '\'' +
                ", campaignName='" + campaignName + '\'' +
                ", referenceDate=" + referenceDate +
                '}';
    }
}
