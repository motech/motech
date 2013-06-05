package org.motechproject.tasks.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskAdditionalData;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskHandlerException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

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
import static org.motechproject.tasks.events.constants.TaskFailureCause.ACTION;
import static org.motechproject.tasks.events.constants.TaskFailureCause.DATA_SOURCE;
import static org.motechproject.tasks.events.constants.TaskFailureCause.FILTER;
import static org.motechproject.tasks.events.constants.TaskFailureCause.TRIGGER;
import static org.motechproject.tasks.service.HandlerPredicates.activeTasks;
import static org.motechproject.tasks.service.HandlerPredicates.withServiceName;

@Service
public class TaskTriggerHandler {
    private static final String TASK_POSSIBLE_ERRORS_KEY = "task.possible.errors";

    private static final Logger LOG = LoggerFactory.getLogger(TaskTriggerHandler.class);

    private TaskService taskService;
    private TaskActivityService activityService;
    private EventListenerRegistryService registryService;
    private EventRelay eventRelay;
    private SettingsFacade settings;
    private Map<String, DataProvider> dataProviders;
    private BundleContext bundleContext;

    @Autowired
    public TaskTriggerHandler(TaskService taskService, TaskActivityService activityService,
                              EventListenerRegistryService registryService, EventRelay eventRelay,
                              @Qualifier("tasksSettings") SettingsFacade settings) {
        this.taskService = taskService;
        this.activityService = activityService;
        this.registryService = registryService;
        this.eventRelay = eventRelay;
        this.settings = settings;

        for (Task task : taskService.getAllTasks()) {
            registerHandlerFor(task.getTrigger().getSubject());
        }
    }

    public final void registerHandlerFor(String subject) {
        String serviceName = "taskTriggerHandler";
        Method method = ReflectionUtils.findMethod(this.getClass(), "handle", MotechEvent.class);
        Object obj = CollectionUtils.find(registryService.getListeners(subject), withServiceName(serviceName));

        try {
            if (method != null && obj == null) {
                EventListener proxy = new MotechListenerEventProxy(serviceName, this, method);

                registryService.registerListener(proxy, subject);
                LOG.info(String.format("%s listens on subject %s", serviceName, subject));
            }
        } catch (Exception e) {
            LOG.error(String.format("%s can not listen on subject %s due to:", serviceName, subject), e);
        }
    }

    public void handle(MotechEvent event) throws TriggerNotFoundException {
        TriggerEvent trigger = taskService.findTrigger(event.getSubject());
        List<Task> tasks = taskService.findTasksForTrigger(trigger);

        CollectionUtils.filter(tasks, activeTasks());
        for (Task task : tasks) {
            try {
                if (passFilters(event, task)) {
                    for (TaskActionInformation actionInformation : task.getActions()) {
                        ActionEvent action = getActionEvent(actionInformation);
                        Map<String, Object> parameters = createParameters(task, actionInformation, action, event);

                        executeAction(task, action, parameters);
                    }

                    handleSuccess(event, task);
                }
            } catch (TaskHandlerException e) {
                handleError(event, task, e);
            } catch (Exception e) {
                handleError(event, task, new TaskHandlerException(TRIGGER, "error.unrecognizedError", e));
            }
        }
    }

    private boolean passFilters(MotechEvent event, Task task) throws TaskHandlerException {
        boolean result;

        try {
            result = HandlerUtil.checkFilters(task.getFilters(), event.getParameters());
        } catch (Exception e) {
            throw new TaskHandlerException(FILTER, "error.filterError", e);
        }

        return result;
    }

    private ActionEvent getActionEvent(TaskActionInformation actionInformation) throws TaskHandlerException {
        ActionEvent action;

        try {
            action = taskService.getActionEventFor(actionInformation);
            LOG.info("Found action on the basic of information: " + actionInformation);
        } catch (ActionNotFoundException e) {
            throw new TaskHandlerException(TRIGGER, "error.actionNotFound", e);
        }

        return action;
    }

