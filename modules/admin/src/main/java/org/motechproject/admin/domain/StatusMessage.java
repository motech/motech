package org.motechproject.admin.domain;

import org.joda.time.DateTime;
import org.motechproject.admin.messages.Level;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.util.SecurityMode;

/**
 * Represents a message displayed in the 'messages' section of the Admin UI. Persisted by MDS.
 * Apart from the message and its {@link Level}, it contains also information about the module that
 * sent the message. The timeout field represents the {@link DateTime} of the message expiration.
 * Status messages are matched against notification rules.
 *
 * @see org.motechproject.admin.domain.NotificationRule
 */
@Entity(nonEditable = true)
@Access(value = SecurityMode.PERMISSIONS, members = { "manageMessages" })
public class StatusMessage {

    @Field(required = true, type = "text")
    private String text;

    @Field(required = true)
    private String moduleName;

    @Field
    private DateTime date;

    @Field
    private DateTime timeout;

    @Field(required = true, defaultValue = "INFO")
    private Level level;

    /**
     * Constructor. Defaults the level of this message to INFO and the expiration date to 60 minutes from now.
     */
    public StatusMessage() {
        this(null, null, Level.INFO);
    }

    /**
     * Constructor. Defaults the expiration date to 60 minutes from now.
     *
     * @param text the message content
     * @param moduleName the name of the module to which this message relates to
     * @param level the message level
     */
    public StatusMessage(String text, String moduleName, Level level) {
        this.text = text;
        this.moduleName = moduleName;
        this.level = level;
        this.date = DateTime.now();
        this.timeout = DateTime.now().plusMinutes(60);
    }

    /**
     * Constructor.
     *
     * @param text the message content
     * @param moduleName the name of the module to which this message relates to
     * @param level the message level
     * @param timeout the message expiry date
     */
    public StatusMessage(String text, String moduleName, Level level, DateTime timeout) {
        this(text, moduleName, level);
        this.timeout = timeout;
    }

    /**
     * @return the message content
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the message content
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the name of the module to which this message relates to
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * @param moduleName the name of the module to which this message relates to
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the date and time at which this message was published
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * @param date the date and time at which this message was published
     */
    public void setDate(DateTime date) {
        this.date = date;
    }

    /**
     * @return the message expiry date
     */
    public DateTime getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the message expiry date
     */
    public void setTimeout(DateTime timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the level of the message
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @param level the level of the message
     */
    public void setLevel(Level level) {
        this.level = level;
    }
}
