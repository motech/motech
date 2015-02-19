package org.motechproject.admin.events;

/**
 * A class grouping constants representing the keys in parameter maps from events published by the admin module.
 */
public final class EventKeys {

    /**
     * Used for publishing status messages. This is the message content for the message that will be published.
     * @see org.motechproject.admin.domain.StatusMessage#setText(String)
     */
    public static final String MESSAGE = "message";

    /**
     * Used for publishing status messages. This is the message level in String form(must match the value from the
     * level enum).
     * @see org.motechproject.admin.domain.StatusMessage#setLevel(org.motechproject.admin.messages.Level)
     * @see org.motechproject.admin.messages.Level
     */
    public static final String LEVEL = "level";

    /**
     * Used for publishing status messages. This is the message timeout. The type of this field is {@link org.joda.time.DateTime}
     * @see org.motechproject.admin.domain.StatusMessage#setTimeout(org.joda.time.DateTime)
     */
    public static final String TIMEOUT = "timeout";

    /**
     * Used for publishing status messages. This is the name of the module this is message is related to.
     * @see org.motechproject.admin.domain.StatusMessage#setModuleName(String)
     */
    public static final String MODULE_NAME = "moduleName";

    private EventKeys() {
    }
}
