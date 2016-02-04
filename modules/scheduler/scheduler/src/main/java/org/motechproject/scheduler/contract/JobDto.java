package org.motechproject.scheduler.contract;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.motechproject.scheduler.util.CustomDateDeserializer;
import org.motechproject.scheduler.util.CustomDateSerializer;

import java.util.HashMap;
import java.util.Map;

public class JobDto {

    private JobType type;

    private String motechEventSubject;

    private Map<String, Object> motechEventParameters;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private DateTime startDate;

    private Map<String, String> parameters;

    public JobDto() {
        parameters = new HashMap<>();
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public String getMotechEventSubject() {
        return motechEventSubject;
    }

    public void setMotechEventSubject(String motechEventSubject) {
        this.motechEventSubject = motechEventSubject;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getMotechEventParameters() {
        return motechEventParameters;
    }

    public void setMotechEventParameters(Map<String, Object> motechEventParameters) {
        this.motechEventParameters = motechEventParameters;
    }
}
