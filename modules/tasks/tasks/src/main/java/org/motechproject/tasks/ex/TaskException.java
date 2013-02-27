package org.motechproject.tasks.ex;

import java.util.Arrays;

public class TaskException extends Exception {
    private static final long serialVersionUID = 5637713166842511095L;

    private String messageKey;
    private String[] fields;

    public TaskException(String messageKey) {
        this(messageKey, (Throwable) null);
    }

    public TaskException(String messageKey, Throwable cause) {
        super(messageKey, cause);

        this.messageKey = messageKey;
        this.fields = new String[0];
    }

    public TaskException(String messageKey, String... fields) {
        this(messageKey, null, fields);
    }

    public TaskException(String messageKey, Throwable cause, String... fields) {
        super(messageKey, cause);

        this.messageKey = messageKey;
        this.fields = Arrays.copyOf(fields, fields.length);
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String[] getFields() {
        return Arrays.copyOf(fields, fields.length);
    }
}
