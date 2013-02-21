package org.motechproject.server.messagecampaign.domain.message;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.commons.date.model.Time;
import org.motechproject.server.messagecampaign.web.util.TimeSerializer;

import java.util.List;

public class CampaignMessage {
    @JsonProperty
    private String name;
    @JsonProperty
    private List<String> formats;
    @JsonProperty
    private List<String> languages;
    @JsonProperty
    private String messageKey;
    @JsonProperty
    @JsonSerialize(using = TimeSerializer.class)
    private Time startTime;

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

    public CampaignMessage name(String name) {
        this.name = name;
        return this;
    }

    public CampaignMessage formats(List<String> formats) {
        this.formats = formats;
        return this;
    }

    public CampaignMessage languages(List<String> languages) {
        this.languages = languages;
        return this;
    }

    public CampaignMessage messageKey(String messageKey) {
        this.messageKey = messageKey;
        return this;
    }

    public Time getStartTime() {
        return startTime;
    }

    public CampaignMessage setStartTime(Time startTime) {
        this.startTime = startTime;
        return this;
    }

    public CampaignMessage setStartTime(int hour, int minute) {
        this.startTime = new Time(hour, minute);
        return this;
    }
}
