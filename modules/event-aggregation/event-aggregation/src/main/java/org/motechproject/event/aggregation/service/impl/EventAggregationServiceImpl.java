package org.motechproject.event.aggregation.service.impl;

import org.motechproject.event.aggregation.aggregate.EventAggregator;
import org.motechproject.event.aggregation.model.mapper.AggregationRuleMapper;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.model.event.PeriodicDispatchEvent;
import org.motechproject.event.aggregation.model.event.SporadicDispatchEvent;
import org.motechproject.event.aggregation.repository.AllAggregatedEvents;
import org.motechproject.event.aggregation.repository.AllAggregationRules;
import org.motechproject.event.aggregation.service.EventAggregationService;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.motechproject.commons.date.util.DateUtil.now;

@Service
public class EventAggregationServiceImpl implements EventAggregationService {

    private AllAggregationRules allAggregationRules;
    private EventListenerRegistryService eventListenerRegistryService;
    private AllAggregatedEvents allAggregatedEvents;
    private MotechSchedulerService schedulerService;

    public static final int MILLIS_IN_A_SEC = 1000;
    private static final long MILLIS_IN_A_MINUTE = 60 * 1000;
    private AggregationRuleMapper aggregationRuleMapper;

    @Autowired
    public EventAggregationServiceImpl(AllAggregationRules allAggregationRules, EventListenerRegistryService eventListenerRegistryService, AllAggregatedEvents allAggregatedEvents, MotechSchedulerService schedulerService) {
        this.allAggregationRules = allAggregationRules;
        this.eventListenerRegistryService = eventListenerRegistryService;
        this.allAggregatedEvents = allAggregatedEvents;
        this.schedulerService = schedulerService;
        this.aggregationRuleMapper = new AggregationRuleMapper();
    }

    @Override
    public void createRule(AggregationRuleRequest request) {
        AggregationRuleRecord aggregationRule = aggregationRuleMapper.toRecord(request);
        allAggregationRules.addOrReplace(aggregationRule);
        eventListenerRegistryService.registerListener(new EventAggregator(request.getName(), request.getFields(), allAggregatedEvents, allAggregationRules), request.getSubscribedTo());

        AggregationScheduleRequest aggregationSchedule = request.getAggregationSchedule();
        if (aggregationSchedule instanceof PeriodicAggregationRequest) {
            PeriodicAggregationRequest periodicSchedule = (PeriodicAggregationRequest) aggregationSchedule;
            schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(new PeriodicDispatchEvent(aggregationRule).toMotechEvent(), periodicSchedule.getStartTime().toDate(), null, (long) (periodicSchedule.getPeriod().toStandardSeconds().getSeconds() * MILLIS_IN_A_SEC), true));
        } else if (aggregationSchedule instanceof CronBasedAggregationRequest) {
            CronBasedAggregationRequest cronSchedule = (CronBasedAggregationRequest) aggregationSchedule;
            schedulerService.safeScheduleJob(new CronSchedulableJob(new PeriodicDispatchEvent(aggregationRule).toMotechEvent(), cronSchedule.getCronExpression()));
        } else if (aggregationSchedule instanceof CustomAggregationRequest) {
            CustomAggregationRequest customSchedule = (CustomAggregationRequest) aggregationSchedule;
            schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(new SporadicDispatchEvent(aggregationRule, customSchedule.getRule()).toMotechEvent(), now().toDate(), null, MILLIS_IN_A_MINUTE, true));
        }
    }
}
