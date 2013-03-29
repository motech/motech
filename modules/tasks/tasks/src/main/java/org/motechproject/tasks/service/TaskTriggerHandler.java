package org.motechproject.tasks.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.motechproject.tasks.EventKeys;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskAdditionalData;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskActionException;
import org.motechproject.tasks.ex.TaskException;
import org.motechproject.tasks.ex.TaskTriggerException;
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
import java.util.SortedSet;

import static org.motechproject.tasks.service.HandlerUtil.checkFilters;
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
                } catch (TaskActionException e) {
                    registerError(task, e);
                    Map<String, Object> eventParam = createEventParameters(task, e);
                    eventRelay.sendEventMessage(new MotechEvent("ACTION_FAILED", eventParam));
                } catch (TaskTriggerException e) {
                    registerError(task, e);
                    Map<String, Object> eventParam = createEventParameters(task, e);
                    eventRelay.sendEventMessage(new MotechEvent("TRIGGER_FAILED", eventParam));
                } catch (Exception e) {
                    TaskException exp = new TaskException("error.unrecognizedError", e);
                    registerError(task, exp);
                    Map<String, Object> eventParam = createEventParameters(task, exp);
                    eventRelay.sendEventMessage(new MotechEvent("TRIGGER_FAILED", eventParam));
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

    private ActionEvent getActionEvent(Task task) throws TaskTriggerException {
        ActionEvent action;

        try {
            action = taskService.getActionEventFor(task);
            LOG.info("Found action for task: " + task);
        } catch (ActionNotFoundException e) {
            throw new TaskTriggerException("error.actionNotFound", e);
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

    private void executeAction(Task task, ActionEvent action, Map<String, Object> parameters) throws TaskActionException {
        boolean invokeMethod = action.hasService() && bundleContext != null;
        boolean serviceAvailable = false;

        if (invokeMethod) {
            serviceAvailable = callActionServiceMethod(action, new MethodHandler(action, parameters));

            if (!serviceAvailable) {
                activityService.addWarning(task, "warning.serviceUnavailable", action.getServiceInterface());
            }
        }

        boolean sendEvent = (!invokeMethod || !serviceAvailable) && action.hasSubject();

        if (sendEvent) {
            eventRelay.sendEventMessage(new MotechEvent(action.getSubject(), parameters));
        }

        if ((!invokeMethod || !serviceAvailable) && !sendEvent) {
            throw new TaskActionException("error.cantExecuteAction");
        }
    }

    private boolean callActionServiceMethod(ActionEvent action, MethodHandler methodHandler) throws TaskActionException {
        ServiceReference reference = bundleContext.getServiceReference(action.getServiceInterface());
        boolean serviceAvailable = reference != null;

        if (serviceAvailable) {
            Object service = bundleContext.getService(reference);
            String serviceMethod = action.getServiceMethod();
            Class[] classes = methodHandler.isParametrized() ? methodHandler.getClasses() : null;
            Object[] objects = methodHandler.isParametrized() ? methodHandler.getObjects() : null;

            try {
                Method method = service.getClass().getMethod(serviceMethod, classes);

                try {
                    method.invoke(service, objects);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new TaskActionException("error.serviceMethodInvokeError", e, serviceMethod, action.getServiceInterface());
                }
            } catch (NoSuchMethodException e) {
                throw new TaskActionException("error.notFoundMethodForService", e, serviceMethod, action.getServiceInterface());
            }
        }

        return serviceAvailable;
    }

    private Map<String, Object> createParameters(Task task, SortedSet<ActionParameter> actionParameters, MotechEvent event) throws TaskTriggerException {
        Map<String, Object> parameters = new HashMap<>(actionParameters.size());

        for (ActionParameter param : actionParameters) {
            String key = param.getKey();

            if (!task.getActionInputFields().containsKey(key)) {
                throw new TaskTriggerException("error.taskNotContainsField", key);
            }

            String template = task.getActionInputFields().get(key);

            if (template == null) {
                throw new TaskTriggerException("error.templateNull", key);
            }

            String userInput = replaceAll(template, event, task);

            Object value;

            switch (param.getType()) {
                case DOUBLE:
                    try {
                        value = Double.valueOf(userInput);
                    } catch (Exception e) {
                        throw new TaskTriggerException("error.convertToDouble", e, key);
                    }
                    break;
                case INTEGER:
                    try {
                        value = Integer.valueOf(userInput);
                    } catch (Exception e) {
                        throw new TaskTriggerException("error.convertToInteger", e, key);
                    }
                    break;
                case DATE:
                    try {
                        value = DateTime.parse(userInput, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z"));
                    } catch (Exception e) {
                        throw new TaskTriggerException("error.convertToDate", e, key);
                    }
                    break;
                default:
                    value = userInput;
            }

            parameters.put(key, value);
        }

        return parameters;
    }

    private String replaceAll(String template, MotechEvent event, Task task) throws TaskTriggerException {
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

    private String getAdditionalDataKey(MotechEvent event, Task task, KeyInformation key) throws TaskTriggerException {
        if (dataProviders == null || dataProviders.isEmpty()) {
            throw new TaskTriggerException("error.notFoundDataProvider", key.getObjectType());
        }

        DataProvider provider = dataProviders.get(key.getDataProviderId());

        if (provider == null) {
            throw new TaskTriggerException("error.notFoundDataProvider", key.getObjectType());
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
        String value = "";
        if (found == null) {
            if (ad.isFailIfDataNotFound()) {
                throw new TaskTriggerException("error.notFoundObjectForType", key.getObjectType());
            } else {
                activityService.addWarning(task, "warning.notFoundObjectForType", key.getObjectType());
            }
        } else {
            value = getAdditionalDataValue(found, key, ad, task);
        }

        return value;
    }

    private String getAdditionalDataValue(Object found, KeyInformation key, TaskAdditionalData ad, Task task) throws TaskTriggerException {
        String value;
        try {
            value = getFieldValue(found, key.getEventKey());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            if (ad.isFailIfDataNotFound()) {
                throw new TaskTriggerException("error.objectNotContainsField", e, key.getEventKey());
            } else {
                activityService.addWarning(task, "warning.objectNotContainsField", key.getEventKey(), e);
                value = "";
            }
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

    private String manipulateValue(String value, List<String> manipulations, Task task) throws TaskTriggerException {
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
                    throw new TaskTriggerException("error.date.format", e, manipulation);
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

    private Map<String, Object> createEventParameters(Task task, TaskException e) {
        Map<String, Object> param = new HashMap<>();
        param.put(EventKeys.TASK_FAIL_MESSAGE, e.getMessageKey());
        param.put(EventKeys.TASK_FAIL_STACK_TRACE, ExceptionUtils.getStackTrace(e));
        param.put(EventKeys.TASK_FAIL_FAILURE_DATE, DateTime.now());
        param.put(EventKeys.TASK_FAIL_FAILURE_NUMBER, activityService.errorsFromLastRun(task));
        param.put(EventKeys.TASK_FAIL_TRIGGER_DISABLED, task.isEnabled());
        param.put(EventKeys.TASK_FAIL_TASK_ID, task.getId());
        param.put(EventKeys.TASK_FAIL_TASK_NAME, task.getName());
        return param;
    }

    @Autowired(required = false)
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
