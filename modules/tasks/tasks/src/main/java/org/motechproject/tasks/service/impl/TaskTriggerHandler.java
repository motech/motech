package org.motechproject.tasks.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.commons.api.TasksEventParser;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.config.SettingsFacade;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.util.TaskContext;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TriggerHandler;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.tasks.service.util.HandlerPredicates.withServiceName;
import static org.motechproject.tasks.constants.EventDataKeys.HANDLER_ERROR_PARAM;
import static org.motechproject.tasks.constants.EventDataKeys.JOB_SUBJECT;
import static org.motechproject.tasks.constants.EventDataKeys.REPEAT_COUNT;
import static org.motechproject.tasks.constants.EventDataKeys.REPEAT_INTERVAL_TIME;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_FAILURE_DATE;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_FAILURE_NUMBER;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_MESSAGE;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_STACK_TRACE;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_TASK_ID;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_TASK_NAME;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_TRIGGER_DISABLED;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_ID;
import static org.motechproject.tasks.constants.EventSubjects.SCHEDULE_REPEATING_JOB;
import static org.motechproject.tasks.constants.EventSubjects.UNSCHEDULE_REPEATING_JOB;
import static org.motechproject.tasks.constants.EventSubjects.createHandlerFailureSubject;
import static org.motechproject.tasks.constants.EventSubjects.createHandlerSuccessSubject;
import static org.motechproject.tasks.constants.TaskFailureCause.TRIGGER;

/**
 * The <code>TaskTriggerHandler</code> receives events and executes tasks for which the trigger
 * event subject is the same as the received event subject.
 */
@Service("taskTriggerHandler")
public class TaskTriggerHandler implements TriggerHandler {

    private static final String BEAN_NAME = "taskTriggerHandler";

    private static final String TASK_POSSIBLE_ERRORS_KEY = "task.possible.errors";

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTriggerHandler.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskActivityService activityService;

    @Autowired
    private EventListenerRegistryService registryService;

    @Autowired
    private EventRelay eventRelay;

    @Autowired
    @Qualifier("tasksSettings")
    private SettingsFacade settings;

    @Autowired
    private TaskActionExecutor executor;

    private Map<String, DataProvider> dataProviders;

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
    public void handle(MotechEvent event) {
        LOGGER.info("Handling the motech event with subject: {}", event.getSubject());

        // Look for custom event parser
        Map<String, Object> eventParams = event.getParameters();

        TasksEventParser parser = null;
        if (eventParams != null) {
            parser = taskService.findCustomParser((String) eventParams.get(TasksEventParser.CUSTOM_PARSER_EVENT_KEY));
        }

        // Use custom event parser, if it exists, to modify event
        String triggerSubject = parser == null ? event.getSubject() : parser.parseEventSubject(event.getSubject(), event.getParameters());
        Map<String, Object> parameters = parser == null ? event.getParameters() : parser.parseEventParameters(event.getSubject(), event.getParameters());

        List<Task> tasks = taskService.findActiveTasksForTriggerSubject(triggerSubject);

        // Handle all tasks one by one
        for (Task task : tasks) {
            handleTask(task, parameters);
        }
    }

    @Override
    public void handleRetry(MotechEvent event) {
        LOGGER.info("Handling the motech event with subject: {} for task retry", event.getSubject());

        Map<String, Object> eventParams = event.getParameters();
        Task task = taskService.getTask((Long) eventParams.get(TASK_ID));

        if (task == null || !task.isEnabled()) {
            unscheduleTaskRetry((String) eventParams.get(JOB_SUBJECT));
        } else {
            handleTask(task, eventParams);
        }
    }

    @Override
    @Transactional
    public void retryTask(Long activityId) {
        TaskActivity activity = activityService.getTaskActivityById(activityId);
        handleTask(taskService.getTask(activity.getTask()), activity.getParameters());
    }

    private boolean isTaskRetryAlreadyScheduled(Map<String, Object> eventParams) {
        return eventParams.get(TASK_ID) != null;
    }

    private void handleTask(Task task, Map<String, Object> parameters) {

        TaskContext taskContext = new TaskContext(task, parameters, activityService);
        TaskInitializer initializer = new TaskInitializer(taskContext);

        boolean success = true;

        try {
            LOGGER.info("Executing all actions from task: {}", task.getName());
            if (initializer.evalConfigSteps(dataProviders)) {
                for (TaskActionInformation action : task.getActions()) {
                    executor.execute(task, action, taskContext);
                }
                handleSuccess(parameters, task);
            }
            LOGGER.warn("Actions from task: {} weren't executed, because config steps didn't pass the evaluation", task.getName());
        } catch (TaskHandlerException e) {
            handleError(parameters, task, e);
            success = false;
        } catch (RuntimeException e) {
            handleError(parameters, task, new TaskHandlerException(TRIGGER, "task.error.unrecognizedError", e));
            success = false;
        }

        if (task.retryTaskOnFailure()) {
            if (success && isTaskRetryAlreadyScheduled(parameters)) {
                unscheduleTaskRetry(task.getTrigger().getEffectiveListenerRetrySubject());
            } else if (!success && !isTaskRetryAlreadyScheduled(parameters)) {
                scheduleTaskRetry(task, parameters);
            }
        }
    }

