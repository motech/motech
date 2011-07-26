package org.motechproject.server.messagecampaign.domain;

import java.util.List;

public class CampaignMessage {
    private String name;
    private List<String> formats;
    private List<String> languages;
    private String messageKey;
    private String timeOffset;

    public String getName() {
        return name;
    }

    public List<String> getFormats() {
        return formats;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getTimeOffset() {
        return timeOffset;
    }
}
