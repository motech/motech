package org.motechproject.messagecampaign.domain.message;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.commons.date.model.DayOfWeek;

import java.util.List;

public class DayOfWeekCampaignMessage extends CampaignMessage {

    @JsonProperty
    private List<DayOfWeek> daysOfWeek;

    public DayOfWeekCampaignMessage(List<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }
}
