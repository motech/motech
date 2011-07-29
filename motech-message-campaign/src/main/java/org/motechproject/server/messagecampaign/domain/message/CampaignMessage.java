package org.motechproject.server.messagecampaign.domain.message;

import java.util.List;

public class CampaignMessage {
    private String name;
    private List<String> formats;
    private List<String> languages;
    private String messageKey;

    //for parameterized relative programs
    private String repeatInterval;
    //for cron based programs
    private String cron;

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

    public String repeatInterval() {
        return this.repeatInterval;
    }

    public String cron() {
        return cron;
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

    public void repeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public void cron(String cron) {
        this.cron = cron;
    }
}