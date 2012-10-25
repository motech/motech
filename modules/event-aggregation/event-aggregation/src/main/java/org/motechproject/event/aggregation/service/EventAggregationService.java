package org.motechproject.event.aggregation.service;

import org.motechproject.event.aggregation.service.impl.AggregationRuleRequest;

public interface EventAggregationService {

    void createRule(AggregationRuleRequest request);
}
