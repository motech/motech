package org.motechproject.server.messagecampaign.domain.campaign;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingMessageMode;

import static org.motechproject.server.messagecampaign.Constants.REPEATING_DEFAULT_START_OFFSET;

@TypeDiscriminator("doc.type === 'CampaignEnrollment'")
public class CampaignEnrollment extends MotechBaseDataObject {

    @JsonProperty
    private String externalId;
    @JsonProperty
    private String campaignName;
    @JsonProperty
    private Integer startOffset;
    @JsonProperty
    private CampaignEnrollmentStatus status;

    private LocalDate startDate;

    private CampaignEnrollment() {
    }

    public CampaignEnrollment(String externalId, String campaignName) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.status = CampaignEnrollmentStatus.ACTIVE;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCampaignName() {
        return campaignName;
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
        Integer offset = startOffset;
        // no startOffset handling for repeatInterval
        return isRepeatingIntervalMode(message) || offset == null ? REPEATING_DEFAULT_START_OFFSET : offset;
    }

    private boolean isRepeatingIntervalMode(RepeatingCampaignMessage message) {
        return message.mode().equals(RepeatingMessageMode.REPEAT_INTERVAL);
    }

    public CampaignEnrollment copyFrom(CampaignEnrollment enrollment) {
        this.startDate = enrollment.getStartDate();
        this.startOffset = enrollment.startOffset;
        this.status = enrollment.getStatus();
        return this;
    }

    public CampaignEnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignEnrollmentStatus status) {
        this.status = status;
    }
}
