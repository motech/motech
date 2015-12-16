package org.motechproject.tasks.domain;

import org.motechproject.tasks.web.ChannelController;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of the response returned by the {@link ChannelController}. Contains a list of triggers and information if there
 * is more triggers to be displayed.
 */
public class TriggersList {

    private List<TriggerEvent> triggers;

    private boolean hasNextPage;

    public TriggersList() {
        this.hasNextPage = false;
        this.triggers = new ArrayList<>();
    }

    public List<TriggerEvent> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<TriggerEvent> triggers) {
        this.triggers = triggers;
    }

    public void addTriggers(List<TriggerEvent> triggers) {
        this.triggers.addAll(triggers);
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}
