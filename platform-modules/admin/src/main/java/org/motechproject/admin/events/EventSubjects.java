package org.motechproject.admin.events;

/**
 * A class grouping constants representing subjects from events published by the admin module.
 */
public final class EventSubjects {

    /**
     * This is the subject used for publishing status messages. The admin module listens to events
     * with this subject and publishes status messages based on their payload. This allows publishing
     * status messages without a dependency on the admin module.
     *
     * @see org.motechproject.admin.domain.StatusMessage
     */
    public static final String MESSAGE_SUBJECT = "org.motechproject.message";

    private EventSubjects() {
    }
}
