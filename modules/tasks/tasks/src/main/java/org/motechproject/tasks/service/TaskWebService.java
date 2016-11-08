package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.enums.TaskActivityType;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.dto.ChannelDto;
import org.motechproject.tasks.dto.TaskActivityDto;
import org.motechproject.tasks.dto.TaskDataProviderDto;
import org.motechproject.tasks.dto.TaskDto;
import org.motechproject.tasks.dto.TaskErrorDto;
import org.motechproject.tasks.dto.TriggerEventDto;

import java.util.List;
import java.util.Set;

/**
 * This interface is used by task controllers to work with dto objects.
 */
public interface TaskWebService {

    /**
     * @see TaskActivityService#getLatestActivities()
     */
    List<TaskActivityDto> getLatestActivities();

    /**
     * @see TaskActivityService#getAllActivities(Set, QueryParams)
     */
    List<TaskActivityDto> getAllActivities(Set<TaskActivityType> activityTypeSet, QueryParams queryParams);

    /**
     * @see TaskActivityService#getAllActivities(Set, Range, QueryParams)
     */
    List<TaskActivityDto> getAllActivities(Set<TaskActivityType> activityTypeSet, Range<DateTime> dateRange, QueryParams queryParams);

    /**
     * @see TaskActivityService#getTaskActivities(Long, Set, QueryParams)
     */
    List<TaskActivityDto> getTaskActivities(Long taskId, Set<TaskActivityType> activityTypeSet, QueryParams queryParams);

    /**
     * @see TaskActivityService#getTaskActivities(Long, Set, Range, QueryParams)
     */
    List<TaskActivityDto> getTaskActivities(Long taskId, Set<TaskActivityType> activityTypeSet, Range<DateTime> dateRange,
                                            QueryParams queryParams);

    /**
     * @see ChannelService#getAllChannels()
     */
    List<ChannelDto> getAllChannels();

    /**
     * @see TriggerEventService#getStaticTriggers(String, int, int)
     */
    List<TriggerEventDto> getStaticTriggers(String moduleName, int page, int pageSize);

    /**
     * @see TriggerEventService#getDynamicTriggers(String, int, int)
     */
    List<TriggerEventDto> getDynamicTriggers(String moduleName, int page, int pageSize);

    /**
     * @see TriggerEventService#getTrigger(TaskTriggerInformation)
     */
    TriggerEventDto getTrigger(TaskTriggerInformation triggerInformation);

    /**
     * @see TaskDataProviderService#getProviders()
     */
    List<TaskDataProviderDto> getProviders();

    /**
     * @see TaskService#getAllTasks()
     */
    List<TaskDto> getAllTasks();

    /**
     * @see TaskService#getTask(Long)
     */
    TaskDto getTask(Long taskId);

    /**
     * @see TaskService#save(Task)
     */
    Set<TaskErrorDto> save(Task task);
}