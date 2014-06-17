package org.motechproject.eventlogging.domain;

import org.motechproject.mds.annotations.Entity;

@Entity
public class Sample {
    private Integer i;
    private String s;
    private EventLog log;

    public Sample(Integer i, String s) {
        this.i = i;
        this.s = s;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public EventLog getLog() {
        return log;
    }

    public void setLog(EventLog log) {
        this.log = log;
    }
}
