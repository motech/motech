package org.motechproject.messagecampaign.domain.message;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.messagecampaign.web.util.LocalDateSerializer;

public class AbsoluteCampaignMessage extends CampaignMessage {

    @JsonProperty
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    public LocalDate date() {
        return this.date;
    }

    public void date(LocalDate date) {
        this.date = date;
    }
}
