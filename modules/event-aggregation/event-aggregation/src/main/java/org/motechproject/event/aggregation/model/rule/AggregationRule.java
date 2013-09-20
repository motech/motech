package org.motechproject.event.aggregation.model.rule;

import org.motechproject.event.aggregation.model.schedule.AggregationSchedule;

import java.util.List;

public interface AggregationRule {

    String getName();
    String getDescription();
    String getSubscribedTo();
    List<String> getFields();
    AggregationSchedule getAggregationSchedule();
    String getPublishAs();
    AggregationState getState();
}
