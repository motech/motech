package org.motechproject.server.messagecampaign.builder;

import org.motechproject.server.messagecampaign.domain.campaign.CampaignType;
import org.motechproject.server.messagecampaign.userspecified.CampaignMessageRecord;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;

import java.util.ArrayList;
import java.util.List;

public class CampaignRecordBuilder {

    public static CampaignRecord absoluteCampaignRecord(String name, CampaignMessageRecord absoluteCampaignMessageRecord) {
        List<CampaignMessageRecord> campaignMessageRecords = new ArrayList<CampaignMessageRecord>();
        campaignMessageRecords.add(absoluteCampaignMessageRecord);

        CampaignRecord record = new CampaignRecord();
        record.setName(name);
        record.setCampaignType(CampaignType.ABSOLUTE);
        record.setMessages(campaignMessageRecords);

        return record;
    }

    public static CampaignRecord offsetCampaignRecord(String name, CampaignMessageRecord offsetCampaignMessageRecord) {
        List<CampaignMessageRecord> campaignMessageRecords = new ArrayList<CampaignMessageRecord>();
        campaignMessageRecords.add(offsetCampaignMessageRecord);

        CampaignRecord record = new CampaignRecord();
        record.setName(name);
        record.setCampaignType(CampaignType.OFFSET);
        record.setMaxDuration("2 Weeks");
        record.setMessages(campaignMessageRecords);

        return record;
    }

    public static CampaignRecord cronBasedCampaignRecord(String name, CampaignMessageRecord cronBasedMessageRecord) {
        List<CampaignMessageRecord> campaignMessageRecords = new ArrayList<CampaignMessageRecord>();
        campaignMessageRecords.add(cronBasedMessageRecord);

        CampaignRecord record = new CampaignRecord();
        record.setName(name);
        record.setCampaignType(CampaignType.CRON);
        record.setMessages(campaignMessageRecords);

        return record;
    }
}
