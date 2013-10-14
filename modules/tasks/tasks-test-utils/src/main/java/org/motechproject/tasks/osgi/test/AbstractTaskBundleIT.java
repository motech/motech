package org.motechproject.tasks.osgi.test;

import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

public class AbstractTaskBundleIT extends BaseOsgiIT {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTaskBundleIT.class);

    protected Channel findChannel(String channelName) throws IOException {
        ServiceReference serviceReference = bundleContext.getServiceReference(ChannelService.class.getName());
        assertNotNull(serviceReference);
        ChannelService channelService = (ChannelService) bundleContext.getService(serviceReference);
        assertNotNull(channelService);

        LOG.info(String.format("Looking for %s", channelName));

        LOG.info(String.format("There are %d channels in total", channelService.getAllChannels().size()));

        return channelService.getChannel(channelName);
    }

    protected TaskEvent findTaskEventBySubject(List<? extends TaskEvent> taskEvents, String subject) {
        TaskEvent taskEvent = null;
        for (TaskEvent event : taskEvents) {
            if (subject.equals(event.getSubject())) {
                taskEvent = event;
                break;
            }
        }
        return taskEvent;
    }

    protected TriggerEvent findTriggerEventBySubject(List<TriggerEvent> triggerEvents, String subject) {
        TaskEvent taskEvent = findTaskEventBySubject(triggerEvents, subject);
        assertTrue(taskEvent instanceof TriggerEvent);
        return (TriggerEvent) taskEvent;
    }

    protected ActionEvent findActionEventBySubject(List<ActionEvent> actionEvents, String subject) {
        TaskEvent taskEvent = findTaskEventBySubject(actionEvents, subject);
        assertTrue(taskEvent instanceof ActionEvent);
        return (ActionEvent) taskEvent;
    }

    protected boolean hasEventParameterKey(String externalIdKey, List<EventParameter> eventParameters) {
        boolean found = false;
        for (EventParameter param : eventParameters) {
            if (externalIdKey.equals(param.getEventKey())) {
                found = true;
                break;
            }
        }
        return found;
    }

    protected boolean hasActionParameterKey(String externalIdKey, SortedSet<ActionParameter> actionParameters) {
        boolean found = false;
        for (ActionParameter param : actionParameters) {
            if (externalIdKey.equals(param.getKey())) {
                found = true;
                break;
            }
        }
        return found;
    }
}
