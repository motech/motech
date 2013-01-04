package org.motechproject.event.aggregation.model.mapper;

import org.motechproject.event.aggregation.model.schedule.AggregationScheduleRecord;
import org.motechproject.event.aggregation.model.schedule.CronBasedAggregationRecord;
import org.motechproject.event.aggregation.model.schedule.CustomAggregationRecord;
import org.motechproject.event.aggregation.model.schedule.PeriodicAggregationRecord;
import org.motechproject.event.aggregation.service.AggregationScheduleRequest;
import org.motechproject.event.aggregation.service.CronBasedAggregationRequest;
import org.motechproject.event.aggregation.service.CustomAggregationRequest;
import org.motechproject.event.aggregation.service.PeriodicAggregationRequest;

public class AggregationScheduleMapper {

    public AggregationScheduleRecord toRecord(AggregationScheduleRequest request) {
        if (request instanceof PeriodicAggregationRequest) {
            return newPeriodicAggregationRecord((PeriodicAggregationRequest) request);
        } else if (request instanceof CronBasedAggregationRequest) {
            return newCronBasedAggregationRecord((CronBasedAggregationRequest) request);
        } else if (request instanceof CustomAggregationRequest) {
            return newCustomAggregationRecord((CustomAggregationRequest) request);
        }
        return null;
    }

    private CustomAggregationRecord newCustomAggregationRecord(CustomAggregationRequest request) {
        CustomAggregationRecord record = new CustomAggregationRecord();
        record.setRule(request.getRule());
        return record;
    }

    private CronBasedAggregationRecord newCronBasedAggregationRecord(CronBasedAggregationRequest request) {
        CronBasedAggregationRecord record = new CronBasedAggregationRecord();
        record.setCronExpression(request.getCronExpression());
        return record;
    }

    private PeriodicAggregationRecord newPeriodicAggregationRecord(PeriodicAggregationRequest request) {
        PeriodicAggregationRecord record = new PeriodicAggregationRecord();
        record.setPeriod(request.getPeriod());
        record.setStartDate(request.getStartTime());
        return record;
    }

    public AggregationScheduleRequest toRequest(AggregationScheduleRecord record) {
        if (record instanceof PeriodicAggregationRecord) {
            return newPeriodicAggregationRequest((PeriodicAggregationRecord) record);
        } else if (record instanceof CronBasedAggregationRecord) {
            return newCronBasedAggregationRequest((CronBasedAggregationRecord) record);
        } else if (record instanceof CustomAggregationRecord) {
            return newCustomAggregationRequest((CustomAggregationRecord) record);
        }
        return null;
    }

    private CustomAggregationRequest newCustomAggregationRequest(CustomAggregationRecord request) {
        CustomAggregationRequest record = new CustomAggregationRequest();
        record.setRule(request.getRule());
        return record;
    }

    private CronBasedAggregationRequest newCronBasedAggregationRequest(CronBasedAggregationRecord request) {
        CronBasedAggregationRequest record = new CronBasedAggregationRequest();
        record.setCronExpression(request.getCronExpression());
        return record;
    }

    private PeriodicAggregationRequest newPeriodicAggregationRequest(PeriodicAggregationRecord request) {
        PeriodicAggregationRequest record = new PeriodicAggregationRequest();
        record.setStartTimeInMillis(request.getStartTime());
        record.setPeriod(request.getPeriod());
        return record;
    }
}
