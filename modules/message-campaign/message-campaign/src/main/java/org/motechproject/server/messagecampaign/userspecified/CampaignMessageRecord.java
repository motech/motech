package org.motechproject.server.messagecampaign.userspecified;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.JodaFormatter;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignType;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.DayOfWeekCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatIntervalCampaignMessage;
import org.motechproject.server.messagecampaign.web.util.LocalDateSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.motechproject.commons.date.model.Time.parseTime;

public class CampaignMessageRecord {

    private static final long serialVersionUID = -1781293196314540037L;

    private String name;
    private List<String> formats;
    private List<String> languages;
    private String messageKey;
    private String startTime;

    @JsonSerialize(using = LocalDateSerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    private LocalDate date;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String timeOffset;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String repeatEvery;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String cron;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<String> repeatOn;

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
        throw new UnknownCampaignTypeException("Unknown campaign type");
    }

    private CampaignMessage buildDayOfWeekCampaignMessage() {
        DayOfWeekCampaignMessage message = new DayOfWeekCampaignMessage(getDaysOfWeek(repeatOn));
        message.name(getName())
            .formats(getFormats())
            .languages(getLanguages())
            .messageKey(getMessageKey())
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
        RepeatIntervalCampaignMessage message = new RepeatIntervalCampaignMessage(new JodaFormatter().
                parsePeriod(getRepeatEvery()));
        message.name(getName())
            .formats(getFormats())
            .languages(getLanguages())
            .messageKey(getMessageKey())
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
        message.date(date);
        message.setStartTime(parseTime(startTime, ":"));
        return message;
    }

    private CampaignMessage buildOffset() {
        OffsetCampaignMessage message = new OffsetCampaignMessage();
        message.name(name);
        message.formats(formats);
        message.languages(languages);
        message.messageKey(messageKey);
        message.timeOffset(new JodaFormatter().parsePeriod(timeOffset));
        message.setStartTime(Time.parseTime(startTime, ":"));
        return message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getRepeatOn() {
        return repeatOn;
    }

    public void setRepeatOn(List<String> repeatOn) {
        this.repeatOn = repeatOn;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(String timeOffset) {
        this.timeOffset = timeOffset;
    }

    public String getRepeatEvery() {
        return repeatEvery;
    }

    public void setRepeatEvery(String repeatEvery) {
        this.repeatEvery = repeatEvery;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CampaignMessageRecord)) {
            return false;
        }

        CampaignMessageRecord other = (CampaignMessageRecord) o;

        return Objects.equals(cron, other.cron) && Objects.equals(date, other.date) &&
                Objects.equals(formats, other.formats) && Objects.equals(languages, other.languages) &&
                Objects.equals(messageKey, other.messageKey) && Objects.equals(name, other.name) &&
                equalTimes(other);
    }

    public boolean equalTimes(CampaignMessageRecord other) {
        return Objects.equals(startTime, other.startTime) && Objects.equals(timeOffset, other.timeOffset) &&
                Objects.equals(repeatEvery, other.repeatEvery) && Objects.equals(repeatOn, other.repeatOn);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (formats != null ? formats.hashCode() : 0);
        result = 31 * result + (languages != null ? languages.hashCode() : 0);
        result = 31 * result + (repeatOn != null ? repeatOn.hashCode() : 0);
        result = 31 * result + (messageKey != null ? messageKey.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = timeHash();
        return result;
    }

    private int timeHash() {
        int result = timeOffset != null ? timeOffset.hashCode() : 0;
        result = 31 * result + (cron != null ? cron.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        return result;
    }
}
