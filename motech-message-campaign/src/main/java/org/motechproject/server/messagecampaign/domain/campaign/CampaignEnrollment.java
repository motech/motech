package org.motechproject.server.messagecampaign.domain.campaign;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingMessageMode;

import static org.motechproject.server.messagecampaign.Constants.DEFAULT_INTERVAL_OFFSET;

@TypeDiscriminator("doc.type === 'CampaignEnrollment'")
public class CampaignEnrollment extends MotechBaseDataObject {

    @JsonProperty
    private String externalId;
    @JsonProperty
    private String campaignName;
    private Integer startOffset;
    private LocalDate startDate;

    private CampaignEnrollment() {
    }

    public CampaignEnrollment(String externalId, String campaignName) {
        this.externalId = externalId;
        this.campaignName = campaignName;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    private Integer getStartOffset() {
        return startOffset;
    }

    public CampaignEnrollment setStartOffset(Integer startOffset) {
        this.startOffset = startOffset;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public CampaignEnrollment setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public int startOffset(RepeatingCampaignMessage message) {
        Integer offset = getStartOffset();
        // no startOffset handling for repeatInterval
        return isRepeatingIntervalMode(message) || offset == null ? DEFAULT_INTERVAL_OFFSET : offset;
    }

    private boolean isRepeatingIntervalMode(RepeatingCampaignMessage message) {
        return message.mode().equals(RepeatingMessageMode.REPEAT_INTERVAL);
    }

    public CampaignEnrollment copyFrom(CampaignEnrollment enrollment) {
        this.startDate = enrollment.getStartDate();
        this.startOffset = enrollment.getStartOffset();
        return this;
    }
}
