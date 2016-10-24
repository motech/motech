package org.motechproject.tasks.web.domain;

import org.motechproject.tasks.web.ChannelController;
import org.motechproject.tasks.dto.TriggerEventDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of the response returned by the {@link ChannelController}. Contains a list of triggers and information if there
 * is more triggers to be displayed.
 */
public class TriggersList {

    private List<TriggerEventDto> triggers;
    private int page;
    private int total;

    public TriggersList() {
        this.triggers = new ArrayList<>();
    }

    public List<TriggerEventDto> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<TriggerEventDto> triggers) {
        this.triggers = triggers;
    }

    public void addTriggers(List<TriggerEventDto> triggers) {
        this.triggers.addAll(triggers);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
