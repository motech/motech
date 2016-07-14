package org.motechproject.tasks.validation;

import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.ActionParameter;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Utility class for validating channels.
 */
public final class ChannelValidator extends GeneralValidator {
    public static final String CHANNEL = "channel";

    /**
     * Utility class, should not be instantiated.
     */
    private ChannelValidator() {
    }

    /**
     * Validates the given channel by checking if all necessary data is set. Returns the set of {@code TaskError}s
     * containing information about missing fields.
     *
     * @param channel  the channel to be validated, not null
     * @return  the set of encountered errors
     */
    public static Set<TaskError> validate(Channel channel) {
        Set<TaskError> errors = new HashSet<>();

        checkBlankValue(errors, CHANNEL, "displayName", channel.getDisplayName());
        checkBlankValue(errors, CHANNEL, "moduleName", channel.getModuleName());
        checkBlankValue(errors, CHANNEL, "moduleVersion", channel.getModuleVersion());

        checkVersion(errors, CHANNEL, "moduleVersion", channel.getModuleVersion());

        boolean containsTriggers = !isEmpty(channel.getTriggerTaskEvents());
        boolean containsActions = !isEmpty(channel.getActionTaskEvents());

        if (!containsTriggers && !containsActions) {
            errors.add(new TaskError("task.validation.error.channel"));
        } else {
            if (containsTriggers) {
                for (int i = 0; i < channel.getTriggerTaskEvents().size(); ++i) {
                    errors.addAll(validateTrigger(i, channel.getTriggerTaskEvents().get(i)));
                }
            }

            if (containsActions) {
                for (int i = 0; i < channel.getActionTaskEvents().size(); ++i) {
                    errors.addAll(validateAction(i, channel.getActionTaskEvents().get(i)));
                }
            }
        }

        return errors;
    }

    private static Set<TaskError> validateTrigger(int index, TriggerEvent trigger) {
        Set<TaskError> errors = new HashSet<>();
        String field = "triggerTaskEvents[" + index + "]";

        checkNullValue(errors, CHANNEL, field, trigger);

        if (isEmpty(errors)) {
            String objectName = CHANNEL + "." + field;

            checkBlankValue(errors, objectName, "displayName", trigger.getDisplayName());
            checkBlankValue(errors, objectName, "subject", trigger.getSubject());

            for (int i = 0; i < trigger.getEventParameters().size(); ++i) {
                errors.addAll(validateEventParameter(objectName, "eventParameters[" + i + "]", trigger.getEventParameters().get(i)));
            }
        }

        return errors;
    }

    private static Set<TaskError> validateAction(int index, ActionEvent action) {
        Set<TaskError> errors = new HashSet<>();
        String field = "actionTaskEvents[" + index + "]";

        checkNullValue(errors, CHANNEL, field, action);

        if (isEmpty(errors)) {
            String objectName = CHANNEL + "." + field;

            checkBlankValue(errors, objectName, "displayName", action.getDisplayName());

            if (!action.hasSubject() && !action.hasService()) {
                errors.add(new TaskError("task.validation.error.channelAction"));
            }

            for (ActionParameter parameter : action.getActionParameters()) {
                errors.addAll(validateActionParameter(objectName, "actionParameters[" + parameter.getOrder() + "]", parameter));
            }

            for (ActionParameter parameter: action.getPostActionParameters()) {
                errors.addAll(validateActionParameter(objectName, "postActionParameters[" + parameter.getOrder() + "]", parameter));
            }
        }

        return errors;
    }

}
