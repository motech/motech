package org.motechproject.messagecampaign.web.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.messagecampaign.web.util.LocalDateSerializer;
import org.motechproject.messagecampaign.web.util.TimeSerializer;

import java.io.Serializable;

public class EnrollmentRequest implements Serializable {

    private static final long serialVersionUID = 8082316095036755730L;

    @JsonProperty
    private String enrollmentId;

    @JsonProperty
    @JsonSerialize(using = TimeSerializer.class)
    private Time startTime;

    @JsonProperty
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate referenceDate;

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

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

    public EnrollmentRequest(String enrollmentId, Time startTime, LocalDate referenceDate) {
        this.enrollmentId = enrollmentId;
        this.startTime = startTime;
        this.referenceDate = referenceDate;
    }

    public EnrollmentRequest() {
    }
}
