package org.motechproject.scheduler.contract;

import java.util.ArrayList;
import java.util.List;

/**
 * JobDetailedInfo is the class which wraps the EventInfo list.
 *
 * @see EventInfo
 */

public class JobDetailedInfo {
    private List<EventInfo> eventInfoList;

    public JobDetailedInfo() {
        eventInfoList = new ArrayList<>();
    }

    /**
     * Constructor.
     *
     * @param eventInfoList  the list of information about event, not null
     */
    public JobDetailedInfo(List<EventInfo> eventInfoList) {
        if (eventInfoList == null) {
            throw new IllegalArgumentException("Argument can't be null!");
        }
        this.eventInfoList = eventInfoList;
    }

    public List<EventInfo> getEventInfoList() {
        return eventInfoList;
    }

    public void setEventInfoList(List<EventInfo> eventInfoList) {
        this.eventInfoList = eventInfoList;
    }
}
