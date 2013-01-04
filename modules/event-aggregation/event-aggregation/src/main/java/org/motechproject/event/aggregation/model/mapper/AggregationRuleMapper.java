package org.motechproject.event.aggregation.model.mapper;

import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.service.AggregationRuleRequest;

public class AggregationRuleMapper {
    
    public AggregationRuleRecord toRecord(AggregationRuleRequest request) {
        AggregationRuleRecord record = new AggregationRuleRecord();
        record.setName(request.getName());
        record.setDescription(request.getDescription());
        record.setSubscribedTo(request.getSubscribedTo());
        record.setFields(request.getFields());
        record.setAggregationSchedule(new AggregationScheduleMapper().toRecord(request.getAggregationSchedule()));
        record.setPublishAs(request.getPublishAs());
        record.setState(request.getState());
        return record;
    }

    public AggregationRuleRequest toRequest(AggregationRuleRecord record) {
        AggregationRuleRequest request = new AggregationRuleRequest();
        request.setName(record.getName());
        request.setDescription(record.getDescription());
        request.setSubscribedTo(record.getSubscribedTo());
        request.setFields(record.getFields());
        request.setAggregationSchedule(new AggregationScheduleMapper().toRequest(record.getAggregationSchedule()));
        request.setPublishAs(record.getPublishAs());
        request.setState(record.getState());
        return request;
    }
}
