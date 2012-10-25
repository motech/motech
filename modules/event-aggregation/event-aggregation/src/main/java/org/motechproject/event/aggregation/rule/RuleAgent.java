package org.motechproject.event.aggregation.rule;

import org.joda.time.DateTime;
import org.motechproject.event.aggregation.service.AggregatedEvent;
import org.mvel.MVEL;

import java.util.List;

import static org.motechproject.commons.date.util.DateUtil.now;

public class RuleAgent {

    private final String rule;
    private List<? extends AggregatedEvent> events;

    public RuleAgent(String dispatchRule, List<? extends AggregatedEvent> events) {
        this.rule = dispatchRule;
        this.events = events;
    }

    public List<? extends AggregatedEvent> getEvents() {
        return events;
    }

    public AggregatedEvent getFirstEvent() {
        return events.get(0);
    }

    public AggregatedEvent getLastEvent() {
        return events.get(events.size() - 1);
    }

    public DateTime getNow() {
        return now();
    }

    public boolean execute() {
        return (Boolean) MVEL.eval(rule, this);
    }
}
