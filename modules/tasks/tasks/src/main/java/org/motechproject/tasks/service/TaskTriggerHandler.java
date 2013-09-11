package org.motechproject.tasks.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.TaskHandlerException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.tasks.events.constants.EventDataKeys.HANDLER_ERROR_PARAM;
import static org.motechproject.tasks.events.constants.EventDataKeys.TASK_FAIL_FAILURE_DATE;
import static org.motechproject.tasks.events.constants.EventDataKeys.TASK_FAIL_FAILURE_NUMBER;
import static org.motechproject.tasks.events.constants.EventDataKeys.TASK_FAIL_MESSAGE;
import static org.motechproject.tasks.events.constants.EventDataKeys.TASK_FAIL_STACK_TRACE;
import static org.motechproject.tasks.events.constants.EventDataKeys.TASK_FAIL_TASK_ID;
import static org.motechproject.tasks.events.constants.EventDataKeys.TASK_FAIL_TASK_NAME;
import static org.motechproject.tasks.events.constants.EventDataKeys.TASK_FAIL_TRIGGER_DISABLED;
import static org.motechproject.tasks.events.constants.EventSubjects.createHandlerFailureSubject;
import static org.motechproject.tasks.events.constants.EventSubjects.createHandlerSuccessSubject;
import static org.motechproject.tasks.events.constants.TaskFailureCause.TRIGGER;
import static org.motechproject.tasks.service.HandlerPredicates.activeTasks;
import static org.motechproject.tasks.service.HandlerPredicates.withServiceName;

/**
 * The <code>TaskTriggerHandler</code> receives events and executes tasks for which the trigger
 * event subject is the same as the received event subject.
 */
@Service
public class TaskTriggerHandler implements TriggerHandler {
    private static final String TASK_POSSIBLE_ERRORS_KEY = "task.possible.errors";

    private static final Logger LOG = LoggerFactory.getLogger(TaskTriggerHandler.class);

    private TaskService taskService;
    private TaskActivityService activityService;
    private EventListenerRegistryService registryService;
    private EventRelay eventRelay;
    private SettingsFacade settings;
    private Map<String, DataProvider> dataProviders;

    private TaskActionExecutor executor;

    @Autowired
    public TaskTriggerHandler(TaskService taskService, TaskActivityService activityService,
                              EventListenerRegistryService registryService, EventRelay eventRelay,
                              @Qualifier("tasksSettings") SettingsFacade settings) {
        this.taskService = taskService;
        this.activityService = activityService;
        this.registryService = registryService;
        this.eventRelay = eventRelay;
        this.settings = settings;

        this.executor = new TaskActionExecutor(taskService, activityService, eventRelay);

        for (Task task : taskService.getAllTasks()) {
            registerHandlerFor(task.getTrigger().getSubject());
        }
    }

    @Override
    public final void registerHandlerFor(String subject) {
        String serviceName = "taskTriggerHandler";
        Method method = ReflectionUtils.findMethod(this.getClass(), "handle", MotechEvent.class);
        Object obj = CollectionUtils.find(
            registryService.getListeners(subject), withServiceName(serviceName)
        );

        try {
            if (method != null && obj == null) {
                EventListener proxy = new MotechListenerEventProxy(serviceName, this, method);

                registryService.registerListener(proxy, subject);
                LOG.info(String.format("%s listens on subject %s", serviceName, subject));
            }
        } catch (Exception exp) {
            LOG.error(
                String.format("%s can not listen on subject %s due to:", serviceName, subject),
                exp
            );
        }
    }

    @Override
    public void handle(MotechEvent event) throws TriggerNotFoundException {
        TriggerEvent trigger = taskService.findTrigger(event.getSubject());
        List<Task> tasks = taskService.findTasksForTrigger(trigger);

        CollectionUtils.filter(tasks, activeTasks());
        for (Task task : tasks) {
            TaskContext taskContext = new TaskContext(task, event, activityService);
            TaskInitializer initializer = new TaskInitializer(taskContext);

            try {
                if (initializer.evalConfigSteps(dataProviders)) {
                    for (TaskActionInformation action : task.getActions()) {
                        executor.execute(task, action, taskContext);
                    }
                    handleSuccess(event, task);
                }
            } catch (TaskHandlerException e) {
                handleError(event, task, e);
            } catch (Exception e) {
                handleError(event, task, new TaskHandlerException(TRIGGER, "task.error.unrecognizedError", e));
            }
        }
    }

    private void handleError(MotechEvent trigger, Task task, TaskHandlerException e) {
        LOG.debug(String.format("Omitted task with ID: %s because: ", task.getId()), e);

        activityService.addError(task, e);

        int failureNumber = activityService.errorsFromLastRun(task).size();
        int possibleErrorsNumber = getPossibleErrorsNumber();

        if (failureNumber >= possibleErrorsNumber) {
            task.setEnabled(false);
            taskService.save(task);

            activityService.addWarning(task);
            publishTaskDisabledMessage(task.getName());
        }

        Map<String, Object> errorParam = new HashMap<>();
        errorParam.put(TASK_FAIL_MESSAGE, e.getMessage());
        errorParam.put(TASK_FAIL_STACK_TRACE, ExceptionUtils.getStackTrace(e));
        errorParam.put(TASK_FAIL_FAILURE_DATE, DateTime.now());
        errorParam.put(TASK_FAIL_FAILURE_NUMBER, failureNumber);
        errorParam.put(TASK_FAIL_TRIGGER_DISABLED, task.isEnabled());
        errorParam.put(TASK_FAIL_TASK_ID, task.getId());
        errorParam.put(TASK_FAIL_TASK_NAME, task.getName());

        Map<String, Object> param = trigger.getParameters();
        param.put(HANDLER_ERROR_PARAM, errorParam);

        eventRelay.sendEventMessage(new MotechEvent(
            createHandlerFailureSubject(task.getName(), e.getFailureCause()),
            param
        ));
    }

    private void handleSuccess(MotechEvent trigger, Task task) {
        activityService.addSuccess(task);

        eventRelay.sendEventMessage(new MotechEvent(
            createHandlerSuccessSubject(task.getName()),
            trigger.getParameters()
        ));
    }

    public void addDataProvider(String taskDataProviderId, DataProvider provider) {
        if (dataProviders == null) {
            dataProviders = new HashMap<>();
        }

        dataProviders.put(taskDataProviderId, provider);
    }

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
        params.put("moduleName", settings.getModuleName());

        eventRelay.sendEventMessage(new MotechEvent("org.motechproject.message", params));
    }

    private int getPossibleErrorsNumber() {
        String property = settings.getProperty(TASK_POSSIBLE_ERRORS_KEY);
        int number;

        try {
            number = Integer.parseInt(property);
        } catch (NumberFormatException e) {
            LOG.error(String.format(
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
