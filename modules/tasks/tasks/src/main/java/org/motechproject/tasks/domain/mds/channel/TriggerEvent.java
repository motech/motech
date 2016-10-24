package org.motechproject.tasks.domain.mds.channel;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.dto.EventParameterDto;
import org.motechproject.tasks.dto.TriggerEventDto;

import javax.jdo.annotations.Persistent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

/**
 * Represents a single trigger event. Trigger event is an event that triggers executions of a task. It is a part of the
 * channel model.
 */
@Entity
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class TriggerEvent extends TaskEvent {

    private static final long serialVersionUID = 4235157487991610105L;

    @Field
    @Persistent(defaultFetchGroup = "false")
    private Channel channel;

    @Field
    @Cascade(delete = true)
    @Persistent(mappedBy = "triggerEvent")
    private List<EventParameter> eventParameters;

    @Field
    private String triggerListenerSubject;

    /**
     * Class constructor.
     */
    public TriggerEvent() {
        this(null, null, null, null, null);
    }

    /**
     * Class constructor.
     *
     * @param displayName  the event display name
     * @param subject  the event subject
     * @param description  the event description
     * @param eventParameters the event parameters
     * @param triggerListenerSubject  the subject that is wrapped by this trigger, in a simple case it is identical to
     *                                the subject above, so it can be omitted
     */
    public TriggerEvent(String displayName, String subject, String description, List<EventParameter> eventParameters,
                        String triggerListenerSubject) {
        super(description, displayName, subject);
        this.eventParameters = eventParameters == null ? new ArrayList<>() : eventParameters;
        this.triggerListenerSubject = StringUtils.isEmpty(triggerListenerSubject) ? subject : triggerListenerSubject;
    }

    /**
     * Copy constructor.
     *
     * @param triggerEvent  the object to be copied
     */
    public TriggerEvent(TriggerEvent triggerEvent) {
        this(triggerEvent.getDisplayName(), triggerEvent.getSubject(), triggerEvent.getDescription(),
                copyEventParameters(triggerEvent.getEventParameters()), triggerEvent.getTriggerListenerSubject());
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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
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

    public TriggerEventDto toDto() {
        List<EventParameterDto> eventParameterDtos = new ArrayList<>();

        for (EventParameter eventParameter : eventParameters) {
            eventParameterDtos.add(eventParameter.toDto());
        }
        return new TriggerEventDto(getName(), getDescription(), getDisplayName(), getSubject(), eventParameterDtos, triggerListenerSubject);
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

    private static List<EventParameter> copyEventParameters(List<EventParameter> sourceEventParameters) {
        List<EventParameter> eventParameters = new ArrayList<>();
        for (EventParameter sourceEventParameter : sourceEventParameters) {
            eventParameters.add(new EventParameter(sourceEventParameter));
        }
        return eventParameters;
    }
}
