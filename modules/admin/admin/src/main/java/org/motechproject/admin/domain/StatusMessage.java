package org.motechproject.admin.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.admin.messages.Level;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import static org.motechproject.commons.date.util.DateUtil.setTimeZoneUTC;

/**
 * Represents a message displayed in the 'messages' section of the Admin UI. Persisted in CouchDB.
 * Apart from the message and its {@link Level}, contains also information about the module that
 * sent the message. The timeout field represents the {@link DateTime} of the message expiration.
 */
@TypeDiscriminator("doc.type === 'StatusMessage'")
public class StatusMessage extends MotechBaseDataObject {

    private static final long serialVersionUID = 2159913276267748034L;

    private static final int DEFAULT_TIMEOUT_MINS = 60;

    private String text;
    private String moduleName;
    private DateTime date = DateTime.now();
    private DateTime timeout = DateTime.now().plusMinutes(DEFAULT_TIMEOUT_MINS);
    private Level level = Level.INFO;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DateTime getTimeout() {
        return setTimeZoneUTC(timeout);
    }

    public void setTimeout(DateTime timeout) {
        this.timeout = timeout;
    }

    public DateTime getDate() {
        return setTimeZoneUTC(date);
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public StatusMessage() {
        // default constructor
    }

    public StatusMessage(String text) {
        this.text = text;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public StatusMessage(String text, String moduleName, Level level) {
        this.text = text;
        this.moduleName = moduleName;
        this.level = level;
    }


    public StatusMessage(String text, String moduleName, Level level, DateTime timeout) {
        this(text, moduleName, level);
        this.timeout = timeout;
    }

    public void setTimeoutAfter(int minutes) {
        timeout = DateTime.now().plusMinutes(minutes);
    }
}
