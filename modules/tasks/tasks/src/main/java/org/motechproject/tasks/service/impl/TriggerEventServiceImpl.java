package org.motechproject.tasks.service.impl;

import org.motechproject.mds.query.QueryParams;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.repository.ChannelsDataService;
import org.motechproject.tasks.repository.TriggerEventsDataService;
import org.motechproject.tasks.service.DynamicChannelLoader;
import org.motechproject.tasks.service.TriggerEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of the {@link TriggerEventService} interface.
 */
@Service
public class TriggerEventServiceImpl implements TriggerEventService {

    private TriggerEventsDataService triggerEventsDataService;
    private ChannelsDataService channelsDataService;
    private DynamicChannelLoader dynamicChannelLoader;

    @Override
    @Transactional
    public List<TriggerEvent> getDynamicTriggers(String moduleName, int page, int pareSize) {
        return dynamicChannelLoader.getDynamicTriggers(moduleName, page, pareSize);
    }

    @Override
    @Transactional
    public List<TriggerEvent> getStaticTriggers(String moduleName, int page, int pageSize) {
        return triggerEventsDataService.byChannelModuleName(moduleName, new QueryParams(page, pageSize));
    }

    @Override
    @Transactional
    public TriggerEvent getTrigger(TaskTriggerInformation triggerInformation) {
        TriggerEvent triggerEvent = triggerEventsDataService.byChannelModuleNameAndSubject(
                triggerInformation.getModuleName(), triggerInformation.getSubject());

        return triggerEvent != null ? triggerEvent : dynamicChannelLoader.getTrigger(triggerInformation);
    }

    @Override
    @Transactional
    public boolean triggerExists(TaskTriggerInformation information) {
        return getTrigger(information) != null;
    }

    @Override
    public boolean providesDynamicTriggers(String moduleName) {
        return dynamicChannelLoader.providesDynamicTriggers(moduleName);
    }

    @Override
    @Transactional
    public long countStaticTriggers(String moduleName) {
        return triggerEventsDataService.countByChannelModuleName(moduleName);
    }

    @Override
    public long countDynamicTriggers(String moduleName) {
        return dynamicChannelLoader.countByChannelModuleName(moduleName);
    }

    @Override
    @Transactional
    public Set<TaskError> validateTrigger(TaskTriggerInformation trigger) {
        Set<TaskError> errors = new HashSet<>();
        String subject = trigger.getSubject();
        String moduleName = trigger.getModuleName();
        boolean channelExists = false;
        boolean validTrigger = false;

        if (channelsDataService.countFindByModuleName(moduleName) != 0) {
            channelExists = true;
            validTrigger = triggerEventsDataService.countByChannelModuleNameAndSubject(moduleName, subject) > 0;
        }

        if (!validTrigger && dynamicChannelLoader.channelExists(moduleName)) {
            channelExists = true;
            validTrigger = dynamicChannelLoader.validateTrigger(moduleName, subject);
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

    @Autowired
    public void setTriggerEventsDataService(TriggerEventsDataService triggerEventsDataService) {
        this.triggerEventsDataService = triggerEventsDataService;
    }

    @Autowired
    public void setChannelsDataService(ChannelsDataService channelsDataService) {
        this.channelsDataService = channelsDataService;
    }

    @Autowired
    public void setDynamicChannelLoader(DynamicChannelLoader dynamicChannelLoader) {
        this.dynamicChannelLoader = dynamicChannelLoader;
    }
}
