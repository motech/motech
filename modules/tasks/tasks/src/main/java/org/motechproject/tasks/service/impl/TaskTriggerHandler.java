package org.motechproject.tasks.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.commons.api.TasksEventParser;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.tasks.constants.EventDataKeys;
import org.motechproject.tasks.domain.mds.task.FilterSet;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TriggerHandler;
import org.motechproject.tasks.service.util.TaskContext;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.motechproject.tasks.constants.EventDataKeys.JOB_SUBJECT;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_ID;
import static org.motechproject.tasks.constants.TaskFailureCause.TRIGGER;
import static org.motechproject.tasks.service.util.HandlerPredicates.withServiceName;

/**
 * The <code>TaskTriggerHandler</code> receives events and executes tasks for which the trigger
 * event subject is the same as the received event subject.
 */
@Service("taskTriggerHandler")
public class TaskTriggerHandler implements TriggerHandler {

    private static final String BEAN_NAME = "taskTriggerHandler";

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTriggerHandler.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskActivityService activityService;

    @Autowired
    private EventListenerRegistryService registryService;

    @Autowired
    private TaskActionExecutor executor;

    @Autowired
    private TaskRetryHandler taskRetryHandler;

    @Autowired
    private TasksPostExecutionHandler postExecutionHandler;

    private Map<String, DataProvider> dataProviders;

    private Set<Long> handledTasksId = new HashSet<>();