    private void scheduleTaskRetry(Task task, Map<String, Object> parameters) {
        Map<String, Object> eventParameters = new HashMap<>();
        eventParameters.putAll(parameters);

        eventParameters.put(TASK_ID, task.getId());
        eventParameters.put(REPEAT_COUNT, task.getNumberOfRetries());
        eventParameters.put(REPEAT_INTERVAL_TIME, task.getRetryIntervalInMilliseconds() / 1000);
        eventParameters.put(JOB_SUBJECT, task.getTrigger().getEffectiveListenerRetrySubject());

        eventRelay.sendEventMessage(new MotechEvent(SCHEDULE_REPEATING_JOB, eventParameters));
    }

    private void unscheduleTaskRetry(String jobSubject) {
        Map<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(JOB_SUBJECT, jobSubject);

        eventRelay.sendEventMessage(new MotechEvent(UNSCHEDULE_REPEATING_JOB, eventParameters));
    }

    private void handleError(Map<String, Object> params, Task task, TaskHandlerException e) {
        LOGGER.warn("Omitted task: {} with ID: {} because: {}", task.getName(), task.getId(), e);

        activityService.addError(task, e, params);
        task.incrementFailuresInRow();

        LOGGER.warn("The number of failures for task: {} is: {}", task.getName(), task.getFailuresInRow());

        int failureNumber = task.getFailuresInRow();
        int possibleErrorsNumber = getPossibleErrorsNumber();

        if (failureNumber >= possibleErrorsNumber) {
            task.setEnabled(false);

            activityService.addWarning(task);
            publishTaskDisabledMessage(task.getName());
        }

        taskService.save(task);

        Map<String, Object> errorParam = new HashMap<>();
        errorParam.put(TASK_FAIL_MESSAGE, e.getMessage());
        errorParam.put(TASK_FAIL_STACK_TRACE, ExceptionUtils.getStackTrace(e));
        errorParam.put(TASK_FAIL_FAILURE_DATE, DateTime.now());
        errorParam.put(TASK_FAIL_FAILURE_NUMBER, failureNumber);
        errorParam.put(TASK_FAIL_TRIGGER_DISABLED, task.isEnabled());
        errorParam.put(TASK_FAIL_TASK_ID, task.getId());
        errorParam.put(TASK_FAIL_TASK_NAME, task.getName());

        Map<String, Object> errorEventParam = new HashMap<>();
        errorEventParam.putAll(params);
        errorEventParam.put(HANDLER_ERROR_PARAM, errorParam);

        eventRelay.sendEventMessage(new MotechEvent(
            createHandlerFailureSubject(task.getName(), e.getFailureCause()),
            errorEventParam
        ));
    }

    private void handleSuccess(Map<String, Object> params, Task task) {
        LOGGER.debug("All actions from task: {} with ID: {} were successfully executed", task.getName(), task.getId());

        activityService.addSuccess(task);
        task.resetFailuresInRow();
        taskService.save(task);

        eventRelay.sendEventMessage(new MotechEvent(
            createHandlerSuccessSubject(task.getName()),
            params
        ));
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


    void setDataProviders(Map<String, DataProvider> dataProviders) {
        this.dataProviders = dataProviders;
    }

    private void publishTaskDisabledMessage(String taskName) {
        Map<String, Object> params = new HashMap<>();
        params.put("message", "Task disabled automatically: " + taskName);
        params.put("level", "CRITICAL");
        params.put("moduleName", settings.getBundleSymbolicName());

        eventRelay.sendEventMessage(new MotechEvent("org.motechproject.message", params));
    }

    private int getPossibleErrorsNumber() {
        String property = settings.getProperty(TASK_POSSIBLE_ERRORS_KEY);
        int number;

        try {
            number = Integer.parseInt(property);
        } catch (NumberFormatException e) {
            LOGGER.error(String.format(
                    "The value of key: %s is not a number. Possible errors number is set to zero.",
                    TASK_POSSIBLE_ERRORS_KEY
            ));
            number = 0;
        }

        return number;
    }

    @Autowired(required = false)
    public void setBundleContext(BundleContext bundleContext) {
        this.executor.setBundleContext(bundleContext);
    }
}
