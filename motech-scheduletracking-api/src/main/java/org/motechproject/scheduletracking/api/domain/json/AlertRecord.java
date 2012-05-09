package org.motechproject.scheduletracking.api.domain.json;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class AlertRecord {
    @JsonProperty
    private String window;
    @JsonProperty
    private List<String> offset = new ArrayList<String>();
    @JsonProperty
    private List<String> interval = new ArrayList<String>();
    @JsonProperty
    private String count;
    @JsonProperty
    private boolean floating;

    public List<String> offset() {
        return offset;
    }

    public List<String> interval() {
        return interval;
    }

    public String window() {
        return window;
    }

    public String count() {
        return count;
    }

    public boolean isFloating() {
        return floating;
    }
}
