package org.motechproject.server.messagecampaign.service;

public interface MessageCampaignService {
    void enroll(String campaignName, int startHour, int startMinute);
}
