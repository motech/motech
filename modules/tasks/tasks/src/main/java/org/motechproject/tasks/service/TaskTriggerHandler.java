package org.motechproject.tasks.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.OperatorType;
import org.motechproject.tasks.domain.ParameterType;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskAdditionalData;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.motechproject.tasks.util.TaskUtil.getSubject;

@Service
public class TaskTriggerHandler {
    public static final String TRIGGER_PREFIX = "trigger";
    public static final String ADDITIONAL_DATA_PREFIX = "ad";

    private static final String SERVICE_NAME = "taskTriggerHandler";
    private static final String TASK_POSSIBLE_ERRORS_KEY = "task.possible.errors";

    private static final Logger LOG = LoggerFactory.getLogger(TaskTriggerHandler.class);

    private TaskService taskService;
    private TaskActivityService activityService;
    private EventListenerRegistryService registryService;
    private EventRelay eventRelay;
    private SettingsFacade settingsFacade;
    private Map<String, DataProvider> dataProviders;

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
                    Map<String, Object> parameters = createParameters(task, action.getEventParameters(), triggerEvent);
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
                Object obj = CollectionUtils.find(registryService.getListeners(subject), new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return object instanceof MotechListenerEventProxy && ((MotechListenerEventProxy) object).getIdentifier().equalsIgnoreCase(SERVICE_NAME);
                    }
                });

                if (obj == null) {
                    registryService.registerListener(proxy, subject);
                    LOG.info(String.format("Register TaskTriggerHandler for subject: '%s'", subject));
                }
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

    private Map<String, Object> createParameters(Task task, List<EventParameter> actionParameters, MotechEvent event) throws TaskException {
        Map<String, Object> parameters = new HashMap<>(actionParameters.size());

        for (EventParameter param : actionParameters) {
            final String key = param.getEventKey();
            String template = task.getActionInputFields().get(key);

            if (template == null) {
                throw new TaskException("error.templateNull", key);
            }

            String userInput = replaceAll(template, event, task);

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
            } else if (param.getType().isDate()) {
                try {
                    value = DateTime.parse(userInput, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z"));
                } catch (IllegalArgumentException e) {
                    throw new TaskException("error.convertToDate", key, e);
                }
            } else {
                value = userInput;
            }

            parameters.put(key, value);
        }

        return parameters;
    }

    private String replaceAll(final String template, final MotechEvent event, final Task task) throws TaskException {
        String conversionTemplate = template;
        List<KeyInformation> keys = getKeys(template);

        for (KeyInformation key : keys) {
            String value = "";

            if (key.fromTrigger()) {
                value = getTriggerKey(event, key);
            } else if (key.fromAdditionalData()) {
                value = getAdditionalDataKey(event, task, key);
            }

            String replacedValue = manipulateValue(value, key.getManipulations(), task);
            conversionTemplate = conversionTemplate.replace(String.format("{{%s}}", key.getOriginalKey()), replacedValue);
        }

        return conversionTemplate;
    }

    private String getTriggerKey(MotechEvent event, KeyInformation key) throws TaskException {
        String value = "";

        if (event.getParameters().containsKey(key.getEventKey())) {
            Object obj = event.getParameters().get(key.getEventKey());

            if (obj == null) {
                obj = "";
            }

            value = String.valueOf(obj);
        }

        return value;
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

        return getValueFromObject(found, key.getEventKey());
    }

    private List<KeyInformation> getKeys(String templateText) {
        List<KeyInformation> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(templateText);

        while (matcher.find()) {
            keys.add(new KeyInformation(matcher.group(1)));
        }

        return keys;
    }

    private boolean checkFilters(List<Filter> filters, Map<String, Object> triggerParameters) {
        boolean filterCheck = false;

        for (Filter filter : filters) {
            EventParameter eventParameter = filter.getEventParameter();

            if (triggerParameters.containsKey(eventParameter.getEventKey())) {
                ParameterType type = eventParameter.getType();
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

    private String getValueFromObject(Object object, String eventKey) throws TaskException {
        String[] fields = eventKey.split("\\.");
        Object current = object;

        for (String f : fields) {
            try {
                Method method = current.getClass().getMethod("get" + WordUtils.capitalize(f));
                current = method.invoke(current);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new TaskException("error.objectNotContainsField", eventKey, e);
            }
        }

        return current.toString();
    }

    private TaskAdditionalData findAdditionalData(Task t, KeyInformation key) {
        List<TaskAdditionalData> taskAdditionalDatas = t.getAdditionalData(key.getDataProviderId());
        TaskAdditionalData taskAdditionalData = null;

        for (TaskAdditionalData ad : taskAdditionalDatas) {
            if (ad.getId().equals(key.getObjectId()) && ad.getType().equalsIgnoreCase(key.getObjectType())) {
                taskAdditionalData = ad;
                break;
            }
        }

        return taskAdditionalData;
    }

    private void logOmittedTask(Task task, Throwable e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Omitted task with ID: %s because: ", task.getId()), e);
        }
    }

    private String manipulateValue(String value, List<String> manipulations, Task task) throws TaskException {
        String manipulateValue = value;
        for (String man : manipulations) {
            if (!man.toLowerCase().contains("join") && !man.toLowerCase().contains("datetime")) {
                switch (man.toLowerCase()) {
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
                        activityService.addWarning(task, "warning.manipulation", man);
                        break;
                }
            } else if (man.toLowerCase().contains("join")) {
                String[] splitValue = manipulateValue.split(" ");
                man = man.substring(5, man.length() - 1);
                manipulateValue = StringUtils.join(splitValue, man);
            } else if (man.toLowerCase().contains("datetime")) {
                try {
                    man = man.substring(9, man.length() - 1);
                    DateTimeFormatter format = DateTimeFormat.forPattern(man);
                    DateTime date = new DateTime(manipulateValue);
                    manipulateValue = format.print(date);
                } catch (IllegalArgumentException e) {
                    throw new TaskException("error.date.format", man, e);
                }
            } else {
                activityService.addWarning(task, "warning.manipulation", man);
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
