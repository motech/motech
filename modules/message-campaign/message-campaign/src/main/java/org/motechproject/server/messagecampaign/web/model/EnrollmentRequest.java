package org.motechproject.server.messagecampaign.web.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.server.messagecampaign.web.util.LocalDateSerializer;
import org.motechproject.server.messagecampaign.web.util.TimeSerializer;

import java.io.Serializable;

public class EnrollmentRequest implements Serializable {

    private static final long serialVersionUID = 8082316095036755730L;

    @JsonProperty
    @JsonSerialize(using = TimeSerializer.class)
    private Time startTime;

    @JsonProperty
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate referenceDate;

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public EnrollmentRequest(Time startTime, LocalDate referenceDate) {
        this.startTime = startTime;
        this.referenceDate = referenceDate;
    }

    public EnrollmentRequest() {
        this(null, null);
    }
}