    private void executeAction(Task task, ActionEvent action, Map<String, Object> parameters) throws TaskHandlerException {
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
            throw new TaskHandlerException(ACTION, "error.cantExecuteAction");
        }
    }

    private boolean callActionServiceMethod(ActionEvent action, MethodHandler methodHandler) throws TaskHandlerException {
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
                    throw new TaskHandlerException(ACTION, "error.serviceMethodInvokeError", e, serviceMethod, action.getServiceInterface());
                }
            } catch (NoSuchMethodException e) {
                throw new TaskHandlerException(ACTION, "error.notFoundMethodForService", e, serviceMethod, action.getServiceInterface());
            }
        }

        return serviceAvailable;
    }

    private Map<String, Object> createParameters(Task task, TaskActionInformation info, ActionEvent action, MotechEvent event) throws TaskHandlerException {
        SortedSet<ActionParameter> actionParameters = action.getActionParameters();
        Map<String, Object> parameters = new HashMap<>(actionParameters.size());

        for (ActionParameter param : actionParameters) {
            String key = param.getKey();

            if (!info.getValues().containsKey(key)) {
                throw new TaskHandlerException(TRIGGER, "error.taskActionNotContainsField", action.getDisplayName(), key);
            }

            String template = info.getValues().get(key);

            if (template == null) {
                throw new TaskHandlerException(TRIGGER, "error.templateNull", key, action.getDisplayName());
            }

            switch (param.getType()) {
                case LIST:
                    parameters.put(key, convertToList(task, event, template));
                    break;
                case MAP:
                    parameters.put(key, convertToMap(task, event, template));
                    break;
                default:
                    try {
                        String userInput = convert(template, event, task);
                        Object obj = HandlerUtil.convertTo(param.getType(), userInput);
                        parameters.put(key, obj);
                    } catch (MotechException ex) {
                        throw new TaskHandlerException(TRIGGER, ex.getMessage(), ex, key);
                    }
            }
        }

        return parameters;
    }

    private Map<Object, Object> convertToMap(Task task, MotechEvent event, String template) throws TaskHandlerException {
        String[] rows = template.split("(\\r)?\\n");
        Map<Object, Object> tempMap = new HashMap<>(rows.length);

        for (String row : rows) {
            String[] array = row.split(":");
            Object mapKey;
            Object mapValue;

            switch (array.length) {
                case 2:
                    mapKey = getValue(array[0], event, task);
                    mapValue = getValue(array[1], event, task);

                    tempMap.put(mapKey, mapValue);
                    break;
                case 1:
                    mapValue = getValue(array[0], event, task);

                    tempMap.putAll((Map) mapValue);
                    break;
                default:
            }
        }
        return tempMap;
    }

    private List<Object> convertToList(Task task, MotechEvent event, String template) throws TaskHandlerException {
        String[] rows = template.split("(\\r)?\\n");
        List<Object> tempList = new ArrayList<>(rows.length);

        for (String row : rows) {
            Object value = getValue(row, event, task);

            if (value instanceof Collection) {
                tempList.addAll((Collection) value);
            } else {
                tempList.add(value);
            }
        }

        return tempList;
    }

    private Object getValue(String row, MotechEvent event, Task task) throws TaskHandlerException {
        List<KeyInformation> keys = KeyInformation.parseAll(row);
        Object result = null;

        if (keys.isEmpty()) {
            result = row;
        } else {
            KeyInformation rowKeyInfo = keys.get(0);

            if (rowKeyInfo.fromTrigger()) {
                result = event.getParameters().get(rowKeyInfo.getKey());
            } else if (rowKeyInfo.fromAdditionalData()) {
                result = getAdditionalDataValue(event, task, rowKeyInfo);
            }
        }

        return result;
    }

    private String convert(String template, MotechEvent event, Task task) throws TaskHandlerException {
        String conversionTemplate = template;

        for (KeyInformation key : KeyInformation.parseAll(template)) {
            String value = "";

            if (key.fromTrigger()) {
                try {
                    Object triggerKey = HandlerUtil.getTriggerKey(event, key);
                    value = triggerKey == null ? "" : triggerKey.toString();
                } catch (Exception e) {
                    throw new TaskHandlerException(TRIGGER, "error.objectNotContainsField", e, key.getKey());
                }
            } else if (key.fromAdditionalData()) {
                Object additionalDataValue = getAdditionalDataValue(event, task, key);
                value = additionalDataValue == null ? "" : additionalDataValue.toString();
            }

            if (key.hasManipulations()) {
                value = manipulateValue(value, key.getManipulations(), task);
            }

            conversionTemplate = conversionTemplate.replace(String.format("{{%s}}", key.getOriginalKey()), value);
        }

        return conversionTemplate;
    }

    private Object getAdditionalDataObject(MotechEvent event, Task task, KeyInformation key) throws TaskHandlerException {
        if (dataProviders == null || dataProviders.isEmpty()) {
            throw new TaskHandlerException(DATA_SOURCE, "error.notFoundDataProvider", key.getObjectType());
        }

        DataProvider provider = dataProviders.get(key.getDataProviderId());

        if (provider == null) {
            throw new TaskHandlerException(DATA_SOURCE, "error.notFoundDataProvider", key.getObjectType());
        }

        TaskAdditionalData ad = task.getAdditionalData(key.getDataProviderId(), key.getObjectId(), key.getObjectType());
        String value = convert(ad.getLookupValue(), event, task);

        Map<String, String> lookupFields = new HashMap<>();
        lookupFields.put(ad.getLookupField(), value);

        return provider.lookup(key.getObjectType(), lookupFields);
    }

    private Object getAdditionalDataValue(MotechEvent event, Task task, KeyInformation key) throws TaskHandlerException {
        TaskAdditionalData ad = task.getAdditionalData(key.getDataProviderId(), key.getObjectId(), key.getObjectType());
        Object found = getAdditionalDataObject(event, task, key);
        Object value;

        if (found == null) {
            if (ad.isFailIfDataNotFound()) {
                throw new TaskHandlerException(DATA_SOURCE, "error.notFoundObjectForType", key.getObjectType());
            } else {
                activityService.addWarning(task, "warning.notFoundObjectForType", key.getObjectType());
            }
        }

        try {
            value = HandlerUtil.getFieldValue(found, key.getKey());
        } catch (Exception e) {
            if (ad.isFailIfDataNotFound()) {
                throw new TaskHandlerException(DATA_SOURCE, "error.objectNotContainsField", e, key.getKey());
            } else {
                activityService.addWarning(task, "warning.objectNotContainsField", key.getKey(), e);
                value = "";
            }
        }

        return value;
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

    private String manipulateValue(String value, List<String> manipulations, Task task) throws TaskHandlerException {
        String manipulateValue = value;

        for (String manipulation : manipulations) {
            try {
                manipulateValue = HandlerUtil.manipulate(manipulation, manipulateValue);
            } catch (MotechException e) {
                String msg = e.getMessage();

                if ("warning.manipulation".equalsIgnoreCase(msg)) {
                    activityService.addWarning(task, msg, manipulation);
                } else {
                    throw new TaskHandlerException(TRIGGER, msg, e, manipulation);
                }
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
            LOG.error(String.format("The value of key: %s is not a number. Possible errors number is set to zero.", TASK_POSSIBLE_ERRORS_KEY));
            number = 0;
        }

        return number;
    }

    @Autowired(required = false)
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
