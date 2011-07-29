package org.motechproject.server.messagecampaign.builder;

import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;

import java.util.Date;
import java.util.List;

public class CampaignMessageBuilder {

    private String name;
    private List<String> formats;
    private List<String> languages;
    private String messageKey;
    private Date date;
    private String timeOffset;


    public CampaignMessage build() {
        AbsoluteCampaignMessage message = new AbsoluteCampaignMessage();
        message.name(name);
        message.formats(formats);
        message.languages(languages);
        message.messageKey(messageKey);
        message.date(date);
        return message;
    }

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

    public Date date() {
        return date;
    }

    public void name(String name) {
        this.name = name;
    }

    public void formats(List<String> formats) {
        this.formats = formats;
    }

    public void languages(List<String> languages) {
        this.languages = languages;
    }

    public void messageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public void date(Date date) {
        this.date = date;
    }

    public String timeOffset() {
        return timeOffset;
    }
}
