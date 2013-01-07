package org.motechproject.tasks.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.tasks.domain.EventParamType;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.OperatorType;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
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
    private TaskActivityService activityService;
    private EventListenerRegistryService registryService;
    private EventRelay eventRelay;
    private SettingsFacade settingsFacade;

    @Autowired
    public TaskTriggerHandler(final TaskService taskService, final TaskActivityService activityService,
                              final EventListenerRegistryService registryService, final EventRelay eventRelay,
                              final SettingsFacade settingsFacade) {
        this.taskService = taskService;
        this.activityService = activityService;
        this.registryService = registryService;
        this.eventRelay = eventRelay;
        this.settingsFacade = settingsFacade;

        registerHandler();
    }

    public void handle(final MotechEvent triggerEvent) {
        TaskEvent trigger = null;

        try {
            String subject = triggerEvent.getSubject();
            trigger = taskService.findTrigger(subject);
            LOG.info("Found trigger for subject: " + subject);
        } catch (TriggerNotFoundException e) {
            LOG.error(e.getMessage());
        }

        if (trigger != null) {
            for (Task task : taskService.findTasksForTrigger(trigger)) {
                if (!task.isEnabled()) {
                    logOmittedTask(task, new Exception("Task is disabled"));
                    continue;
                }

                TaskEvent action;

                try {
                    action = taskService.getActionEventFor(task);
                    LOG.info("Found action for task: " + task);
                } catch (ActionNotFoundException e) {
                    TaskException exception = new TaskException("error.actionNotFound", e);
                    registerError(task, exception);
                    logOmittedTask(task, exception);
                    continue;
                }

                String subject = action.getSubject();

                if (StringUtils.isBlank(subject)) {
                    TaskException exception = new TaskException("error.actionWithoutSubject");
                    registerError(task, exception);
                    logOmittedTask(task, exception);
                    continue;
                }

                if (task.hasFilters() && !checkFilters(task.getFilters(), triggerEvent.getParameters())) {
                    logOmittedTask(task, new Exception("Filter criteria not met"));
                    continue;
                }

                try {
                    Map<String, Object> parameters = createParameters(task, action.getEventParameters(), trigger, triggerEvent);
                    eventRelay.sendEventMessage(new MotechEvent(subject, parameters));
                    activityService.addSuccess(task);
                } catch (TaskException e) {
                    registerError(task, e);
                    logOmittedTask(task, e);
                }
            }
        }
    }

    public final void registerHandlerFor(final String subject) {
        Method method = ReflectionUtils.findMethod(AopUtils.getTargetClass(this), "handle", MotechEvent.class);

        try {
            if (method != null) {
                EventListener proxy = new MotechListenerEventProxy(SERVICE_NAME, this, method);
                registryService.registerListener(proxy, subject);
                LOG.info(String.format("Register TaskTriggerHandler for subject: '%s'", subject));
            }
        } catch (Exception e) {
            LOG.error(String.format("Cant register TaskTriggerHandler for subject: %s", subject), e);
        }
    }

    private void registerHandler() {
        List<Task> tasks = taskService.getAllTasks();

        for (Task t : tasks) {
            registerHandlerFor(getSubject(t.getTrigger()));
        }
    }

    private void registerError(final Task task, final TaskException e) {
        activityService.addError(task, e);

        int errorRunsCount = activityService.errorsFromLastRun(task).size();
        int possibleErrorRun = Integer.valueOf(settingsFacade.getProperty(TASK_POSSIBLE_ERRORS_KEY));

        if (errorRunsCount >= possibleErrorRun) {
            task.setEnabled(false);
            taskService.save(task);
            activityService.addWarning(task);
        }
    }

    private Map<String, Object> createParameters(Task task, List<EventParameter> actionParameters, TaskEvent trigger, MotechEvent event) throws TaskException {
        Map<String, Object> parameters = new HashMap<>(actionParameters.size());

        for (EventParameter param : actionParameters) {
            final String key = param.getEventKey();
            String template = task.getActionInputFields().get(key);

            if (template == null) {
                throw new TaskException("error.templateNull", key);
            }

            String userInput = replaceAll(template, trigger.getEventParameters(), event);
            Object value;

            if (param.getType().isNumber()) {
                BigDecimal decimal;

                try {
                    decimal = new BigDecimal(userInput);
                } catch (Exception e) {
                    throw new TaskException("error.convertToNumber", key, e);
                }

                if (decimal.signum() == 0 || decimal.scale() <= 0 || decimal.stripTrailingZeros().scale() <= 0) {
                    value = decimal.intValueExact();
                } else {
                    value = decimal.doubleValue();
                }
            } else {
                value = userInput;
            }

            parameters.put(key, value);
        }

        return parameters;
    }

    private String replaceAll(final String template, final List<EventParameter> triggerParameters, final MotechEvent event) {
        String replaced = template;

        for (EventParameter param : triggerParameters) {
            String key = param.getEventKey();

            if (event.getParameters().containsKey(key)) {
                String value = String.valueOf(event.getParameters().get(key));
                replaced = replaced.replaceAll(String.format("\\{\\{%s\\}\\}", key), value);
            }
        }

        return replaced;
    }

    private boolean checkFilters(List<Filter> filters, Map<String, Object> triggerParameters) {
        boolean filterCheck = false;

        for (Filter filter : filters) {
            EventParameter eventParameter = filter.getEventParameter();

            if (triggerParameters.containsKey(eventParameter.getEventKey())) {
                EventParamType type = eventParameter.getType();
                Object object = triggerParameters.get(eventParameter.getEventKey());

                if (type.isString()) {
                    filterCheck = checkFilterForString(filter, (String) object);
                } else if (type.isNumber()) {
                    filterCheck = checkFilterForNumber(filter, new BigDecimal(object.toString()));
                }

                if (!filter.isNegationOperator()) {
                    filterCheck = !filterCheck;
                }
            }

            if (!filterCheck) {
                break;
            }
        }

        return filterCheck;
    }

    private boolean checkFilterForString(Filter filter, String param) {
        String expression = filter.getExpression();

        switch (OperatorType.fromString(filter.getOperator())) {
            case EQUALS:
                return param.equals(expression);
            case CONTAINS:
                return param.contains(expression);
            case EXIST:
                return true;
            case STARTSWITH:
                return param.startsWith(expression);
            case ENDSWITH:
                return param.endsWith(expression);
            default:
                return false;
        }
    }

    private boolean checkFilterForNumber(Filter filter, BigDecimal param) {
        if (OperatorType.fromString(filter.getOperator()) == OperatorType.EXIST) {
            return true;
        }

        int compare = param.compareTo(new BigDecimal(filter.getExpression()));

        switch (OperatorType.fromString(filter.getOperator())) {
            case EQUALS:
                return compare == 0;
            case GT:
                return compare == 1;
            case LT:
                return compare == -1;
            default:
                return false;
        }
    }

    private void logOmittedTask(Task task, Throwable e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Omitted task with ID: %s because: ", task.getId()), e);
        }
    }
}
