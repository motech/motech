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
import org.motechproject.tasks.domain.enums.MethodCallManner;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.dto.ActionEventDto;
import org.motechproject.tasks.dto.ActionParameterDto;

import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Represents an action from a channel. An action is taken once a task is triggered. This class is the representation of
 * the definition from the channel, not the representation of an usage within task. An action can be represented as an
 * event, but also as a direct OSGi message(or both - a service call with the event acting as a fallback way of
 * executing the action).
 */
@Entity
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class ActionEvent extends TaskEvent {

    private static final long serialVersionUID = 8362330377208460896L;

    @Field
    @Cascade(delete = true)
    private SortedSet<ActionParameter> actionParameters;

    @Field
    @Cascade(delete = true)
    private SortedSet<ActionParameter> postActionParameters;

    @Field
    private String serviceInterface;

    @Field
    private String serviceMethod;

    @Field
    private MethodCallManner serviceMethodCallManner;

    /**
     * Constructor.
     */
    public ActionEvent() {
        this(null, null, null, null, null, null, MethodCallManner.NAMED_PARAMETERS, null, null);
    }

    /**
     * Constructor.
     */
    public ActionEvent(String name, String description, String displayName, String subject, String serviceInterface, String serviceMethod,
                       MethodCallManner serviceMethodCallManner, SortedSet<ActionParameter> actionParameters) {

        this(name, description, displayName, subject, serviceInterface, serviceMethod, serviceMethodCallManner, actionParameters, null);
    }

    /**
     * Constructor.
     *
     * @param name  the action name
     * @param description  the action description
     * @param displayName  the action display name
     * @param subject  the action event subject
     * @param serviceInterface  the event service interface
     * @param serviceMethod  the event service method
     * @param serviceMethodCallManner  the event service call manner, for supported values see {@see MethodCallManner}
     * @param actionParameters  the action parameters
     * @param postActionParameters  the post action parameters
     */
    public ActionEvent(String name, String description, String displayName, String subject, String serviceInterface, String serviceMethod,
                       MethodCallManner serviceMethodCallManner, SortedSet<ActionParameter> actionParameters,
                       SortedSet<ActionParameter> postActionParameters) {
        super(name, description, displayName, subject);
        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
        this.serviceMethodCallManner = serviceMethodCallManner;
        this.actionParameters = actionParameters == null ? new TreeSet<>() : actionParameters;
        this.postActionParameters = postActionParameters == null ? new TreeSet<>() : postActionParameters;
    }

    public ActionEvent(ActionEvent actionEvent) {
        this(actionEvent.getName(), actionEvent.getDescription(), actionEvent.getDisplayName(),
                actionEvent.getSubject(), actionEvent.getServiceInterface(), actionEvent.getServiceMethod(),
                actionEvent.getServiceMethodCallManner(), copyActionParameters(actionEvent.getActionParameters()),
                copyActionParameters(actionEvent.getPostActionParameters()));
    }

    public boolean accept(TaskActionInformation info) {
        boolean result = false;

        if (null != info.getName() && null != getName()) {
            if (StringUtils.equals(info.getName(), getName())) {
                result = true;
            }
        } else {
            if (hasService() && info.hasService() && equalsService(info.getServiceInterface(), info.getServiceMethod())) {
                result = true;
            } else if (hasSubject() && info.hasSubject() && equalsSubject(info.getSubject())) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean containsParameter(String key) {
        boolean found = false;

        for (ActionParameter param : getActionParameters()) {
            if (equalsIgnoreCase(param.getKey(), key)) {
                found = true;
                break;
            }
        }

        return found;
    }

    public void addParameter(ActionParameter parameter, boolean changeOrder) {
        if (changeOrder) {
            if (actionParameters.isEmpty()) {
                parameter.setOrder(0);
            } else {
                parameter.setOrder(actionParameters.last().getOrder() + 1);
            }
        }

        actionParameters.add(parameter);
    }

    public boolean hasService() {
        return isNotBlank(serviceInterface) && isNotBlank(serviceMethod);
    }

    public SortedSet<ActionParameter> getActionParameters() {
        return actionParameters;
    }

    public void setActionParameters(SortedSet<ActionParameter> actionParameters) {
        this.actionParameters.clear();

        if (actionParameters != null) {
            this.actionParameters.addAll(actionParameters);
        }
    }

    public SortedSet<ActionParameter> getPostActionParameters() {
        return postActionParameters;
    }

    public void setPostActionParameters(SortedSet<ActionParameter> postActionParameters) {
        this.postActionParameters.clear();

        if (postActionParameters != null) {
            this.postActionParameters.addAll(postActionParameters);
        }
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

    public MethodCallManner getServiceMethodCallManner() {
        return serviceMethodCallManner;
    }

    public void setServiceMethodCallManner(MethodCallManner serviceMethodCallManner) {
        this.serviceMethodCallManner = serviceMethodCallManner;
    }

    public ActionEventDto toDto() {
        SortedSet<ActionParameterDto> actionParameterDtos = new TreeSet<>();
        SortedSet<ActionParameterDto> postActionParameterDtos = new TreeSet<>();

        for (ActionParameter actionParameter : actionParameters) {
            actionParameterDtos.add(actionParameter.toDto());
        }
        for (ActionParameter postActionParameter : postActionParameters) {
            postActionParameterDtos.add(postActionParameter.toDto());
        }

        return new ActionEventDto(getName(), getDescription(), getDisplayName(), getSubject(), actionParameterDtos,
                serviceInterface, serviceMethod, serviceMethodCallManner, postActionParameterDtos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionParameters, serviceInterface, serviceMethod);
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

        final ActionEvent other = (ActionEvent) obj;

        return this.actionParametersEquals(other) &&
                equalsService(other.serviceInterface, other.serviceMethod);

    }

    @Override
    public String toString() {
        return String.format("ActionEvent{actionParameters=%s, serviceInterface='%s', serviceMethod='%s'}",
                actionParameters, serviceInterface, serviceMethod);
    }

    private boolean equalsService(String serviceInterface, String serviceMethod) {
        return Objects.equals(this.serviceInterface, serviceInterface) &&
                Objects.equals(this.serviceMethod, serviceMethod);
    }

    private boolean actionParametersEquals(ActionEvent other) {

        Iterator thisActionParametersIterator = this.actionParameters.iterator();
        Iterator otherActionParametersIterator = other.actionParameters.iterator();

        boolean isEqual = this.actionParameters.size() == other.actionParameters.size();

        if (isEqual) {
            ActionParameter currentThisActionParameter;
            ActionParameter currentOtherActionParameter;
            while (isEqual && thisActionParametersIterator.hasNext()) {
                currentThisActionParameter = (ActionParameter) thisActionParametersIterator.next();
                currentOtherActionParameter = (ActionParameter) otherActionParametersIterator.next();
                isEqual = currentThisActionParameter.equals(currentOtherActionParameter);
            }
        }
        return isEqual;
    }

    private static SortedSet<ActionParameter> copyActionParameters(SortedSet<ActionParameter> actionParameters) {
        SortedSet<ActionParameter> copiedActionParameters = new TreeSet<>();
        for (ActionParameter actionParameter : actionParameters) {
            copiedActionParameters.add(new ActionParameter(actionParameter));
        }
        return copiedActionParameters;
    }
}
