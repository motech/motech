package org.motechproject.tasks.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.tasks.util.TaskUtil.getSubject;

@Service
public class TaskTriggerHandler {
    private static final String SERVICE_NAME = "taskTriggerHandler";
    private static final String TASK_POSSIBLE_ERRORS_KEY = "task.possible.errors";

    private static final Logger LOG = LoggerFactory.getLogger(TaskTriggerHandler.class);

    private TaskService taskService;
    private TaskStatusMessageService statusMessageService;
    private EventListenerRegistryService registryService;
    private EventRelay eventRelay;
    private SettingsFacade settingsFacade;

    @Autowired
    public TaskTriggerHandler(final TaskService taskService, final TaskStatusMessageService statusMessageService,
                              final EventListenerRegistryService registryService, final EventRelay eventRelay,
                              final SettingsFacade settingsFacade) {
        this.taskService = taskService;
        this.statusMessageService = statusMessageService;
        this.registryService = registryService;
        this.eventRelay = eventRelay;
        this.settingsFacade = settingsFacade;

        registerHandler();
    }

    public void handler(final MotechEvent triggerEvent) {
        TaskEvent trigger;

        try {
            trigger = taskService.findTrigger(triggerEvent.getSubject());
        } catch (TriggerNotFoundException e) {
            LOG.error(e.getMessage());
            return;
        }

        List<Task> tasks = taskService.findTasksForTrigger(trigger);

        for (Task t : tasks) {
            if (!t.isEnabled()) {
                continue;
            }

            TaskEvent action;

            try {
                action = taskService.getActionEventFor(t);
            } catch (ActionNotFoundException e) {
                registerError(t, "error.actionNotFound");
                continue;
            }

            String subject = action.getSubject();

            if (StringUtils.isBlank(subject)) {
                registerError(t, "error.actionWithoutSubject");
                continue;
            }

            List<EventParameter> actionEventParameters = action.getEventParameters();
            Map<String, Object> parameters = new HashMap<>(actionEventParameters.size());
            boolean send = true;

            for (EventParameter param : actionEventParameters) {
                String key = param.getEventKey();
                String value = replaceAll(t.getActionInputFields().get(key), trigger.getEventParameters(), triggerEvent);

                if (StringUtils.isBlank(value)) {
                    registerError(t, "error.wrongActionInputFields");
                    send = false;
                    break;
                }

                parameters.put(key, value);
            }

            if (send) {
                eventRelay.sendEventMessage(new MotechEvent(subject, parameters));
                statusMessageService.addSuccess(t);
            }
        }
    }

    public final void registerHandlerFor(final String subject) {
        Method method = ReflectionUtils.findMethod(AopUtils.getTargetClass(this), "handler");
        EventListener proxy = new MotechListenerEventProxy(SERVICE_NAME, this, method);

        try {
            registryService.registerListener(proxy, subject);
            LOG.info(String.format("Register TaskTriggerHandler for subject: '%s'", subject));
        } catch (Exception e) {
            LOG.error(String.format("Cant register TaskTriggerHandler for subject: %s", subject), e);
        }
    }

    private void registerHandler() {
        List<Task> tasks = taskService.getAllTasks();
        List<String> subjects = new ArrayList<>();

        for (Task t : tasks) {
            subjects.add(getSubject(t.getTrigger()));
        }

        for (String subject : subjects) {
            registerHandlerFor(subject);
        }
    }

    private void registerError(final Task task, final String message) {
        statusMessageService.addError(task, message);
        LOG.error(message);

        int errorRunsCount = statusMessageService.errorsFromLastRun(task).size();
        int possibleErrorRun = Integer.valueOf(settingsFacade.getProperty(TASK_POSSIBLE_ERRORS_KEY));

        if (errorRunsCount >= possibleErrorRun) {
            task.setEnabled(false);
            taskService.save(task);
            statusMessageService.addWarning(task);
        }
    }

    private String replaceAll(final String template, final List<EventParameter> triggerEventParameters, final MotechEvent triggerEvent) {
        String replaced = template;

        if (replaced != null) {
            for (EventParameter parameter : triggerEventParameters) {
                String key = parameter.getEventKey();
                String value = String.valueOf(triggerEvent.getParameters().get(key));

                replaced = replaced.replaceAll(String.format("\\{\\{%s\\}\\}", key), value);
            }
        }

        return replaced;
    }

}
