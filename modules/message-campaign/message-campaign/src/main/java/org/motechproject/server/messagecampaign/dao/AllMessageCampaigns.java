package org.motechproject.server.messagecampaign.dao;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.equalTo;

@Component
public class AllMessageCampaigns {

    private SettingsFacade settings;
    private MotechJsonReader motechJsonReader;
    public String messageCampaignsJsonFile = "message-campaigns.json";
    private static List<Campaign> campaigns = new ArrayList<Campaign>();

    public AllMessageCampaigns(@Qualifier("messageCampaignSettings") SettingsFacade settings, MotechJsonReader motechJsonReader) {
        this.settings = settings;
        this.motechJsonReader = motechJsonReader;
    }

    @Autowired
    public AllMessageCampaigns(@Qualifier("messageCampaignSettings") SettingsFacade settings) {
        this(settings, new MotechJsonReader());
    }

    public Campaign get(String campaignName) {
        List<Campaign> campaign = select(readCampaignsFromJSON(), having(on(Campaign.class).getName(), equalTo(campaignName)));
        return CollectionUtils.isEmpty(campaign) ? null : campaign.get(0);
    }

    private List<Campaign> readCampaignsFromJSON() {
        if (CollectionUtils.isEmpty(campaigns)) {
            List<CampaignRecord> campaignRecords = (List<CampaignRecord>) motechJsonReader.readFromStream(
                settings.getRawConfig(messageCampaignsJsonFile),
                new TypeToken<List<CampaignRecord>>() { } .getType()
            );
            for (CampaignRecord campaignRecord : campaignRecords) {
                campaigns.add(campaignRecord.build());
            }
        }
        return campaigns;
    }

    public CampaignMessage get(String campaignName, String messageKey) {
        Campaign campaign = get(campaignName);
        if (campaign != null) {
            for (Object message : campaign.getMessages()) {
                CampaignMessage campaignMessage = (CampaignMessage) message;
                if (campaignMessage.messageKey().equals(messageKey)) {
                    return campaignMessage;
                }
            }
        }
        return null;
    }

    public void setMessageCampaignsJsonFile(String messageCampaignsJsonFile) {
        this.messageCampaignsJsonFile = messageCampaignsJsonFile;
    }

    public String getMessageCampaignsJsonFile() {
        return messageCampaignsJsonFile;
    }
}