    @PostConstruct
    public void init() {
        for (Task task : taskService.getAllTasks()) {
            registerHandlerFor(task.getTrigger().getEffectiveListenerSubject());

            if (task.retryTaskOnFailure()) {
                registerHandlerFor(task.getTrigger().getEffectiveListenerRetrySubject(), true);
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        registryService.clearListenersForBean(BEAN_NAME);
    }

    @Override
    public void registerHandlerFor(String subject) {
        registerHandlerFor(subject, false);
    }

    @Override
    public void registerHandlerFor(String subject, boolean isRetryHandler) {
        LOGGER.info("Registering handler for {}", subject);

        String methodName = isRetryHandler ? "handleRetry" : "handle";
        Method method = ReflectionUtils.findMethod(this.getClass(), methodName, MotechEvent.class);
        Object obj = CollectionUtils.find(
                registryService.getListeners(subject), withServiceName(BEAN_NAME)
        );

        try {
            if (method != null && obj == null) {
                EventListener proxy = new MotechListenerEventProxy(BEAN_NAME, this, method);

                registryService.registerListener(proxy, subject);
                LOGGER.info(String.format("%s listens on subject %s", BEAN_NAME, subject));
            }
        } catch (RuntimeException exp) {
            LOGGER.error(
                    String.format("%s can not listen on subject %s due to:", BEAN_NAME, subject),
                    exp
            );
        }
    }

    @Override
    @Transactional
    public void handle(MotechEvent event) {
        LOGGER.info("Handling the motech event with subject: {}", event.getSubject());

        // Look for custom event parser
        Map<String, Object> eventParams = event.getParameters();
        eventParams.putAll(event.getMetadata());

        TasksEventParser parser = null;
        if (eventParams != null) {
            parser = taskService.findCustomParser((String) eventParams.get(TasksEventParser.CUSTOM_PARSER_EVENT_KEY));
        }

        // Use custom event parser, if it exists, to modify event
        String triggerSubject = parser == null ? event.getSubject() : parser.parseEventSubject(event.getSubject(), eventParams);
        Map<String, Object> parameters = parser == null ? eventParams : parser.parseEventParameters(event.getSubject(), eventParams);

        List<Task> tasks = taskService.findActiveTasksForTriggerSubject(triggerSubject);

        // Handle all tasks one by one
        for (Task task : tasks) {
            checkAndHandleTask(task, parameters, false);
        }
    }

    @Override
    @Transactional
    public void handleRetry(MotechEvent event) {
        LOGGER.info("Handling the motech event with subject: {} for task retry", event.getSubject());

        Map<String, Object> eventParams = event.getParameters();
        Map<String, Object> eventMetadata = event.getMetadata();

        Task task = taskService.getTask((Long) eventMetadata.get(TASK_ID));

        if (task == null || !task.isEnabled()) {
            taskRetryHandler.unscheduleTaskRetry((String) eventMetadata.get(JOB_SUBJECT));
        } else {
            checkAndHandleTask(task, eventParams, true);
        }
    }

    @Override
    @Transactional
    public void retryTask(Long activityId) {
        TaskActivity activity = activityService.getTaskActivityById(activityId);
        handleTask(taskService.getTask(activity.getTask()), activity.getParameters(), true);
    }

    private void handleTask(Task task, Map<String, Object> parameters, boolean isRetry) {
        long activityId = activityService.addTaskStarted(task, parameters);
        Map<String, Object> metadata = prepareTaskMetadata(task.getId(), activityId, isRetry);

        TaskContext taskContext = new TaskContext(task, parameters, metadata, activityService);
        TaskInitializer initializer = new TaskInitializer(taskContext);
        List<FilterSet> filterSetList = new ArrayList<>(task.getTaskConfig().getFilters());
        boolean actionFilterResult = true;
        int executedActions = 0;
        int step = 0;
        int actualFilterIndex = initializer.getActionFilters();

        try {
            LOGGER.info("Executing all actions from task: {}", task.getName());
            if (initializer.evalConfigSteps(dataProviders)) {
                while (actionFilterResult && executedActions < task.getActions().size()) {
                    if (shouldCheckFilter(filterSetList, actualFilterIndex, step)) {
                        actionFilterResult = initializer.checkActionFilter(actualFilterIndex, filterSetList);
                        actualFilterIndex += 1;
                    } else {
                        executor.execute(task, task.getActions().get(executedActions), executedActions, taskContext, activityId);
                        executedActions += 1;
                    }
                    step += 1;
                }
            } else {
                activityService.addTaskFiltered(activityId);
            }
            LOGGER.warn("Actions from task: {} weren't executed, because config steps didn't pass the evaluation", task.getName());
        } catch (TaskHandlerException e) {
            postExecutionHandler.handleError(parameters, metadata, task, e, activityId);
        } catch (RuntimeException e) {
            postExecutionHandler.handleError(parameters, metadata, task, new TaskHandlerException(TRIGGER, "task.error.unrecognizedError", e), activityId);
        }
    }

    /**
     * Checks if the given task is still running. If not the task will be handled.
     *
     * @param task the given task.
     * @param eventParameters parameters from the given event
     * @param isRetry true if handler is for task retry system; false otherwise
     */
    private void checkAndHandleTask(Task task, Map<String, Object> eventParameters, boolean isRetry) {
        if (!isTaskHandled(task) ) { //Checks
            addTaskHandled(task);

            handleTask(task, eventParameters, isRetry);

            deleteTaskHandled(task);
        } else {
            LOGGER.warn("The task {} didn't execute, because the previous invocation is still running.", task.getName());
        }
    }

    @Override
    public void addDataProvider(DataProvider provider) {
        if (dataProviders == null) {
            dataProviders = new HashMap<>();
        }

        dataProviders.put(provider.getName(), provider);
    }

    @Override
    public void removeDataProvider(String taskDataProviderId) {
        if (MapUtils.isNotEmpty(dataProviders)) {
            dataProviders.remove(taskDataProviderId);
        }
    }

    private boolean shouldCheckFilter(List<FilterSet> filterSetList, int index, int step) {
        return index < filterSetList.size() && filterSetList.get(index).getActionFilterOrder() == step;
    }

    void setDataProviders(Map<String, DataProvider> dataProviders) {
        this.dataProviders = dataProviders;
    }

    private Map<String, Object> prepareTaskMetadata(Long taskId, long activityId, Boolean isRetry) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(EventDataKeys.TASK_ID, taskId);
        metadata.put(EventDataKeys.TASK_ACTIVITY_ID, activityId);
        metadata.put(EventDataKeys.TASK_RETRY, isRetry);

        return metadata;
    }

    private void addTaskHandled(Task task) {
        this.handledTasksId.add(task.getId());
    }

    private void deleteTaskHandled(Task task) {
        this.handledTasksId.remove(task.getId());
    }

    private boolean isTaskHandled(Task task) {
        boolean handled = false;

        if (this.handledTasksId.contains(task.getId())) {
            handled = true;
        }

        return handled;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.executor.setBundleContext(bundleContext);
    }
}
