package org.motechproject.server.messagecampaign.userspecified;

import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignType;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.DayOfWeekCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatIntervalCampaignMessage;
import org.motechproject.util.DateUtil;
import org.motechproject.util.TimeIntervalParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.motechproject.model.Time.parseTime;

public class CampaignMessageRecord {

    private String name;
    private List<String> formats;
    private List<String> languages;
    private List<String> repeatOn;
    private String messageKey;
    private Date date;
    private String timeOffset;
    private String repeatEvery;
    private String cron;
    private String startTime;

    public CampaignMessage build(CampaignType type) {
        if (type == CampaignType.ABSOLUTE) {
            return buildAbsolute();
        }
        if (type == CampaignType.OFFSET) {
            return buildOffset();
        }
        if (type == CampaignType.REPEAT_INTERVAL) {
            return buildRepeatIntervalCampaignMessage();
        }
        if (type == CampaignType.DAY_OF_WEEK) {
            return buildDayOfWeekCampaignMessage();
        }
        if (type == CampaignType.CRON) {
            return buildCron();
        }
        throw new RuntimeException("Unknown campaign type");
    }

    private CampaignMessage buildDayOfWeekCampaignMessage() {
        DayOfWeekCampaignMessage message = new DayOfWeekCampaignMessage(getDaysOfWeek(repeatOn));
        message.name(name())
            .formats(formats())
            .languages(languages())
            .messageKey(messageKey())
            .setStartTime(parseTime(startTime, ":"));
        return message;
    }

    private List<DayOfWeek> getDaysOfWeek(List<String> days) {
        List<DayOfWeek> daysOfWeek = new ArrayList<>();
        for (String day : days) {
            daysOfWeek.add(DayOfWeek.parse(day));
        }
        return daysOfWeek;
    }

    private CampaignMessage buildRepeatIntervalCampaignMessage() {
        RepeatIntervalCampaignMessage message = new RepeatIntervalCampaignMessage(new TimeIntervalParser().parse(repeatInterval()));
        message.name(name())
            .formats(formats())
            .languages(languages())
            .messageKey(messageKey())
            .setStartTime(parseTime(startTime, ":"));
        return message;
    }

    private CampaignMessage buildCron() {
        CronBasedCampaignMessage message = new CronBasedCampaignMessage();
        message.name(name);
        message.formats(formats);
        message.languages(languages);
        message.messageKey(messageKey);
        message.cron(cron);
        message.setStartTime(parseTime(startTime, ":"));
        return message;
    }

    private CampaignMessage buildAbsolute() {
        AbsoluteCampaignMessage message = new AbsoluteCampaignMessage();
        message.name(name);
        message.formats(formats);
        message.languages(languages);
        message.messageKey(messageKey);
        message.date(DateUtil.newDate(date));
        message.setStartTime(parseTime(startTime, ":"));
        return message;
    }

    private CampaignMessage buildOffset() {
        OffsetCampaignMessage message = new OffsetCampaignMessage();
        message.name(name);
        message.formats(formats);
        message.languages(languages);
        message.messageKey(messageKey);
        message.timeOffset(new TimeIntervalParser().parse(timeOffset));
        message.setStartTime(Time.parseTime(startTime, ":"));
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

    public CampaignMessageRecord repeatEvery(String repeatEvery) {
        this.repeatEvery = repeatEvery;
        return this;
    }

    public CampaignMessageRecord timeOffset(String timeOffset) {
        this.timeOffset = timeOffset;
        return this;
    }

    public CampaignMessageRecord repeatOn(List<String> repeatOn) {
        this.repeatOn = repeatOn;
        return this;
    }

    public String repeatInterval() {
        return this.repeatEvery;
    }

    public void cron(String cron) {
        this.cron = cron;
    }

    public CampaignMessageRecord startTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String cron() {
        return cron;
    }
}
