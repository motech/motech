package org.motechproject.admin.domain;

import org.joda.time.DateTime;
import org.motechproject.admin.messages.Level;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import static org.motechproject.commons.date.util.DateUtil.setTimeZoneUTC;

/**
 * Represents a message displayed in the 'messages' section of the Admin UI. Persisted by MDS module.
 * Apart from the message and its {@link Level}, contains also information about the module that
 * sent the message. The timeout field represents the {@link DateTime} of the message expiration.
 */
@Entity
public class StatusMessage {
    @Field(required = true)
    private String text;

    @Field(required = true)
    private String moduleName;

    @Field
    private DateTime date;

    @Field
    private DateTime timeout;

    @Field(required = true, defaultValue = "INFO")
    private Level level;

    public StatusMessage() {
        this(null, null, Level.INFO);
    }

    public StatusMessage(String text, String moduleName, Level level) {
        this.text = text;
        this.moduleName = moduleName;
        this.level = level;
        this.date = DateTime.now();
        this.timeout = DateTime.now().plusMinutes(60);
    }

    public StatusMessage(String text, String moduleName, Level level, DateTime timeout) {
        this(text, moduleName, level);
        this.timeout = timeout;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public DateTime getDate() {
        return setTimeZoneUTC(date);
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public DateTime getTimeout() {
        return setTimeZoneUTC(timeout);
    }

    public void setTimeout(DateTime timeout) {
        this.timeout = timeout;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
