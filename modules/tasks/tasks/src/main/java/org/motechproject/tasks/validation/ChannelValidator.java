package org.motechproject.tasks.validation;

import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.domain.TriggerEvent;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public final class ChannelValidator extends GeneralValidator {
    public static final String CHANNEL = "channel";

    private ChannelValidator() {
    }

    public static Set<TaskError> validate(Channel channel) {
        Set<TaskError> errors = new HashSet<>();

        checkBlankValue(errors, CHANNEL, "displayName", channel.getDisplayName());
        checkBlankValue(errors, CHANNEL, "moduleName", channel.getModuleName());
        checkBlankValue(errors, CHANNEL, "moduleVersion", channel.getModuleVersion());

        checkVersion(errors, CHANNEL, "moduleVersion", channel.getModuleVersion());

        boolean containsTriggers = !isEmpty(channel.getTriggerTaskEvents());
        boolean containsActions = !isEmpty(channel.getActionTaskEvents());

        if (!containsTriggers && !containsActions) {
            errors.add(new TaskError("validation.error.channel"));
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
                errors.add(new TaskError("validation.error.channelAction"));
            }

            for (ActionParameter parameter : action.getActionParameters()) {
                errors.addAll(validateActionParameter(objectName, "actionParameters[" + parameter.getOrder() + "]", parameter));
            }
        }

        return errors;
    }

}
