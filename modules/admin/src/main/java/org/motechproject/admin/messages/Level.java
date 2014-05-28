package org.motechproject.admin.messages;

import org.codehaus.jackson.annotate.JsonValue;

/**
 * Represents the level of a {@link org.motechproject.admin.domain.StatusMessage}, which is reflected on the UI.
 * Posting a {@link org.motechproject.admin.domain.StatusMessage} with a {@code CRITICAL} level will trigger
 * notifications.
 *
 * @see org.motechproject.admin.domain.StatusMessage
 */
public enum Level {
    CRITICAL("CRITICAL"), ERROR("ERROR"), WARN("WARN"), INFO("INFO"), DEBUG("DEBUG");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    private Level(String value) {
        this.value = value;
    }

    /**
     * Parses the string to create the enum instance. The parse is case-insensitive.
     * @param string The string to be parsed
     * @return The {@link Level} which this string represents or {@code null} if no match is found.
     */
    public static Level fromString(String string) {
        Level result = null;
        if (string != null) {
            for (Level level : Level.values()) {
                if (level.getValue().equals(string.toUpperCase())) {
                    result = level;
                    break;
                }
            }
        }
        return result;
    }

    public boolean containsLevel(Level level) {
        return this.ordinal() >= level.ordinal();
    }
}
