package org.motechproject.server.messagecampaign.userspecified;

import org.motechproject.server.messagecampaign.domain.campaign.CampaignType;
import org.motechproject.server.messagecampaign.domain.message.*;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.List;

public class CampaignMessageRecord {

    private String name;
    private List<String> formats;
    private List<String> languages;
    private String messageKey;
    private Date date;
    private String timeOffset;
    private String repeatInterval;
    private String cron;


    public CampaignMessage build(CampaignType type) {

        if (type == CampaignType.ABSOLUTE) {
            return buildAbsolute();
        }
        if (type == CampaignType.OFFSET) {
            return buildOffset();
        }
        if (type == CampaignType.REPEATING) {
            return buildRepeating();
        }
        if (type == CampaignType.CRON) {
            return buildCron();
        }
        throw new RuntimeException("Unknown campaign type");
    }

    private CampaignMessage buildCron() {
        CronBasedCampaignMessage message = new CronBasedCampaignMessage();
        message.name(name);
        message.formats(formats);
        message.languages(languages);
        message.messageKey(messageKey);
        message.cron(cron);
        return message;
    }

    private CampaignMessage buildRepeating() {
        RepeatingCampaignMessage message = new RepeatingCampaignMessage();
        message.name(name);
        message.formats(formats);
        message.languages(languages);
        message.messageKey(messageKey);
        message.repeatInterval(repeatInterval);
        return message;
    }

    private CampaignMessage buildAbsolute() {
        AbsoluteCampaignMessage message = new AbsoluteCampaignMessage();
        message.name(name);
        message.formats(formats);
        message.languages(languages);
        message.messageKey(messageKey);
        message.date(DateUtil.newDate(date));
        return message;
    }

    private CampaignMessage buildOffset() {
        OffsetCampaignMessage message = new OffsetCampaignMessage();
        message.name(name);
        message.formats(formats);
        message.languages(languages);
        message.messageKey(messageKey);
        message.timeOffset(timeOffset);
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

    public CampaignMessageRecord name(String name) {
        this.name = name;
        return this;
    }

    public CampaignMessageRecord formats(List<String> formats) {
        this.formats = formats;
        return this;
    }

    public CampaignMessageRecord languages(List<String> languages) {
        this.languages = languages;
        return this;
    }

    public CampaignMessageRecord messageKey(String messageKey) {
        this.messageKey = messageKey;
        return this;
    }

    public CampaignMessageRecord date(Date date) {
        this.date = date;
        return this;
    }

    public String timeOffset() {
        return timeOffset;
    }

    public CampaignMessageRecord timeOffset(String timeOffset) {
        this.timeOffset = timeOffset;
        return this;
    }

    public String repeatInterval() {
        return this.repeatInterval;
    }

    public void cron(String cron) {
        this.cron = cron;
    }

    public String cron() {
        return cron;
    }
}
