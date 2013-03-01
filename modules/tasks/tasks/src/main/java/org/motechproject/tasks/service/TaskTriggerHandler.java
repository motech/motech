package org.motechproject.tasks.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskAdditionalData;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.tasks.service.HandlerUtil.checkFilters;
import static org.motechproject.tasks.service.HandlerUtil.convertToDate;
import static org.motechproject.tasks.service.HandlerUtil.convertToNumber;
import static org.motechproject.tasks.service.HandlerUtil.findAdditionalData;
import static org.motechproject.tasks.service.HandlerUtil.getFieldValue;
import static org.motechproject.tasks.service.HandlerUtil.getKeys;
import static org.motechproject.tasks.service.HandlerUtil.getTriggerKey;
import static org.motechproject.tasks.util.TaskUtil.getSubject;
import static org.springframework.util.ReflectionUtils.findMethod;

@Service
public class TaskTriggerHandler {
    private static final String TASK_POSSIBLE_ERRORS_KEY = "task.possible.errors";
    private static final int JOIN_PATTERN_BEGIN_INDEX = 5;
    private static final int DATETIME_PATTERN_BEGIN_INDEX = 9;

    private static final Logger LOG = LoggerFactory.getLogger(TaskTriggerHandler.class);

    private TaskService taskService;
    private TaskActivityService activityService;
    private EventListenerRegistryService registryService;
    private EventRelay eventRelay;
    private SettingsFacade settingsFacade;
    private Map<String, DataProvider> dataProviders;

    @Autowired
    public TaskTriggerHandler(TaskService taskService, TaskActivityService activityService,
                              EventListenerRegistryService registryService, EventRelay eventRelay,
                              SettingsFacade settingsFacade) {
        this.taskService = taskService;
        this.activityService = activityService;
        this.registryService = registryService;
        this.eventRelay = eventRelay;
        this.settingsFacade = settingsFacade;

        registerHandler();
    }

    public final void registerHandlerFor(String subject) {
        String serviceName = "taskTriggerHandler";
        Method method = findMethod(this.getClass(), "handle", MotechEvent.class);
        Object obj = CollectionUtils.find(registryService.getListeners(subject), new HandlerPredicate(serviceName));

        try {
            if (method != null && obj == null) {
                EventListener proxy = new MotechListenerEventProxy(serviceName, this, method);

                registryService.registerListener(proxy, subject);
                LOG.info(String.format("Register TaskTriggerHandler for subject: '%s'", subject));
            }
        } catch (Exception e) {
            LOG.error(String.format("Cant register TaskTriggerHandler for subject: %s", subject), e);
        }
    }

    public void handle(MotechEvent triggerEvent) {
        TaskEvent trigger = getTriggerEvent(triggerEvent.getSubject());

        if (trigger != null) {
            for (Task task : selectTasks(trigger, triggerEvent.getParameters())) {
                try {
                    TaskEvent action = getActionEvent(task);
                    Map<String, Object> parameters = createParameters(task, action.getEventParameters(), triggerEvent);

                    eventRelay.sendEventMessage(new MotechEvent(action.getSubject(), parameters));
                    activityService.addSuccess(task);
                } catch (TaskException e) {
                    registerError(task, e);
                }
            }
        }
    }

    private void registerHandler() {
        if (taskService != null) {
            List<Task> tasks = taskService.getAllTasks();

            for (Task t : tasks) {
                registerHandlerFor(getSubject(t.getTrigger()));
            }
        }
    }

    private TaskEvent getTriggerEvent(String subject) {
        TaskEvent trigger = null;

        try {
            trigger = taskService.findTrigger(subject);
            LOG.info("Found trigger for subject: " + subject);
        } catch (TriggerNotFoundException e) {
            LOG.error(e.getMessage(), e);
        }

        return trigger;
    }

    private TaskEvent getActionEvent(Task task) throws TaskException {
        TaskEvent action;

        try {
            action = taskService.getActionEventFor(task);
            LOG.info("Found action for task: " + task);
        } catch (ActionNotFoundException e) {
            throw new TaskException("error.actionNotFound", e);
        }

        return action;
    }

