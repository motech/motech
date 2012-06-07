package org.motechproject.server.messagecampaign.contract;

import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * \ingroup MessageCampaign
 *
 * This is the document to enroll an entity into a campaign
 */
public class CampaignRequest {
    private String externalId;
    private String campaignName;
    private Time reminderTime;
    private Time deliverTime;
    private LocalDate referenceDate;
    private Integer startOffset;
    private List<DayOfWeek> userPreferredDays;

    public CampaignRequest() {
    }

    /**
     * Creates a request for enrollment into a campaign. Holds all fields required for the enrollment.
     * @param externalId - a client defined id to identify the enrollment
     * @param campaignName - the campaign into which the entity should be enrolled
     * @param reminderTime - time of day at which the alert must be raised. can be overridden by campaign's deliverTime, unless 24.hour.repeating.campaign.strategy is true
     * @param referenceDate - the date the campaign has started for this enrollment. it can be in the past resulting in a delayed enrollment.
     */
    public CampaignRequest(String externalId, String campaignName, Time reminderTime, LocalDate referenceDate) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.reminderTime = reminderTime;
        this.referenceDate = referenceDate;
        userPreferredDays = new ArrayList<DayOfWeek>();
    }

    /**
     * Creates a request for enrollment into a campaign. Holds all fields required for the enrollment.
     * @param externalId - a client defined id to identify the enrollment
     * @param campaignName - the campaign into which the entity should be enrolled
     * @param referenceDate - the date the campaign has started for this enrollment. it can be in the past resulting in a delayed enrollment.
     * @param deliverTime - time of the day at which the alert must be raised. This overrides the campaign's deliverTime.
     * @param userPreferredDays - specify a list of days that override the days defined by the campaign. The enrollment will have alerts raised on these days only.
     */
    public CampaignRequest(String externalId, String campaignName, LocalDate referenceDate, Time deliverTime, List<DayOfWeek> userPreferredDays) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.referenceDate = referenceDate;
        this.deliverTime = deliverTime;
        this.userPreferredDays = userPreferredDays;
    }

    /**
     * Creates a request for enrollment into a campaign. Holds all fields required for the enrollment.
     * @param externalId - a client defined id to identify the enrollment
     * @param campaignName - the campaign into which the entity should be enrolled
     * @param reminderTime - time of day at which the alert must be raised. can be overridden by campaign's deliverTime, unless 24.hour.repeating.campaign.strategy is true
     * @param referenceDate - the date the campaign has started for this enrollment. it can be in the past resulting in a delayed enrollment.
     * @param startOffset - the offset should be used in case of a delayed enrollment to specify where the enrollment begins w.r.t the referenceDate.
     */
    public CampaignRequest(String externalId, String campaignName, Time reminderTime, LocalDate referenceDate, Integer startOffset) {
        this(externalId, campaignName, reminderTime, referenceDate);
        this.startOffset = startOffset;
    }

    /**
     * Creates a request for enrollment into a campaign. Holds all fields required for the enrollment.
     * @param externalId - a client defined id to identify the enrollment
     * @param campaignName - the campaign into which the entity should be enrolled
     * @param reminderTime - time of day at which the alert must be raised. can be overridden by campaign's deliverTime, unless 24.hour.repeating.campaign.strategy is true
     * @param referenceDate - the date the campaign has started for this enrollment. it can be in the past resulting in a delayed enrollment.
     * @param userPreferredDays - specify a list of days that override the days defined by the campaign. The enrollment will have alerts raised on these days only.
     */
    public CampaignRequest(String externalId, String campaignName, Time reminderTime, LocalDate referenceDate, List<DayOfWeek> userPreferredDays) {
        this(externalId, campaignName, reminderTime, referenceDate);
        this.userPreferredDays.addAll(userPreferredDays);
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

    public void setReminderTime(Time reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Time reminderTime() {
        return this.reminderTime;
    }

    public void setDeliverTime(Time deliverTime) {
        this.deliverTime = deliverTime;
    }

    public Time deliverTime() {
        return this.deliverTime;
    }

    public LocalDate referenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public Integer startOffset() {
        return startOffset;
    }

    public void setStartOffset(Integer startOffset) {
        this.startOffset =startOffset;
    }

    @Override
    public String toString() {
        return "CampaignRequest{" +
                "externalId='" + externalId + '\'' +
                ", campaignName='" + campaignName + '\'' +
                ", reminderTime=" + reminderTime +
                ", referenceDate=" + referenceDate +
                ", startOffset=" + startOffset +
                '}';
    }


    public List<DayOfWeek> getUserPreferredDays() {
        return userPreferredDays;
    }

    public void setUserPreferredDays(List<DayOfWeek> userPreferredDays) {
        this.userPreferredDays = userPreferredDays;
    }
}
