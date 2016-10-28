package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.dto.TaskActionInformationDto;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Value;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Represents an action from a channel. An action is taken upon a task trigger. This class is the representation of the
 * definition from the channel, not the representation of an usage within a task. An action can be represented as an
 * event, but also as a direct OSGi message(or both - a service call with fallback event). It is part of the tasks
 * model.
 */
@Entity(recordHistory = true)
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class TaskActionInformation extends TaskEventInformation {

    private static final long serialVersionUID = -132464255615128442L;
    private static final int MAX_VALUE_LENGTH = 20000;

    @Field
    private String serviceInterface;

    @Field
    private String serviceMethod;

    @Field
    private String specifiedName;

    @Field
    @Value(columns = @Column(length = MAX_VALUE_LENGTH))
    private Map<String, String> values;

    /**
     * Constructor.
     */
    public TaskActionInformation() {
        this(null, null, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param displayName   the task display name
     * @param channelName   the channel name
     * @param moduleName    the module name
     * @param moduleVersion the module version
     * @param subject       the task subject
     */
    public TaskActionInformation(String displayName, String channelName, String moduleName, String moduleVersion,
                                 String subject) {
        this(displayName, channelName, moduleName, moduleVersion, subject, (Map<String, String>) null);
    }

    /**
     * Constructor for an action that is an event sent by the task module.
     *
     * @param displayName   the task display name
     * @param channelName   the channel name
     * @param moduleName    the module name
     * @param moduleVersion the module  version
     * @param subject       the task subject
     * @param values        the map of values
     */
    public TaskActionInformation(String displayName, String channelName, String moduleName, String moduleVersion,
                                 String subject, Map<String, String> values) {
        this(null, null, displayName, channelName, moduleName, moduleVersion, subject, null, null, values);
    }

    /**
     * Constructor for an action that is an OSGi service method call from the tasks module.
     *
     * @param displayName      the task display name
     * @param channelName      the channel name
     * @param moduleName       the module name
     * @param moduleVersion    the module version
     * @param serviceInterface the task service interface
     * @param serviceMethod    the task service method
     */
    public TaskActionInformation(String displayName, String channelName, String moduleName, String moduleVersion,
                                 String serviceInterface, String serviceMethod) {
        this(null, displayName, channelName, moduleName, moduleVersion, serviceInterface, serviceMethod);
    }

    /**
     * Constructor.
     *
     * @param name             the task name
     * @param displayName      the task display name
     * @param channelName      the channel name
     * @param moduleName       the module name
     * @param moduleVersion    the module version
     * @param serviceInterface the task service interface
     * @param serviceMethod    the task service method
     */
    public TaskActionInformation(String name, String displayName, String channelName, String moduleName, String moduleVersion,
                                 String serviceInterface, String serviceMethod) {
        this(name, null, displayName, channelName, moduleName, moduleVersion, null, serviceInterface, serviceMethod, null);
    }

    /**
     * Constructor for a task that is an OSGi service call from the tasks module, but falls back to sending an event if
     * the service is not present
     *
     * @param name             the task name
     * @param specifiedName    the task action specified name
     * @param displayName      the task display name
     * @param channelName      the channel name
     * @param moduleName       the module name
     * @param moduleVersion    the module version
     * @param subject          the task subject
     * @param serviceInterface the task service interface
     * @param serviceMethod    the task service method
     * @param values           the map of values
     */
    public TaskActionInformation(String name, String specifiedName, String displayName, // NO CHECKSTYLE More than 7 parameters (found 9).
                                 String channelName, String moduleName, String moduleVersion, String subject,
                                 String serviceInterface, String serviceMethod, Map<String, String> values) {
        super(name, displayName, channelName, moduleName, moduleVersion, subject);

        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
        this.specifiedName = specifiedName;
        this.values = values == null ? new HashMap<String, String>() : values;
    }

    public boolean hasService() {
        return isNotBlank(serviceInterface) && isNotBlank(serviceMethod);
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values.clear();

        if (values != null) {
            this.values.putAll(values);
        }
    }

    public String getSpecifiedName() {
        return specifiedName;
    }

    public void setSpecifiedName(String specifiedName) {
        this.specifiedName = specifiedName;
    }

    public TaskActionInformationDto toDto() {
        return new TaskActionInformationDto(getName(), getSpecifiedName(), getDisplayName(), getChannelName(), getModuleName(), getModuleVersion(),
                getSubject(), serviceInterface, serviceMethod, values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceInterface, serviceMethod, specifiedName, values);
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

        final TaskActionInformation other = (TaskActionInformation) obj;

        return Objects.equals(this.serviceInterface, other.serviceInterface) &&
                Objects.equals(this.serviceMethod, other.serviceMethod) &&
                Objects.equals(this.specifiedName, other.specifiedName) &&
                Objects.equals(this.values, other.values);
    }

    @Override
    public String toString() {
        return String.format("TaskActionInformation{serviceInterface='%s', serviceMethod='%s', values=%s} %s",
                serviceInterface, serviceMethod, values, super.toString());
    }
}
