package org.motechproject.tasks.service.impl;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.domain.enums.TaskActivityType;
import org.motechproject.tasks.domain.mds.task.TaskDataProvider;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.dto.ChannelDto;
import org.motechproject.tasks.dto.TaskActivityDto;
import org.motechproject.tasks.dto.TaskDataProviderDto;
import org.motechproject.tasks.dto.TaskDto;
import org.motechproject.tasks.dto.TaskErrorDto;
import org.motechproject.tasks.dto.TriggerEventDto;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TaskWebService;
import org.motechproject.tasks.service.TriggerEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The implementation of the {@link TaskWebService} interface.
 */
@Service("taskWebService")
public class TaskWebServiceImpl implements TaskWebService {

    @Autowired
    private TaskActivityService taskActivityService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private TriggerEventService triggerEventService;
    @Autowired
    private TaskDataProviderService providerService;
    @Autowired
    private TaskService taskService;

    @Override
    @Transactional
    public List<TaskActivityDto> getLatestActivities() {
        return activityToDtos(taskActivityService.getLatestActivities());
    }

    @Override
    @Transactional
    public List<TaskActivityDto> getAllActivities(Set<TaskActivityType> activityTypeSet, QueryParams queryParams, boolean lastExecution) {
        return activityToDtos(taskActivityService.getAllActivities(activityTypeSet, queryParams, lastExecution));
    }

    @Override
    @Transactional
    public List<TaskActivityDto> getAllActivities(Set<TaskActivityType> activityTypeSet,
                                                  Range<DateTime> dateRange, QueryParams queryParams, boolean lastExecution) {
        return activityToDtos(taskActivityService.getAllActivities(activityTypeSet, dateRange, queryParams, lastExecution));
    }

    @Override
    @Transactional
    public List<TaskActivityDto> getTaskActivities(Long taskId, Set<TaskActivityType> activityTypeSet, QueryParams queryParams, boolean lastExecution) {
        return activityToDtos(taskActivityService.getTaskActivities(taskId, activityTypeSet, queryParams, lastExecution));
    }

    @Override
    @Transactional
    public List<TaskActivityDto> getTaskActivities(Long taskId, Set<TaskActivityType> activityTypeSet,
                                                   Range<DateTime> dateRange, QueryParams queryParams, boolean lastExecution) {
        return activityToDtos(taskActivityService.getTaskActivities(taskId, activityTypeSet, dateRange, queryParams, lastExecution));
    }

    @Override
    @Transactional
    public List<ChannelDto> getAllChannels() {
        return channelToDtos(channelService.getAllChannels());
    }

    @Override
    @Transactional
    public List<TriggerEventDto> getStaticTriggers(String moduleName, int page, int pageSize) {
        return triggerEventToDtos(triggerEventService.getStaticTriggers(moduleName, page, pageSize));
    }

    @Override
    @Transactional
    public List<TriggerEventDto> getDynamicTriggers(String moduleName, int page, int pageSize) {
        return triggerEventToDtos(triggerEventService.getDynamicTriggers(moduleName, page, pageSize));
    }

    @Override
    @Transactional
    public TriggerEventDto getTrigger(TaskTriggerInformation triggerInformation) {
        TriggerEvent trigger = triggerEventService.getTrigger(triggerInformation);
        return trigger != null ? trigger.toDto() : null;
    }

    @Override
    @Transactional
    public List<TaskDataProviderDto> getProviders() {
        return providerToDtos(providerService.getProviders());
    }

    @Override
    @Transactional
    public List<TaskDto> getAllTasks() {
        return taskToDtos(taskService.getAllTasks());
    }

    @Override
    @Transactional
    public TaskDto getTask(Long taskId) {
        return taskService.getTask(taskId).toDto();
    }

    @Override
    @Transactional
    public Set<TaskErrorDto> save(Task task) {
        return TaskError.toDtos(taskService.save(task));
    }

    @Override
    @Transactional
    public void setEnabledOrDisabled(Task task) {
        taskService.setEnabledOrDisabled(task);
    }


    private List<TaskActivityDto> activityToDtos(List<TaskActivity> activities) {
        List<TaskActivityDto> activityDtos = new ArrayList<>();

        for (TaskActivity activity : activities) {
            activityDtos.add(activity.toDto());
        }

        return activityDtos;
    }

    private List<ChannelDto> channelToDtos(List<Channel> channels) {
        List<ChannelDto> channelDtos = new ArrayList<>();

        for (Channel channel : channels) {
            channelDtos.add(channel.toDto());
        }

        return channelDtos;
    }

    private List<TriggerEventDto> triggerEventToDtos(List<TriggerEvent> triggerEvents) {
        List<TriggerEventDto> triggerEventDtos = new ArrayList<>();

        for (TriggerEvent triggerEvent : triggerEvents) {
            triggerEventDtos.add(triggerEvent.toDto());
        }

        return triggerEventDtos;
    }

    private List<TaskDataProviderDto> providerToDtos(List<TaskDataProvider> providers) {
        List<TaskDataProviderDto> providerDtos = new ArrayList<>();

        for (TaskDataProvider provider : providers) {
            providerDtos.add(provider.toDto());
        }

        return providerDtos;
    }

    private List<TaskDto> taskToDtos(List<Task> tasks) {
        List<TaskDto> taskDtos = new ArrayList<>();

        for (Task task : tasks) {
            taskDtos.add(task.toDto());
        }

        return taskDtos;
    }
}