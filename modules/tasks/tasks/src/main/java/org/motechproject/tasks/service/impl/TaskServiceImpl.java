package org.motechproject.tasks.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.motechproject.commons.api.TasksEventParser;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryExecutor;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.motechproject.tasks.compatibility.TaskMigrationManager;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.domain.mds.task.DataSource;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskDataProvider;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.exception.ActionNotFoundException;
import org.motechproject.tasks.exception.CustomParserNotFoundException;
import org.motechproject.tasks.exception.TaskNameAlreadyExistsException;
import org.motechproject.tasks.exception.TaskNotFoundException;
import org.motechproject.tasks.exception.ValidationException;
import org.motechproject.tasks.repository.TasksDataService;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TriggerEventService;
import org.motechproject.tasks.service.TriggerHandler;
import org.motechproject.tasks.validation.TaskValidator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.tasks.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;
import static org.motechproject.tasks.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;
import static org.motechproject.tasks.service.util.HandlerPredicates.tasksWithRegisteredChannel;

/**
 * A {@link TaskService} that manages CRUD operations for a {@link Task}.
 * Expects channel registered,updated and deregistered events to be raised so that the associated tasks can be revalidated.
 */
@Service("taskService")
public class TaskServiceImpl implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private TasksDataService tasksDataService;
    private ChannelService channelService;
    private TriggerEventService triggerEventService;
    private TaskValidator taskValidator;
    private TaskDataProviderService providerService;
    private EventRelay eventRelay;
    private BundleContext bundleContext;
    private TaskMigrationManager taskMigrationManager;


    private static final String[] TASK_TRIGGER_VALIDATION_ERRORS = new String[]{"task.validation.error.triggerNotExist",
            "task.validation.error.triggerFieldNotExist"};
    private static final String TASK_ACTION_VALIDATION_ERRORS = "task.validation.error.actionNotExist";
    private static final String[] TASK_DATA_PROVIDER_VALIDATION_ERRORS = new String[]{"task.validation.error.providerObjectFieldNotExist",
            "task.validation.error.providerObjectNotExist",
            "task.validation.error.providerObjectLookupNotExist"};
    public static final List<String> IGNORED_FIELDS = new ArrayList<>();

    static {
        Collections.addAll(
                IGNORED_FIELDS, "validationErrors", "id", "creator", "creationDate", "owner",
                "modifiedBy", "modificationDate"
        );
    }

    @Override
    @Transactional
    public Set<TaskError> save(final Task task) {
        LOGGER.info("Saving task: {} with ID: {}", task.getName(), task.getId());
        Set<TaskError> errors = taskValidator.validate(task);

        if (task.isEnabled() && !isEmpty(errors)) {
            throw new ValidationException(TaskValidator.TASK, TaskError.toDtos(errors));
        }

        validateName(task);
        errors.addAll(validateTrigger(task));
        errors.addAll(validateDataSources(task));
        errors.addAll(validateActions(task));

        if (!isEmpty(errors)) {
            if (task.isEnabled()) {
                throw new ValidationException(TaskValidator.TASK, TaskError.toDtos(errors));
            } else {
                task.setValidationErrors(errors);
            }
        } else {
            task.setValidationErrors(null);
        }

        addOrUpdate(task);
        registerHandler(task);
        LOGGER.info("Saved task: {} with ID: {}", task.getName(), task.getId());
        return errors;
    }

    @Override
    @Transactional
    public ActionEvent getActionEventFor(TaskActionInformation taskActionInformation)
            throws ActionNotFoundException {
        Channel channel = channelService.getChannel(taskActionInformation.getModuleName());
        ActionEvent event = null;

        for (ActionEvent action : channel.getActionTaskEvents()) {
            if (action.accept(taskActionInformation)) {
                event = action;
                break;
            }
        }

        if (event == null) {
            throw new ActionNotFoundException(format(
                    "Cant find action on the basic of information: %s", taskActionInformation
            ));
        }

        return event;
    }

    @Override
    @Transactional
    public List<Task> getAllTasks() {
        List<Task> tasks = tasksDataService.retrieveAll();

        checkChannelAvailableInTasks(tasks);

        return tasks;
    }

    @Override
    @Transactional
    public List<Task> findTasksByName(String name) {
        List<Task> tasks = tasksDataService.findTasksByName(name);

        checkChannelAvailableInTasks(tasks);

        return tasks;
    }

    @Override
    @Transactional
    public List<Task> findActiveTasksForTrigger(final TriggerEvent trigger) {
        return (trigger == null) ? Collections.<Task>emptyList() : findActiveTasksForTriggerSubject(trigger.getSubject());
    }

    @Override
    @Transactional
    public List<Task> findActiveTasksForTriggerSubject(final String subject) {
        List<Task> list = null;

        if (isNotBlank(subject)) {
            List enabledTasks = tasksDataService.executeQuery(new QueryExecution<List<Task>>() {
                @Override
                public List<Task> execute(Query query, InstanceSecurityRestriction restriction) {
                    String byTriggerSubject = "trigger.subject == param";
                    String isTaskActive = "enabled == true";
                    String filter = String.format("(%s) && (%s)", isTaskActive, byTriggerSubject);

                    query.setFilter(filter);
                    query.declareParameters("java.lang.String param");
                    query.setOrdering("id asc");

                    return (List) QueryExecutor.execute(query, subject, restriction);
                }
            });
            if (enabledTasks != null) {
                checkChannelAvailableInTasks(enabledTasks);
                list = checkTimeWindowInTasks(enabledTasks);
                CollectionUtils.filter(list, tasksWithRegisteredChannel());
            }
        }

        return list == null ? new ArrayList<>() : list;
    }

    @Override
    public TasksEventParser findCustomParser(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        try {
            Collection<ServiceReference<TasksEventParser>> references = bundleContext.getServiceReferences(TasksEventParser.class, null);

            for (ServiceReference<TasksEventParser> ref : references) {
                TasksEventParser parser = bundleContext.getService(ref);
                if (parser.getName().equals(name)) {
                    return parser;
                }
            }
        } catch (InvalidSyntaxException e) {
            //Should never happen
            LOGGER.error("Passed filter expression is incorrect.");
        }

        // If a non-null parser name has been found in the event parameter, yet it cannot be found in
        // the running context, this indicates an error
        throw new CustomParserNotFoundException(name);
    }

    @Override
    @Transactional
    public Task getTask(Long taskId) {
        Task task = tasksDataService.findById(taskId);
        checkChannelAvailableInTask(task);

        return task;
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        Task t = getTask(taskId);

        if (t == null) {
            throw new TaskNotFoundException(taskId);
        }
        LOGGER.info("Deleted task: {} with ID: {}", t.getName(), taskId);
        tasksDataService.delete(t);
    }

    @MotechListener(subjects = CHANNEL_UPDATE_SUBJECT)
    @Transactional
    public void validateTasksAfterChannelUpdate(MotechEvent event) {
        String moduleName = event.getParameters().get(CHANNEL_MODULE_NAME).toString();
        Channel channel = channelService.getChannel(moduleName);

        LOGGER.debug("Handling Channel update: {} for module: {}", channel.getDisplayName(), moduleName);

        List<Task> tasks = findTasksDependentOnModule(moduleName);
        for (Task task : tasks) {
            Set<TaskError> errors;

            if (task.getTrigger() != null) {
                errors = validateTrigger(task);
                handleValidationErrors(task, errors, TASK_TRIGGER_VALIDATION_ERRORS);
            }

            errors = validateActions(task, channel);
            handleValidationErrors(task, errors, TASK_ACTION_VALIDATION_ERRORS);
        }
    }

    @MotechListener(subjects = DATA_PROVIDER_UPDATE_SUBJECT)
    @Transactional
    public void validateTasksAfterTaskDataProviderUpdate(MotechEvent event) {
        String providerName = event.getParameters().get(DATA_PROVIDER_NAME).toString();

        TaskDataProvider provider = providerService.getProvider(providerName);

        LOGGER.debug("Handling a task data provider update: {}", providerName);

        for (Task task : getAllTasks()) {
            SortedSet<DataSource> dataSources = task.getTaskConfig().getDataSources(provider.getName());
            if (isNotEmpty(dataSources)) {
                Set<TaskError> errors = new HashSet<>();
                for (DataSource dataSource : dataSources) {
                    errors.addAll(validateProvider(
                            provider,
                            dataSource,
                            task,
                            new HashMap<Long, TaskDataProvider>()
                    ));
                }
                errors.addAll(validateActions(task));
                handleValidationErrors(task, errors, TASK_DATA_PROVIDER_VALIDATION_ERRORS);
            }

        }
    }

    @Override
    @Transactional
    public String exportTask(Long taskId) {
        Task task = getTask(taskId);

        if (null != task) {
            LOGGER.info("Exporting task: {} with ID: {}", task.getName(), task.getId());
            JsonNode node = new ObjectMapper().valueToTree(task.toDto());
            removeIgnoredFields(node);

            return node.toString();
        } else {
            throw new TaskNotFoundException(taskId);
        }
    }

    private void removeIgnoredFields(JsonNode node) {
        if (null == node || node.isValueNode()) {
            return;
        }

        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            obj.remove(IGNORED_FIELDS);
        }

        if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            for (JsonNode item : array) {
                removeIgnoredFields(item);
            }
        }

        Iterator<Map.Entry<String, JsonNode>> elements = node.getFields();
        while (elements.hasNext()) {
            Map.Entry<String, JsonNode> entry = elements.next();
            if (!"values".equals(entry.getKey())) {
                removeIgnoredFields(entry.getValue());
            }
        }
    }

    @Override
    @Transactional
    public Task importTask(String json) throws IOException {
        LOGGER.info("Importing a task from json");
        LOGGER.trace("The json file: {}", json);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        removeIgnoredFields(node);

        Task task = mapper.readValue(node, Task.class);

        taskMigrationManager.migrateTask(task);

        save(task);
        return task;
    }

    @Override
    @Transactional
    public List<Task> findTasksDependentOnModule(final String moduleName) {
        List<Task> tasks = tasksDataService.executeQuery(new QueryExecution<List<Task>>() {
            @Override
            public List<Task> execute(Query query, InstanceSecurityRestriction restriction) {
                String byTrigger = "trigger.moduleName == param";
                String byAction = "actions.contains(action) && action.moduleName == param";
                String filter = String.format("(%s) || (%s)", byTrigger, byAction);

                query.setFilter(filter);
                query.declareParameters("java.lang.String param");

                return (List<Task>) query.execute(moduleName);
            }
        });

        checkChannelAvailableInTasks(tasks);

        return tasks;
    }

    private Set<TaskError> validateTrigger(Task task) {

        LOGGER.debug("Validating trigger in task: {} with ID: {}", task.getName(), task.getId());

        Set<TaskError> errors = triggerEventService.validateTrigger(task.getTrigger());

        logResultOfValidation("trigger", task.getName(), errors);

        return errors;
    }

    private Set<TaskError> validateDataSources(Task task) {
        LOGGER.debug("Validating task data sources in task: {} with ID: {}", task.getName(), task.getId());
        Set<TaskError> errors = new HashSet<>();
        Map<Long, TaskDataProvider> availableDataProviders = new HashMap<>();

        for (DataSource dataSource : task.getTaskConfig().getDataSources()) {
            TaskDataProvider provider = providerService.getProvider(dataSource.getProviderName());

            errors.addAll(validateProvider(provider, dataSource, task, availableDataProviders));
            if (provider != null) {
                availableDataProviders.put(provider.getId(), provider);
            }
        }

        logResultOfValidation("task data sources", task.getName(), errors);

        return errors;
    }

    private Set<TaskError> validateProvider(TaskDataProvider provider, DataSource dataSource, Task task, Map<Long, TaskDataProvider> availableDataProviders) {
        LOGGER.debug("Validating task data provider: {} in task: {} with ID: {}", dataSource.getProviderName(), task.getName(), task.getId());
        Set<TaskError> errors = new HashSet<>();

        TaskTriggerInformation trigger = task.getTrigger();

        if (provider != null) {
            errors.addAll(taskValidator.validateProvider(provider,
                    dataSource,
                    triggerEventService.getTrigger(trigger),
                    availableDataProviders
            ));
        } else {
            errors.add(new TaskError("task.validation.error.providerNotExist", dataSource.getProviderName()));
        }

        logResultOfValidation("task data provider", task.getName(), errors);

        return errors;
    }

    private Set<TaskError> validateActions(Task task) {
        LOGGER.debug("Validating all actions in task: {} with ID: {}", task.getName(), task.getId());
        Set<TaskError> errors = new HashSet<>();

        for (TaskActionInformation action : task.getActions()) {
            Channel channel = channelService.getChannel(action.getModuleName());
            errors.addAll(validateAction(task, channel, action));
        }

        logResultOfValidation("actions", task.getName(), errors);

        return errors;
    }

    private Set<TaskError> validateActions(Task task, Channel channel) {
        LOGGER.debug("Validating all actions in task: {} with ID: {}", task.getName(), task.getId());
        Set<TaskError> errors = new HashSet<>();

        for (TaskActionInformation action : task.getActions()) {
            errors.addAll(validateAction(task, channel, action));
        }

        logResultOfValidation("actions", task.getName(), errors);

        return errors;
    }

    private Set<TaskError> validateAction(Task task, Channel channel, TaskActionInformation action) {
        LOGGER.debug("Validating task action: {} from task: {} with ID: {}", action.getName(), task.getName(), task.getId());
        Set<TaskError> errors = new HashSet<>();

        if (channel == null) {
            errors.add(new TaskError("task.validation.error.actionChannelNotRegistered"));
            return errors;
        }

        if (channel.getModuleName().equalsIgnoreCase(action.getModuleName())) {
            errors.addAll(taskValidator.validateAction(action, channel));
            TriggerEvent trigger = triggerEventService.getTrigger(task.getTrigger());
            Map<Long, TaskDataProvider> providers = getProviders(task);
            ActionEvent actionEvent = channel.getAction(action);
            if (actionEvent != null) {
                errors.addAll(taskValidator.validateActionFields(action, actionEvent, trigger, providers));
            }
        }

        logResultOfValidation("task action", task.getName(), errors);

        return errors;
    }

    private void validateName(Task task) {

        Long taskId = task.getId();
        List<Task> tasksWithName = tasksDataService.findTasksByName(task.getName());

        if(taskId == null && tasksWithName.size() > 0) {
            throw new TaskNameAlreadyExistsException(task.getName());
        } else if (taskId != null && tasksWithName.size() > 0 && !tasksWithName.get(0).getId().equals(taskId)) {
            throw new TaskNameAlreadyExistsException(task.getName());
        }
    }

    private void logResultOfValidation(String validationName, String taskName, Set<TaskError> errors) {
        if (errors.isEmpty()) {
            LOGGER.debug("There is no errors in {} validation for task: {} ", validationName, taskName);
        } else {
            LOGGER.debug("In {} validation for task: {} the following errors occurred: {}", validationName, taskName, errors);
        }
    }

    private Map<Long, TaskDataProvider> getProviders(Task task) {
        Map<Long, TaskDataProvider> dataProviders = new HashMap<>();

        for (DataSource dataSource : task.getTaskConfig().getDataSources()) {
            TaskDataProvider provider = providerService.getProvider(dataSource.getProviderName());

            if (provider != null) {
                dataProviders.put(provider.getId(), provider);
            }
        }

        return dataProviders;
    }

    private void handleValidationErrors(Task task, Set<TaskError> errors, String... messages) {
        LOGGER.debug("Handling validation errors for task: {} with ID: {}", task.getName(), task.getId());
        if (CollectionUtils.isNotEmpty(errors)) {
            setTaskValidationErrors(task, errors);
        } else {
            removeTaskValidationErrors(task, messages);
        }
    }

    private void setTaskValidationErrors(Task task, Set<TaskError> errors) {
        publishTaskDisabledMessage(task.getName(), task.isEnabled() ? "CRITICAL" : "INFO");

        task.setEnabled(false);
        task.addValidationErrors(errors);
        addOrUpdate(task);
    }

    private void removeTaskValidationErrors(Task task, String... messages) {
        if (!task.hasValidationErrors()) {
            return;
        }
        for (String message : messages) {
            task.removeValidationError(message);
        }

        addOrUpdate(task);
    }

    private void publishTaskDisabledMessage(String taskName, String level) {
        Map<String, Object> params = new HashMap<>();
        String message = format("Task: %s was disabled due to validation errors.", taskName);
        params.put("message", message);
        params.put("level", level);
        params.put("moduleName", "tasks");

        LOGGER.info(message);
        MotechEvent motechEvent = new MotechEvent("org.motechproject.message", params);
        eventRelay.sendEventMessage(motechEvent);
    }

    private void addOrUpdate(final Task task) {
        Task existing = tasksDataService.findById(task.getId());

        if (null != existing) {
            LOGGER.debug("Updating task: {} with ID: {}", existing.getName(), existing.getId());
            existing.setActions(task.getActions());
            existing.setDescription(task.getDescription());
            existing.setFailuresInRow(task.getFailuresInRow());

            if (!existing.isEnabled() && task.isEnabled()) {
                existing.resetFailuresInRow();
            }

            existing.setEnabled(task.isEnabled());
            existing.setHasRegisteredChannel(task.hasRegisteredChannel());
            existing.setTaskConfig(task.getTaskConfig());
            existing.setTrigger(task.getTrigger());
            existing.setName(task.getName());
            existing.setValidationErrors(task.getValidationErrors());
            existing.setRetryTaskOnFailure(task.isRetryTaskOnFailure());
            existing.setNumberOfRetries(task.getNumberOfRetries());
            existing.setRetryIntervalInMilliseconds(task.getRetryIntervalInMilliseconds());
            existing.setUseTimeWindow(task.isUsingTimeWindow());
            existing.setStartTime(task.getStartTime());
            existing.setEndTime(task.getEndTime());

            checkChannelAvailableInTask(existing);

            tasksDataService.update(existing);
        } else {
            LOGGER.debug("Creating task: {}", task.getName());
            checkChannelAvailableInTask(task);

            tasksDataService.create(task);
        }

        LOGGER.info("Saved task: {}", task.getName());
    }

    private void registerHandler(Task task) {
        // We cannot simply autowire trigger handler bean, since that would create
        // circular dependency between TaskService and TriggerHandler
        ServiceReference<TriggerHandler> serviceReference = bundleContext.getServiceReference(TriggerHandler.class);
        if (serviceReference != null) {
            TriggerHandler triggerHandler = bundleContext.getService(serviceReference);
            triggerHandler.registerHandlerFor(task.getTrigger().getEffectiveListenerSubject());

            if (task.isRetryTaskOnFailure()) {
                triggerHandler.registerHandlerFor(task.getTrigger().getEffectiveListenerRetrySubject(), true);
            }
        }
    }

    private void checkChannelAvailableInTasks(List<Task> tasks) {
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (Task task : tasks) {
                checkChannelAvailableInTask(task);
            }
        }
    }

    private void checkChannelAvailableInTask(Task task) {
        if (null != task) {
            List<String> symbolic = WebBundleUtil.getSymbolicNames(bundleContext);

            TaskTriggerInformation trigger = task.getTrigger();
            List<TaskActionInformation> actions = task.getActions();

            task.setHasRegisteredChannel(null != trigger && symbolic.contains(trigger.getModuleName()));

            if (CollectionUtils.isNotEmpty(actions)) {
                for (TaskActionInformation action : actions) {
                    if (!symbolic.contains(action.getModuleName())) {
                        task.setHasRegisteredChannel(false);
                    }
                }
            }
        }
    }

    private List<Task> checkTimeWindowInTasks(List<Task> tasks) {
        List<Task> checked = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (Task task : tasks) {
                if (checkTimeWindowInTask(task)) {
                    checked.add(task);
                }
            }
        }
        return checked;
    }

    private boolean checkTimeWindowInTask(Task task) {
        if (task.isUsingTimeWindow()) {
            if (task.getStartTime() == null || task.getEndTime() == null) {
                return false;
            }
            Time now = new Time(new LocalTime(DateTimeZone.UTC));
            if (task.getStartTime().isBefore(task.getEndTime())) {
                return now.isBetween(task.getStartTime(), task.getEndTime());
            } else {
                return now.isBetween(task.getStartTime(), new Time(24, 0)) ||
                        now.isBetween(new Time(LocalTime.MIDNIGHT), task.getEndTime());
            }
        }
        return true;
    }

    @Autowired
    public void setTasksDataService(TasksDataService tasksDataService) {
        this.tasksDataService = tasksDataService;
    }

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Autowired
    public void setProviderService(TaskDataProviderService providerService) {
        this.providerService = providerService;
    }

    @Autowired
    public void setEventRelay(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setTaskValidator(TaskValidator taskValidator) {
        this.taskValidator = taskValidator;
    }

    @Autowired
    public void setTriggerEventService(TriggerEventService triggerEventService) {
        this.triggerEventService = triggerEventService;
    }

    @Autowired
    public void setTaskMigrationManager(TaskMigrationManager taskMigrationManager) {
        this.taskMigrationManager = taskMigrationManager;
    }
}
