package org.motechproject.tasks.web.domain;

import org.motechproject.tasks.web.ChannelController;

/**
 * Response returned by the {@link ChannelController}. Contains list of both static and dynamic triggers provided by a
 * single channel.
 */
public class TriggersLists {

    private TriggersList staticTriggersList;

    private TriggersList dynamicTriggersList;

    public TriggersLists(TriggersList staticTriggersList, TriggersList dynamicTriggersList) {
        this.staticTriggersList = staticTriggersList;
        this.dynamicTriggersList = dynamicTriggersList;
    }

    public TriggersList getStaticTriggersList() {
        return staticTriggersList;
    }

    public void setStaticTriggersList(TriggersList staticTriggersList) {
        this.staticTriggersList = staticTriggersList;
    }

    public TriggersList getDynamicTriggersList() {
        return dynamicTriggersList;
    }

    public void setDynamicTriggersList(TriggersList dynamicTriggersList) {
        this.dynamicTriggersList = dynamicTriggersList;
    }
}
