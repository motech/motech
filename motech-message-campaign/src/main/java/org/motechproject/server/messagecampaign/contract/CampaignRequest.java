package org.motechproject.server.messagecampaign.contract;

import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

import java.util.ArrayList;
import java.util.List;

public class CampaignRequest {
    private String externalId;
    private String campaignName;
    private Time reminderTime;
    private LocalDate referenceDate;
    private Integer startOffset;
    private List<DayOfWeek> userPreferredDays;

    public CampaignRequest() {
    }

    public CampaignRequest(String externalId, String campaignName, Time reminderTime, LocalDate referenceDate) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.reminderTime = reminderTime;
        this.referenceDate = referenceDate;
        userPreferredDays = new ArrayList<DayOfWeek>();
    }

    public CampaignRequest(String externalId, String campaignName, Time reminderTime, LocalDate referenceDate, Integer startOffset) {
        this(externalId, campaignName, reminderTime, referenceDate);
        this.startOffset = startOffset;
    }

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
