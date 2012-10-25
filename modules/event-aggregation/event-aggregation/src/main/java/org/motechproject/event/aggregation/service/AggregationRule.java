package org.motechproject.event.aggregation.service;

import org.motechproject.event.aggregation.model.AggregationState;

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
