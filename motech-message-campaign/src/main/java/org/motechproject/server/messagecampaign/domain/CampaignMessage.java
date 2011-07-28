package org.motechproject.server.messagecampaign.domain;

import java.util.Date;
import java.util.List;

public class CampaignMessage {
    private String name;
    private List<String> formats;
    private List<String> languages;
    private String messageKey;

    //for absolute programs
    private Date date;

    //for simple relative programs
    private String timeOffset;
    //for parameterized reltive programs
    private String repeatInterval;

    public String name() {
        return name;
    }

    public List<String> formats() {
        return formats;
    }

    public List<String> languages() {
        return languages;
    }

    public String messageKey() {
        return messageKey;
    }

    public String timeOffset() {
        return timeOffset;
    }

    public Date date() {
        return this.date;
    }

    public String repeatInterval() {
        return this.repeatInterval;
    }
}
