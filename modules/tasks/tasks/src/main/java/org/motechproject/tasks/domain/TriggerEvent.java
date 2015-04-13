package org.motechproject.tasks.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.tasks.contract.EventParameterRequest;
import org.motechproject.tasks.contract.TriggerEventRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

/**
 * The <code>TriggerEvent</code> class is responsible for storing data about event
 */
@Entity
@CrudEvents(CrudEventType.NONE)
public class TriggerEvent extends TaskEvent {
    private static final long serialVersionUID = 4235157487991610105L;

    @Field
    @Cascade(delete = true)
    private List<EventParameter> eventParameters;

    @Field
    private String triggerListenerSubject;

    public TriggerEvent() {
        this(null, null, null, null, null);
    }

    public TriggerEvent(String displayName, String subject, String description, List<EventParameter> eventParameters, String triggerListenerSubject) {
        super(description, displayName, subject);
        this.eventParameters = eventParameters == null ? new ArrayList<EventParameter>() : eventParameters;
        this.triggerListenerSubject = StringUtils.isEmpty(triggerListenerSubject) ? subject : triggerListenerSubject;
    }

    public TriggerEvent(TriggerEventRequest triggerEventRequest) {
        this(triggerEventRequest.getDisplayName(), triggerEventRequest.getSubject(), triggerEventRequest.getDescription(),
                getEventParametersForTriggerEvent(triggerEventRequest), triggerEventRequest.getTriggerListenerSubject());
    }

    private static List<EventParameter> getEventParametersForTriggerEvent(TriggerEventRequest triggerEventRequest) {
        List<EventParameter> parameters = new ArrayList<>();
        for (EventParameterRequest eventParameterRequest : triggerEventRequest.getEventParameters()) {
            parameters.add(new EventParameter(eventParameterRequest));
        }
        return parameters;
    }

    @Override
    public boolean containsParameter(String key) {
        boolean found = false;

        for (EventParameter param : getEventParameters()) {
            if (equalsIgnoreCase(param.getEventKey(), key)) {
                found = true;
                break;
            }
        }

        return found;
    }

    public List<EventParameter> getEventParameters() {
        return eventParameters;
    }

    public String getTriggerListenerSubject() {
        return triggerListenerSubject;
    }

    public void setEventParameters(List<EventParameter> eventParameters) {
        this.eventParameters.clear();

        if (eventParameters != null) {
            this.eventParameters.addAll(eventParameters);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventParameters);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final TriggerEvent other = (TriggerEvent) obj;

        return Objects.equals(this.eventParameters, other.eventParameters);
    }

    @Override
    public String toString() {
        return String.format("TriggerEvent{eventParameters=%s}", eventParameters);
    }

    public String getKeyType(String key) {
        String type = "UNKNOWN";

        for (EventParameter param : getEventParameters()) {
            if (equalsIgnoreCase(param.getEventKey(), key)) {
                type = param.getType().toString();
                break;
            }
        }

        return type;
    }

    public TriggerEvent copy() {
        List<EventParameter> eventParametersCopy = new ArrayList<>();

        for (EventParameter eventParameter : getEventParameters()) {
            eventParametersCopy.add(new EventParameter(eventParameter.getDisplayName(), eventParameter.getEventKey(),
                    eventParameter.getType()));
        }

        return new TriggerEvent(getDisplayName(), getSubject(), getDescription(), eventParametersCopy,
                getTriggerListenerSubject());
    }
}
