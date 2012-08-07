package org.motechproject.admin.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.admin.messages.Level;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'StatusMessage'")
public class StatusMessage extends MotechBaseDataObject {

    private static final int DEFAULT_TIMEOUT_MINS = 60;

    private String text;
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
        return timeout;
    }

    public void setTimeout(DateTime timeout) {
        this.timeout = timeout;
    }

    public DateTime getDate() {
        return date;
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

    public StatusMessage(String text, Level level) {
        this.text = text;
        this.level = level;
    }


    public StatusMessage(String text, Level level, DateTime timeout) {
        this(text, level);
        this.timeout = timeout;
    }

    public void setTimeoutAfter(int minutes) {
        timeout = DateTime.now().plusMinutes(minutes);
    }
}
