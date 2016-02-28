package org.motechproject.tasks.constants;

/**
 * Enumeration class representing the potential causes of task failure.
 */
public enum TaskFailureCause {
    TRIGGER, FILTER, DATA_SOURCE, ACTION;

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", "");
    }
}
