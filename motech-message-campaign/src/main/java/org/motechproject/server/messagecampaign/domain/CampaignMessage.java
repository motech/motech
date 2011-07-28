package org.motechproject.server.messagecampaign.domain;

import java.util.Date;
import java.util.List;

public class CampaignMessage {
    private String name;
    private List<String> formats;
    private List<String> languages;
    private String messageKey;

    //for absolute schedules
    private Date date;

    private String timeOffset;

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
}
