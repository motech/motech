package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.Period;

public class Alert {

    @JsonProperty
    private Period offset;
    @JsonProperty
    private Period interval;
    @JsonProperty
    private int count;
    @JsonProperty
    private int index;
    @JsonProperty
    private boolean floating;

    private Alert() {
    }

    public Alert(Period offset, Period interval, int count, int index, boolean floating) {
        this.offset = offset;
        this.interval = interval;
        this.count = count;
        this.index = index;
        this.floating = floating;
    }

    @JsonIgnore
    public int getCount() {
        return count;
    }

    @JsonIgnore
    public Period getOffset() {
        return offset;
    }

    @JsonIgnore
    public Period getInterval() {
        return interval;
    }

    @JsonIgnore
    public int getIndex() {
        return index;
    }

    @JsonIgnore
    public boolean isFloating() {
        return floating;
    }
}
