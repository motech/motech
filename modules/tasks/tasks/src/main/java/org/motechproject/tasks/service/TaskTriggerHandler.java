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
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskAdditionalData;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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
    private BundleContext bundleContext;

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
        TriggerEvent trigger = getTriggerEvent(triggerEvent.getSubject());

        if (trigger != null) {
            for (Task task : selectTasks(trigger, triggerEvent.getParameters())) {
                try {
                    ActionEvent action = getActionEvent(task);
                    Map<String, Object> parameters = createParameters(task, action.getActionParameters(), triggerEvent);

                    executeAction(task, action, parameters);
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
                registerHandlerFor(t.getTrigger().getSubject());
            }
        }
    }

    private TriggerEvent getTriggerEvent(String subject) {
        TriggerEvent trigger = null;

        try {
            trigger = taskService.findTrigger(subject);
            LOG.info("Found trigger for subject: " + subject);
        } catch (TriggerNotFoundException e) {
            LOG.error(e.getMessage(), e);
        }

        return trigger;
    }

    private ActionEvent getActionEvent(Task task) throws TaskException {
        ActionEvent action;

        try {
            action = taskService.getActionEventFor(task);
            LOG.info("Found action for task: " + task);
        } catch (ActionNotFoundException e) {
            throw new TaskException("error.actionNotFound", e);
        }

        return action;
    }

    private List<Task> selectTasks(TriggerEvent trigger, Map<String, Object> triggerParameters) {
        List<Task> tasks = new ArrayList<>();

        for (Task task : taskService.findTasksForTrigger(trigger)) {
            if (task.isEnabled() && checkFilters(task.getFilters(), triggerParameters)) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    private void executeAction(Task task, ActionEvent action, Map<String, Object> parameters) throws TaskException {
        boolean invokeMethod = action.hasService() && bundleContext != null;
        boolean serviceAvailable = false;

        if (invokeMethod) {
            serviceAvailable = callActionServiceMethod(action, parameters);

            if (!serviceAvailable) {
                activityService.addWarning(task, "warning.serviceUnavailable", action.getServiceInterface());
            }
        }

        boolean sendEvent = (!invokeMethod || !serviceAvailable) && action.hasSubject();

        if (sendEvent) {
            eventRelay.sendEventMessage(new MotechEvent(action.getSubject(), parameters));
        }

        if ((!invokeMethod || !serviceAvailable) && !sendEvent) {
            throw new TaskException("error.cantExecuteAction");
        }
    }

    private boolean callActionServiceMethod(ActionEvent action, Map<String, Object> parameters) throws TaskException {
        ServiceReference reference = bundleContext.getServiceReference(action.getServiceInterface());
        boolean serviceAvailable = reference != null;

        if (serviceAvailable) {
            Object service = bundleContext.getService(reference);

            try {
                Method serviceMethod = service.getClass().getMethod(action.getServiceMethod(), Map.class);

                try {
                    serviceMethod.invoke(service, parameters);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new TaskException("error.serviceMethodInvokeError", e, action.getServiceMethod(), action.getServiceInterface());
                }
            } catch (NoSuchMethodException e) {
                throw new TaskException("error.notFoundMethodForService", e, action.getServiceMethod(), action.getServiceInterface());
            }
        }

        return serviceAvailable;
    }

    private Map<String, Object> createParameters(Task task, List<ActionParameter> actionParameters, MotechEvent event) throws TaskException {
        Map<String, Object> parameters = new HashMap<>(actionParameters.size());

        for (ActionParameter param : actionParameters) {
            String key = param.getKey();

            if (!task.getActionInputFields().containsKey(key)) {
                throw new TaskException("error.taskNotContainsField", key);
            }

            String template = task.getActionInputFields().get(key);

            if (template == null) {
                throw new TaskException("error.templateNull", key);
            }

            String userInput = replaceAll(template, event, task);

            Object value;

            switch (param.getType()) {
                case NUMBER:
                    try {
                        value = convertToNumber(userInput);
                    } catch (Exception e) {
                        throw new TaskException("error.convertToNumber", e, key);
                    }
                    break;
                case DATE:
                    try {
                        value = convertToDate(userInput);
                    } catch (Exception e) {
                        throw new TaskException("error.convertToDate", e, key);
                    }
                    break;
                default:
                    value = userInput;
            }

            parameters.put(key, value);
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

    @Autowired(required = false)
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
