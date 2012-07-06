package org.motechproject.server.messagecampaign.domain.message;

import org.motechproject.model.DayOfWeek;

import java.util.List;

import static org.motechproject.model.Time.parseTime;

public class DayOfWeekCampaignMessage extends CampaignMessage {

    private List<DayOfWeek> daysOfWeek;

    public DayOfWeekCampaignMessage(List<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }
}
