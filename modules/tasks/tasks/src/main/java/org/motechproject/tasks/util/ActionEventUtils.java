package org.motechproject.tasks.util;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;

import java.util.Objects;

/**
 * Utility class for checking if the given instance of the {@link TaskActionInformation} class matches the given
 * instance of the {@link ActionEvent} class.
 */
public final class ActionEventUtils {

    /**
     * Checks if the given instance of the {@link TaskActionInformation} class matches the given instance of the
     * {@link ActionEvent} class.
     *
     * @param actionEvent  the instance of the action event
     * @param info  the info about a task action
     * @return true if the given instance matches, false otherwise
     */
    public static boolean accept(ActionEvent actionEvent, TaskActionInformation info) {
        boolean result = false;

        if (null != info.getName() && null != actionEvent.getName()) {
            if (StringUtils.equals(info.getName(), actionEvent.getName())) {
                result = true;
            }
        } else {
            if (actionEvent.hasService() && info.hasService() && equalsService(actionEvent, info)) {
                result = true;
            } else if (actionEvent.hasSubject() && info.hasSubject() && StringUtils.equals(actionEvent.getSubject(), info.getSubject())) {
                result = true;
            }
        }

        return result;
    }

    private static boolean equalsService(ActionEvent actionEvent, TaskActionInformation info) {
        return Objects.equals(actionEvent.getServiceInterface(), info.getServiceInterface()) &&
                Objects.equals(actionEvent.getServiceMethod(), info.getServiceMethod());
    }

    /**
     * Utility class, should not be initiated.
     */
    private ActionEventUtils() {
    }
}
