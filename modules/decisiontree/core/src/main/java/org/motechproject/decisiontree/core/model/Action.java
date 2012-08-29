/**
 * \ingroup decisionTree
 */
package org.motechproject.decisiontree.core.model;

/**
 * Represents an action that can be associated with a node to raise events.
 */
public class Action {
    private String eventId;

    /**
     * Builder for building an Action instance. See @{link Action}
     */
    public static class Builder {
        private Action obj;

        public Builder() {
            obj = new Action();
        }

        /**
         * Returns the Action instance built by the builder instance with the given credentials. [Here with eventID (if setEventId method is called)]
         * @return Action instance built by the builder
         */
        public Action build() {
            return obj;
        }

        /**
         * Sets event id for the Action instance to be built
         * @param eventId event id of the action
         * @return instance of the current builder for Action
         */
        public Builder setEventId(String eventId) {
            obj.eventId = eventId;
            return this;
        }
    }

    /**
     * Returns a builder to build an action. see @{link Action.Builder}
     * @return A builder instance
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "Action{" +
                "eventId='" + eventId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Action action = (Action) o;

        if (eventId != null ? !eventId.equals(action.eventId) : action.eventId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return eventId != null ? eventId.hashCode() : 0;
    }
}
