package org.motechproject.tasks.util;

import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.Channel;

/**
 * Utility class for fetching and verifying if the given instance of the {@link Channel} contains an action matching the
 * given instance of the {@link TaskActionInformation} class.
 */
public final class ChannelUtils {

    /**
     * Checks whether the given instance of the {@link Channel} class contains an action matching the given instance of
     * the {@link TaskActionInformation} class.
     *
     * @param channel  the channel
     * @param actionInformation  the information about an action to be found
     * @return true if the channel contains matching action, false otherwise
     */
    public static boolean containsAction(Channel channel, TaskActionInformation actionInformation) {
        boolean found = false;

        for (ActionEvent action : channel.getActionTaskEvents()) {
            if (ActionEventUtils.accept(action, actionInformation)) {
                found = true;
                break;
            }
        }

        return found;
    }

    /**
     * Returns an instance of the {@link ActionEvent} class matching the information passed as the instance of the
     * {@link TaskActionInformation} class. If the channel doesn't contain the action, null will be returned.
     *
     * @param channel  the channel
     * @param actionInformation  the information about an action to be returned
     * @return the matching action, null if the action doesn't exist
     */
    public static ActionEvent getAction(Channel channel, TaskActionInformation actionInformation) {
        ActionEvent found = null;

        for (ActionEvent action : channel.getActionTaskEvents()) {
            if (ActionEventUtils.accept(action, actionInformation)) {
                found = action;
                break;
            }
        }

        return found;
    }

    /**
     * Utility class, should not be initiated.
     */
    private ChannelUtils() {
    }
}
