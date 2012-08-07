package org.motechproject.scheduletracking.api.domain.json;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ScheduleWindowsRecord {

    @JsonProperty
    private List<String> earliest = new ArrayList<String>();
    @JsonProperty
    private List<String> due = new ArrayList<String>();
    @JsonProperty
    private List<String> late = new ArrayList<String>();
    @JsonProperty
    private List<String> max = new ArrayList<String>();

    public List<String> earliest() {
        return earliest;
    }

    public List<String> due() {
        return due;
    }

    public List<String> late() {
        return late;
    }

    public List<String> max() {
        return max;
    }
}
