package org.motechproject.tasks.events.constants;

public enum TaskFailureCause {
    TRIGGER, FILTER, DATA_SOURCE, ACTION;

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", "");
    }
}
