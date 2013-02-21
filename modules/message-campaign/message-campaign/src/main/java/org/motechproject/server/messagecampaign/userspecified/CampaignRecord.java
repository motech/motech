package org.motechproject.server.messagecampaign.userspecified;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignType;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.DayOfWeekCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatIntervalCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.commons.date.util.JodaFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@TypeDiscriminator("doc.type == 'CampaignRecord'")
public class CampaignRecord extends MotechBaseDataObject {

    private static final long serialVersionUID = 7088652519806025250L;

    private String name;
    private List<CampaignMessageRecord> messages = new ArrayList<>();
    private CampaignType campaignType;
    private String maxDuration;

    public Campaign build() {
        Campaign campaign = campaignType.instance();
        campaign.setMessages(buildCampaignMessages());
        campaign.setName(this.name);
        if (campaign instanceof OffsetCampaign) {
            ((OffsetCampaign) campaign).maxDuration(maxDuration);
        } else if (campaign instanceof RepeatIntervalCampaign) {
            ((RepeatIntervalCampaign) campaign).maxDuration(new JodaFormatter().parsePeriod(maxDuration));
        } else if (campaign instanceof DayOfWeekCampaign) {
            ((DayOfWeekCampaign) campaign).maxDuration(new JodaFormatter().parsePeriod(maxDuration));
        } else if (campaign instanceof CronBasedCampaign) {
            ((CronBasedCampaign) campaign).maxDuration(maxDuration);
        }
        return campaign;
    }

    private List<CampaignMessage> buildCampaignMessages() {
        List<CampaignMessage> campaignMessages = new ArrayList<CampaignMessage>();
        for (CampaignMessageRecord messageRecord : this.messages) {
            campaignMessages.add(messageRecord.build(campaignType));
        }
        return campaignMessages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CampaignMessageRecord> getMessages() {
        return messages;
    }

    public void setMessages(List<CampaignMessageRecord> messages) {
        this.messages = messages;
    }

    public CampaignType getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(CampaignType type) {
        this.campaignType = type;
    }

    public String getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void updateFrom(CampaignRecord other) {
        name = other.name;
        messages = other.messages;
        campaignType = other.campaignType;
        maxDuration = other.maxDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CampaignRecord other = (CampaignRecord) o;

        return Objects.equals(campaignType, other.campaignType) && Objects.equals(maxDuration, other.maxDuration)
                && Objects.equals(messages, other.messages) && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (messages != null ? messages.hashCode() : 0);
        result = 31 * result + (campaignType != null ? campaignType.hashCode() : 0);
        result = 31 * result + (maxDuration != null ? maxDuration.hashCode() : 0);
        return result;
    }
}
