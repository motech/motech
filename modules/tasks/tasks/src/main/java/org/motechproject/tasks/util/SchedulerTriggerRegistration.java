package org.motechproject.tasks.util;

import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.contract.EventParameterRequest;
import org.motechproject.tasks.contract.TriggerEventRequest;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class SchedulerTriggerRegistration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerTriggerRegistration.class);

    private BundleContext bundleContext;
    private ChannelService channelService;


    @Autowired
    public SchedulerTriggerRegistration(BundleContext bundleContext, ChannelService channelService) {
        this.bundleContext = bundleContext;
        this.channelService = channelService;
    }

    @PostConstruct
    public void updateTaskInfo() {
        LOGGER.info("Updating tasks integration");

        try {
            updateChannel();
        } catch (ValidationException e) {
            LOGGER.error("Channel generated was not accepted by tasks due to validation errors", e);
        }
    }

    private void updateChannel() {

        LOGGER.info("Registering tasks channel with the channel service");

        List<EventParameterRequest> eventParameters = new ArrayList<>();
        eventParameters.add(new EventParameterRequest("runDate", "scheduler.runDate", "DATE"));

        List<TriggerEventRequest> triggers = buildTriggers();
       //triggers.add(new TriggerEventRequest("scheduler.runOnceJobTrigger", "org.motechproject.tasks.scheduler.runOnceJob.", null, eventParameters));

        ChannelRequest channelRequest = new ChannelRequest("scheduler",
                bundleContext.getBundle().getSymbolicName(),
                bundleContext.getBundle().getVersion().toString(),
                null, triggers, new ArrayList<ActionEventRequest>());

        channelService.registerChannel(channelRequest);
    }

    public List<TriggerEventRequest> buildTriggers() {
        List<String> triggerNames = new ArrayList<>();
        triggerNames.add("cronJob");
        triggerNames.add("repeatingJob");
        triggerNames.add("runOnceJob");
        triggerNames.add("dayOfWeekJob");
        triggerNames.add("repeatingJobWithPeriodInterval");

        List<TriggerEventRequest> triggers = new ArrayList<>();
        List<EventParameterRequest> parameters = new ArrayList<>();

        for (String name : triggerNames) {
            String displayName = "scheduler." + name + "Trigger";
            String eventSubject = "org.motechproject.tasks.scheduler." + name +".";

            triggers.add(new TriggerEventRequest(displayName, eventSubject, null, parameters));
        }

        return triggers;
    }
}
