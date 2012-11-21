package org.motechproject.server.messagecampaign.userspecified;

import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignType;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.DayOfWeekCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatIntervalCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.commons.date.util.TimeIntervalParser;

import java.util.ArrayList;
import java.util.List;

public class CampaignRecord {

    private String name;
    private List<CampaignMessageRecord> messages;
    private CampaignType type;
    private String maxDuration;

    public String name() {
        return this.name;
    }

    public Campaign build() {
        Campaign campaign = type.instance();
        campaign.setMessages(buildCampaignMessages());
        campaign.setName(this.name());
        if (campaign instanceof OffsetCampaign) {
            ((OffsetCampaign) campaign).maxDuration(maxDuration);
        } else if (campaign instanceof RepeatIntervalCampaign) {
            ((RepeatIntervalCampaign) campaign).maxDuration(new TimeIntervalParser().parse(maxDuration));
        } else if (campaign instanceof DayOfWeekCampaign) {
            ((DayOfWeekCampaign) campaign).maxDuration(new TimeIntervalParser().parse(maxDuration));
        } else if (campaign instanceof CronBasedCampaign) {
            ((CronBasedCampaign) campaign).maxDuration(maxDuration);
        }
        return campaign;
    }

    private List<CampaignMessage> buildCampaignMessages() {
        List<CampaignMessage> campaignMessages = new ArrayList<CampaignMessage>();
        for (CampaignMessageRecord messageRecord : this.messageBuilders()) {
            campaignMessages.add(messageRecord.build(type));
        }
        return campaignMessages;
    }

    private List<CampaignMessageRecord> messageBuilders() {
        return this.messages;
    }

    public CampaignRecord name(String name) {
        this.name = name;
        return this;
    }

    public CampaignRecord messages(List<CampaignMessageRecord> campaignMessageRecords) {
        this.messages = campaignMessageRecords;
        return this;
    }

    public CampaignRecord type(CampaignType type) {
        this.type = type;
        return this;
    }

    public String maxDuration() {
        return maxDuration;
    }

    public CampaignRecord maxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
        return this;
    }
}
