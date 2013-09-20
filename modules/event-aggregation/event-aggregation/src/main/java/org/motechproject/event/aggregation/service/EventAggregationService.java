package org.motechproject.event.aggregation.service;

import org.motechproject.event.aggregation.model.rule.AggregationRuleRequest;

public interface EventAggregationService {

    void createRule(AggregationRuleRequest request);
}
