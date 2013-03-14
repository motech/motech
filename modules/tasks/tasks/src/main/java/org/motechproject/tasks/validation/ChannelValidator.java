package org.motechproject.tasks.validation;

import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TriggerEvent;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public final class ChannelValidator extends GeneralValidator {
    public static final String CHANNEL = "channel";

    private ChannelValidator() {
    }

    public static ValidationResult validate(Channel channel) {
        ValidationResult result = new ValidationResult();

        result.addError(checkBlankValue(CHANNEL, "displayName", channel.getDisplayName()));
        result.addError(checkBlankValue(CHANNEL, "moduleName", channel.getModuleName()));
        result.addError(checkBlankValue(CHANNEL, "moduleVersion", channel.getModuleVersion()));

        result.addError(checkVersion(CHANNEL, "moduleVersion", channel.getModuleVersion()));

        boolean containsTriggers = !isEmpty(channel.getTriggerTaskEvents());
        boolean containsActions = !isEmpty(channel.getActionTaskEvents());

        if (!containsTriggers && !containsActions) {
            result.addError(new CustomTaskError("validation.error.channel"));
        } else {
            if (containsTriggers) {
                for (int i = 0; i < channel.getTriggerTaskEvents().size(); ++i) {
                    result.addErrors(validateTrigger(i, channel.getTriggerTaskEvents().get(i)));
                }
            }

            if (containsActions) {
                for (int i = 0; i < channel.getActionTaskEvents().size(); ++i) {
                    result.addErrors(validateAction(i, channel.getActionTaskEvents().get(i)));
                }
            }
        }

        return result;
    }

    private static ValidationResult validateTrigger(int index, TriggerEvent trigger) {
        ValidationResult result = new ValidationResult();
        String field = "triggerTaskEvents[" + index + "]";

        result.addError(checkNullValue(CHANNEL, field, trigger));

        if (result.isValid()) {
            String objectName = CHANNEL + "." + field;

            result.addError(checkBlankValue(objectName, "displayName", trigger.getDisplayName()));
            result.addError(checkBlankValue(objectName, "subject", trigger.getSubject()));

            for (int i = 0; i < trigger.getEventParameters().size(); ++i) {
                result.addErrors(validateEventParameter(objectName, "eventParameters[" + i + "]", trigger.getEventParameters().get(i)));
            }
        }

        return result;
    }

    private static ValidationResult validateAction(int index, ActionEvent action) {
        ValidationResult result = new ValidationResult();
        String field = "actionTaskEvents[" + index + "]";

        result.addError(checkNullValue(CHANNEL, field, action));

        if (result.isValid()) {
            String objectName = CHANNEL + "." + field;

            result.addError(checkBlankValue(objectName, "displayName", action.getDisplayName()));

            if (!action.hasSubject() && !action.hasService()) {
                result.addError(new CustomTaskError("validation.error.channelAction"));
            }

            for (ActionParameter parameter : action.getActionParameters()) {
                result.addErrors(validateActionParameter(objectName, "actionParameters[" + parameter.getOrder() + "]", parameter));
            }
        }

        return result;
    }

}