    private List<Task> selectTasks(TaskEvent trigger, Map<String, Object> triggerParameters) {
        List<Task> tasks = new ArrayList<>();

        for (Task task : taskService.findTasksForTrigger(trigger)) {
            if (task.isEnabled() && checkFilters(task.getFilters(), triggerParameters)) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    private Map<String, Object> createParameters(Task task, List<EventParameter> actionParameters, MotechEvent event) throws TaskException {
        Map<String, Object> parameters = new HashMap<>(actionParameters.size());

        for (EventParameter param : actionParameters) {
            String eventKey = param.getEventKey();

            if (!task.getActionInputFields().containsKey(eventKey)) {
                throw new TaskException("error.taskNotContainsField", eventKey);
            }

            String template = task.getActionInputFields().get(eventKey);

            if (template == null) {
                throw new TaskException("error.templateNull", eventKey);
            }

            String userInput = replaceAll(template, event, task);

            Object value;

            switch (param.getType()) {
                case NUMBER:
                    try {
                        value = convertToNumber(userInput);
                    } catch (Exception e) {
                        throw new TaskException("error.convertToNumber", e, eventKey);
                    }
                    break;
                case DATE:
                    try {
                        value = convertToDate(userInput);
                    } catch (Exception e) {
                        throw new TaskException("error.convertToDate", e, eventKey);
                    }
                    break;
                default:
                    value = userInput;
            }

            parameters.put(eventKey, value);
        }

        return parameters;
    }

    private String replaceAll(String template, MotechEvent event, Task task) throws TaskException {
        String conversionTemplate = template;

        for (KeyInformation key : getKeys(template)) {
            String value = "";

            if (key.fromTrigger()) {
                value = getTriggerKey(event, key);
            } else if (key.fromAdditionalData()) {
                value = getAdditionalDataKey(event, task, key);
            }

            String replacedValue = key.getManipulations() != null ? manipulateValue(value, key.getManipulations(), task) : value;
            conversionTemplate = conversionTemplate.replace(String.format("{{%s}}", key.getOriginalKey()), replacedValue);
        }

        return conversionTemplate;
    }

    private String getAdditionalDataKey(MotechEvent event, Task task, KeyInformation key) throws TaskException {
        if (dataProviders == null || dataProviders.isEmpty()) {
            throw new TaskException("error.notFoundDataProvider", key.getObjectType());
        }

        DataProvider provider = dataProviders.get(key.getDataProviderId());

        if (provider == null) {
            throw new TaskException("error.notFoundDataProvider", key.getObjectType());
        }

        TaskAdditionalData ad = findAdditionalData(task, key);
        KeyInformation adKey = new KeyInformation(ad.getLookupValue());

        Map<String, String> lookupFields = new HashMap<>();

        if (adKey.fromTrigger()) {
            lookupFields.put(ad.getLookupField(), event.getParameters().get(adKey.getEventKey()).toString());
        } else if (adKey.fromAdditionalData()) {
            String objectValue = getAdditionalDataKey(event, task, adKey);
            lookupFields.put(ad.getLookupField(), objectValue);
        }

        Object found = provider.lookup(key.getObjectType(), lookupFields);

        if (found == null) {
            throw new TaskException("error.notFoundObjectForType", key.getObjectType());
        }

        String value;

        try {
            value = getFieldValue(found, key.getEventKey());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new TaskException("error.objectNotContainsField", e, key.getEventKey());
        }

        return value;
    }

    private void registerError(Task task, TaskException e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Omitted task with ID: %s because: ", task.getId()), e);
        }

        activityService.addError(task, e);

        int errorRunsCount = activityService.errorsFromLastRun(task).size();
        int possibleErrorRun = Integer.valueOf(settingsFacade.getProperty(TASK_POSSIBLE_ERRORS_KEY));

        if (errorRunsCount >= possibleErrorRun) {
            task.setEnabled(false);
            taskService.save(task);
            activityService.addWarning(task);
        }
    }

    private String manipulateValue(String value, List<String> manipulations, Task task) throws TaskException {
        String manipulateValue = value;

        for (String manipulation : manipulations) {
            String lowerCase = manipulation.toLowerCase();

            if (!lowerCase.startsWith("join") && !lowerCase.startsWith("datetime")) {
                switch (lowerCase) {
                    case "toupper":
                        manipulateValue = manipulateValue.toUpperCase();
                        break;
                    case "tolower":
                        manipulateValue = manipulateValue.toLowerCase();
                        break;
                    case "capitalize":
                        manipulateValue = WordUtils.capitalize(manipulateValue);
                        break;
                    default:
                        activityService.addWarning(task, "warning.manipulation", manipulation);
                        break;
                }
            } else if (lowerCase.contains("join")) {
                String[] splitValue = manipulateValue.split(" ");
                String pattern = manipulation.substring(JOIN_PATTERN_BEGIN_INDEX, manipulation.length() - 1);

                manipulateValue = StringUtils.join(splitValue, pattern);
            } else if (lowerCase.contains("datetime")) {
                try {
                    String pattern = manipulation.substring(DATETIME_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
                    DateTimeFormatter format = DateTimeFormat.forPattern(pattern);

                    manipulateValue = format.print(new DateTime(manipulateValue));
                } catch (IllegalArgumentException e) {
                    throw new TaskException("error.date.format", e, manipulation);
                }
            } else {
                activityService.addWarning(task, "warning.manipulation", manipulation);
            }
        }

        return manipulateValue;
    }

    public void addDataProvider(String taskDataProviderId, DataProvider provider) {
        if (dataProviders == null) {
            dataProviders = new HashMap<>();
        }

        dataProviders.put(taskDataProviderId, provider);
    }

    public void removeDataProvider(String taskDataProviderId) {
        if (dataProviders != null && !dataProviders.isEmpty()) {
            dataProviders.remove(taskDataProviderId);
        }
    }

    void setDataProviders(Map<String, DataProvider> dataProviders) {
        this.dataProviders = dataProviders;
    }
}
