package org.motechproject.tasks.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.commons.api.TasksEventParser;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryExecutor;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.FilterSet;
import org.motechproject.tasks.domain.Lookup;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskConfigStep;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.domain.TaskTriggerInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.CustomParserNotFoundException;
import org.motechproject.tasks.ex.TaskNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.repository.TasksDataService;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TriggerHandler;
import org.motechproject.tasks.validation.TaskValidator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

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
import static org.motechproject.tasks.events.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.events.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.events.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;
import static org.motechproject.tasks.events.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;
import static org.motechproject.tasks.service.HandlerPredicates.tasksWithRegisteredChannel;
import static org.motechproject.tasks.validation.TaskValidator.TASK;

/**
 * A {@link TaskService} that manages CRUD operations for a {@link Task}.
 * Expects channel registered,updated and deregistered events to be raised so that the associated tasks can be revalidated.
 */
@Service("taskService")
public class TaskServiceImpl implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private static final String SCHEDULER_TASK_TRIGGER_WILDCARD = "org.motechproject.tasks.scheduler(.*)";

    private TasksDataService tasksDataService;
    private ChannelService channelService;
    private TaskDataProviderService providerService;
    private EventRelay eventRelay;
    private BundleContext bundleContext;


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


    public void save(final Task task){
        save(task, true);
    }

    @Override
    public void save(final Task task, boolean registerHandler) {
        Set<TaskError> errors = TaskValidator.validate(task);

        if (task.isEnabled() && !isEmpty(errors)) {
            throw new ValidationException(TASK, errors);
        }

        errors.addAll(validateTrigger(task));
        errors.addAll(validateDataSources(task));
        errors.addAll(validateActions(task));

        if (!isEmpty(errors)) {
            if (task.isEnabled()) {
                throw new ValidationException(TASK, errors);
            } else {
                task.setValidationErrors(errors);
            }
        } else {
            task.setValidationErrors(null);
        }

        // todo clean those ifs somehow
        if (task.getTrigger().getSubject().matches(SCHEDULER_TASK_TRIGGER_WILDCARD)) {
            task.getTrigger().setEffectiveListenerSubject(task.getTrigger().getSubject() + task.getName());
        }

        addOrUpdate(task);

        if (registerHandler) {
            registerHandler(task.getTrigger().getEffectiveListenerSubject());
        }

        LOGGER.info(format("Saved task: %s", task.getId()));
    }

    @Override
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
    public List<Task> getAllTasks() {
        List<Task> tasks = tasksDataService.retrieveAll();

        checkChannelAvailableInTasks(tasks);

        return tasks;
    }

    @Override
    public List<Task> findTasksByName(String name) {
        List<Task> tasks = tasksDataService.findTasksByName(name);

        checkChannelAvailableInTasks(tasks);

        return tasks;
    }

    @Override
    public List<Task> findActiveTasksForTrigger(final TriggerEvent trigger) {
        return (trigger == null) ? Collections.<Task>emptyList() : findActiveTasksForTriggerSubject(trigger.getSubject());
    }

    @Override
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

                    return (List) QueryExecutor.execute(query, subject, restriction);
                }
            });
            if (enabledTasks != null) {
                checkChannelAvailableInTasks(enabledTasks);
                list = new ArrayList<>(enabledTasks);
                CollectionUtils.filter(list, tasksWithRegisteredChannel());
            }
        }

        return list == null ? new ArrayList<Task>() : list;
    }

    @Override
    public TriggerEvent findTrigger(String subject) throws TriggerNotFoundException {
        List<Channel> channels = channelService.getAllChannels();
        TriggerEvent trigger = null;

        for (Channel c : channels) {
            for (TriggerEvent t : c.getTriggerTaskEvents()) {
                if (t.getSubject().equalsIgnoreCase(subject)) {
                    trigger = t;
                    break;
                }
            }

            if (trigger != null) {
                break;
            }
        }

        if (trigger == null) {
            throw new TriggerNotFoundException(format(
                    "Cant find trigger for subject: %s", subject
            ));
        }

        return trigger;
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
    public Task getTask(Long taskId) {
        Task task = tasksDataService.findById(taskId);
        checkChannelAvailableInTask(task);

        return task;
    }

    @Override
    public void deleteTask(Long taskId) {
        Task t = getTask(taskId);

        if (t == null) {
            throw new TaskNotFoundException(taskId);
        }

        unscheduleTaskTrigger(t);

        tasksDataService.delete(t);
    }

    @MotechListener(subjects = CHANNEL_UPDATE_SUBJECT)
    public void validateTasksAfterChannelUpdate(MotechEvent event) {
        String moduleName = event.getParameters().get(CHANNEL_MODULE_NAME).toString();
        Channel channel = channelService.getChannel(moduleName);

        LOGGER.debug(String.format("Handling Channel update %s for module %s", channel.getDisplayName(), moduleName));

        List<Task> tasks = findTasksDependentOnModule(moduleName);
        for (Task task : tasks) {
            Set<TaskError> errors;

            if (task.getTrigger() != null) {
                errors = validateTrigger(task, channel);
                handleValidationErrors(task, errors, TASK_TRIGGER_VALIDATION_ERRORS);
            }

            errors = validateActions(task, channel);
            handleValidationErrors(task, errors, TASK_ACTION_VALIDATION_ERRORS);
        }
    }

    @MotechListener(subjects = DATA_PROVIDER_UPDATE_SUBJECT)
    public void validateTasksAfterTaskDataProviderUpdate(MotechEvent event) {
        String providerName = event.getParameters().get(DATA_PROVIDER_NAME).toString();

        TaskDataProvider provider = providerService.getProvider(providerName);

        for (Task task : getAllTasks()) {
            SortedSet<DataSource> dataSources = task.getTaskConfig().getDataSources(provider.getId());
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
    public String exportTask(Long taskId) {
        Task task = getTask(taskId);

        if (null != task) {
            JsonNode node = new ObjectMapper().valueToTree(task);
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
    public Task importTask(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        removeIgnoredFields(node);

        Task task = mapper.readValue(node, Task.class);
        List<DataSource> sources = task.getTaskConfig().getDataSources();

        // update data provider IDs
        for (DataSource ds : sources) {
            TaskDataProvider provider = findProviderByName(ds);

            if (null != provider) {
                Long oldId = ds.getProviderId();
                Long newId = provider.getId();

                if (!oldId.equals(newId)) {
                    replaceProviderId(task, oldId, newId);
                    ds.setProviderId(newId);
                }
            }
        }

        save(task);
        return task;
    }

    @Override
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

    private void replaceProviderId(Task task, Long oldId, Long newId) {
        for (TaskConfigStep step : task.getTaskConfig().getSteps()) {
            if (step instanceof DataSource) {
                DataSource source = (DataSource) step;

                for (Lookup lookup : source.getLookup()) {
                    lookup.setValue(lookup.getValue().replace(oldId.toString(), newId.toString()));
                }
            } else if (step instanceof FilterSet) {
                FilterSet set = (FilterSet) step;

                for (Filter filter : set.getFilters()) {
                    filter.setKey(filter.getKey().replace(oldId.toString(), newId.toString()));
                }
            }
        }

        for (TaskActionInformation action : task.getActions()) {
            for (Map.Entry<String, String> row : action.getValues().entrySet()) {
                action.getValues().put(
                        row.getKey(),
                        row.getValue().replace(oldId.toString(), newId.toString())
                );
            }
        }
    }

    private TaskDataProvider findProviderByName(DataSource ds) {
        TaskDataProvider provider = null;

        for (TaskDataProvider p : providerService.getProviders()) {
            if (p.getName().equalsIgnoreCase(ds.getProviderName())) {
                provider = p;
                break;
            }
        }

        return provider;
    }

    private Set<TaskError> validateTrigger(Task task) {
        TaskTriggerInformation trigger = task.getTrigger();
        Channel channel = channelService.getChannel(trigger.getModuleName());

        return validateTrigger(task, channel);
    }

    private Set<TaskError> validateTrigger(Task task, Channel channel) {
        Set<TaskError> errors = new HashSet<>();

        TaskTriggerInformation trigger = task.getTrigger();

        if (trigger != null) {
            if (channel == null) {
                errors.add(new TaskError("task.validation.error.triggerChannelNotRegistered"));
                return errors;
            }
            if (channel.getModuleName().equalsIgnoreCase(trigger.getModuleName())) {
                errors.addAll(TaskValidator.validateTrigger(task, channel));
            }
        } else {
            errors.add(new TaskError("task.validation.error.triggerNotSpecified"));
        }

        return errors;
    }

    private Set<TaskError> validateDataSources(Task task) {
        Set<TaskError> errors = new HashSet<>();
        Map<Long, TaskDataProvider> availableDataProviders = new HashMap<>();

        for (DataSource dataSource : task.getTaskConfig().getDataSources()) {
            TaskDataProvider provider = providerService.getProviderById(dataSource.getProviderId());

            errors.addAll(validateProvider(provider, dataSource, task, availableDataProviders));
            if (provider != null) {
                availableDataProviders.put(provider.getId(), provider);
            }
        }

        return errors;
    }

    private Set<TaskError> validateProvider(TaskDataProvider provider, DataSource dataSource, Task task, Map<Long, TaskDataProvider> availableDataProviders) {
        Set<TaskError> errors = new HashSet<>();

        TaskTriggerInformation trigger = task.getTrigger();

        if (provider != null) {
            errors.addAll(TaskValidator.validateProvider(provider,
                    dataSource,
                    channelService.getChannel(trigger.getModuleName()).getTrigger(trigger),
                    availableDataProviders
            ));
        } else {
            errors.add(new TaskError("task.validation.error.providerNotExist", dataSource.getProviderName()));
        }

        return errors;
    }

    private Set<TaskError> validateActions(Task task) {
        Set<TaskError> errors = new HashSet<>();

        for (TaskActionInformation action : task.getActions()) {
            Channel channel = channelService.getChannel(action.getModuleName());
            errors.addAll(validateAction(task, channel, action));
        }

        return errors;
    }

    private Set<TaskError> validateActions(Task task, Channel channel) {
        Set<TaskError> errors = new HashSet<>();

        for (TaskActionInformation action : task.getActions()) {
            errors.addAll(validateAction(task, channel, action));
        }

        return errors;
    }

    private Set<TaskError> validateAction(Task task, Channel channel, TaskActionInformation action) {
        Set<TaskError> errors = new HashSet<>();

        if (channel == null) {
            errors.add(new TaskError("task.validation.error.actionChannelNotRegistered"));
            return errors;
        }

        if (channel.getModuleName().equalsIgnoreCase(action.getModuleName())) {
            errors.addAll(TaskValidator.validateAction(action, channel));
            TriggerEvent trigger = channelService.getChannel(task.getTrigger().getModuleName()).getTrigger(task.getTrigger());
            Map<Long, TaskDataProvider> providers = getProviders(task);
            ActionEvent actionEvent = channel.getAction(action);
            if (actionEvent != null) {
                errors.addAll(TaskValidator.validateActionFields(action, actionEvent, trigger, providers));
            }
        }

        return errors;
    }

    private Map<Long, TaskDataProvider> getProviders(Task task) {
        Map<Long, TaskDataProvider> dataProviders = new HashMap<>();

        for (DataSource dataSource : task.getTaskConfig().getDataSources()) {
            TaskDataProvider provider = providerService.getProviderById(dataSource.getProviderId());

            if (provider != null) {
                dataProviders.put(provider.getId(), provider);
            }
        }

        return dataProviders;
    }

    private void handleValidationErrors(Task task, Set<TaskError> errors, String... messages) {
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
        tasksDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Task existing = tasksDataService.findById(task.getId());

                if (null != existing) {
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

                    checkChannelAvailableInTask(existing);

                    tasksDataService.update(existing);
                } else {
                    checkChannelAvailableInTask(task);

                    tasksDataService.create(task);
                }
            }
        });
    }

    private void registerHandler(String effectiveListenerSubject) {
        // We cannot simply autowire trigger handler bean, since that would create
        // circular dependency between TaskService and TriggerHandler
        ServiceReference<TriggerHandler> serviceReference = bundleContext.getServiceReference(TriggerHandler.class);
        if (serviceReference != null) {
            TriggerHandler triggerHandler = bundleContext.getService(serviceReference);
            triggerHandler.registerHandlerFor(effectiveListenerSubject);
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

    private void unscheduleTaskTrigger(Task task) {
        ServiceReference<TriggerHandler> serviceReference = bundleContext.getServiceReference(TriggerHandler.class);
        if (serviceReference != null) {
            TriggerHandler triggerHandler = bundleContext.getService(serviceReference);
            triggerHandler.unscheduleTaskTriggerFor(task);
        }
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
}
