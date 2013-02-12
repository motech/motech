package org.motechproject.messagecampaign.web.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.messagecampaign.domain.campaign.CampaignType;
import org.motechproject.messagecampaign.userspecified.CampaignMessageRecord;
import org.motechproject.messagecampaign.userspecified.CampaignRecord;

import java.io.Serializable;
import java.util.List;

public class CampaignDto implements Serializable {

    private static final long serialVersionUID = -4318242403037577752L;

    @JsonProperty
    private String name;

    @JsonProperty
    private CampaignType type;

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String maxDuration;

    @JsonProperty
    private List<CampaignMessageRecord> messages;

    public CampaignDto() {
    }

    public CampaignDto(CampaignRecord campaignRecord) {
        this.name = campaignRecord.getName();
        this.messages = campaignRecord.getMessages();
        this.type = campaignRecord.getCampaignType();
        this.maxDuration = campaignRecord.getMaxDuration();
    }

    public CampaignRecord toCampaignRecord() {
        CampaignRecord campaignRecord = new CampaignRecord();

        campaignRecord.setName(name);
        campaignRecord.setMaxDuration(maxDuration);
        campaignRecord.setMessages(messages);
        campaignRecord.setCampaignType(type);

        return campaignRecord;
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

    public CampaignType getType() {
        return type;
    }

    public void setType(CampaignType type) {
        this.type = type;
    }

    public String getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }
}
