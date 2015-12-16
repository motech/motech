package org.motechproject.tasks.service.impl;

import org.motechproject.mds.query.QueryParams;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.domain.TaskTriggerInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.repository.ChannelsDataService;
import org.motechproject.tasks.repository.TriggerEventsDataService;
import org.motechproject.tasks.service.DynamicChannelLoader;
import org.motechproject.tasks.service.TriggerEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TriggerEventServiceImpl implements TriggerEventService {

    @Autowired
    private TriggerEventsDataService triggerEventsDataService;

    @Autowired
    private ChannelsDataService channelsDataService;

    @Autowired
    private DynamicChannelLoader dynamicChannelLoader;

    @Override
    public List<TriggerEvent> getDynamicTriggers(String moduleName, int page, int pareSize) {
        return dynamicChannelLoader.getDynamicTriggers(moduleName, page, pareSize);
    }

    @Override
    public List<TriggerEvent> getStaticTriggers(String moduleName, int page, int pageSize) {
        return triggerEventsDataService.byChannelModuleName(moduleName, new QueryParams(page, pageSize));
    }

    @Override
    public TriggerEvent getTrigger(TaskTriggerInformation triggerInformation) {
        TriggerEvent triggerEvent = triggerEventsDataService.byChannelModuleNameAndListenerSubject(
                triggerInformation.getModuleName(), triggerInformation.getTriggerListenerSubject());

        return triggerEvent != null ? triggerEvent : dynamicChannelLoader.getTrigger(triggerInformation);
    }

    @Override
    public boolean triggerExists(TaskTriggerInformation information) {
        return getTrigger(information) != null;
    }

    @Override
    public boolean hasDynamicTriggers(String moduleName) {
        return dynamicChannelLoader.hasDynamicTriggers(moduleName);
    }

    @Override
    public long countStaticTriggers(String moduleName) {
        return triggerEventsDataService.countByChannelModuleName(moduleName);
    }

    @Override
    public long countDynamicTriggers(String moduleName) {
        return dynamicChannelLoader.countByChannelModuleName(moduleName);
    }

    @Override
    public Set<TaskError> validateTrigger(TaskTriggerInformation trigger) {
        Set<TaskError> errors = new HashSet<>();
        String subject = trigger.getTriggerListenerSubject();
        String moduleName = trigger.getModuleName();
        boolean channelExists = false;
        boolean validTrigger = false;

        if (channelsDataService.countFindByModuleName(moduleName) != 0) {
            channelExists = true;
            validTrigger = triggerEventsDataService.countByChannelModuleNameAndListenerSubject(moduleName, subject) > 0;
        }

        if (!validTrigger && dynamicChannelLoader.channelExists(moduleName)) {
            channelExists = true;
            validTrigger = dynamicChannelLoader.isValidTrigger(moduleName, subject);
        }

        if (channelExists) {
            if (!validTrigger) {
                errors.add(new TaskError("task.validation.error.triggerNotExist", trigger.getDisplayName()));
            }
        } else {
            errors.add(new TaskError("task.validation.error.triggerChannelNotRegistered"));
        }

        return errors;
    }
}
