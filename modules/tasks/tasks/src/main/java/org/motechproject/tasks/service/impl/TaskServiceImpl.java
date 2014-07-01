package org.motechproject.tasks.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.ChannelDeregisterEvent;
import org.motechproject.tasks.domain.ChannelRegisterEvent;
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
import org.motechproject.tasks.events.constants.EventSubjects;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.repository.TasksDataService;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.validation.TaskValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.tasks.events.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.events.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.events.constants.EventSubjects.CHANNEL_DEREGISTER_SUBJECT;
import static org.motechproject.tasks.events.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;
import static org.motechproject.tasks.events.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;
import static org.motechproject.tasks.validation.TaskValidator.TASK;

/**
 * A {@link TaskService} that manages CRUD operations for a {@link Task} over a couchdb database.
 * Expects channel registered,updated and deregistered events to be raised so that the associated tasks can be revalidated.
 */
@Service("taskService")
public class TaskServiceImpl implements TaskService {
    private static final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

    private TasksDataService tasksDataService;
    private ChannelService channelService;
    private TaskDataProviderService providerService;
    private EventRelay eventRelay;

    private static final String[] TASK_TRIGGER_VALIDATION_ERRORS = new String[]{"task.validation.error.triggerNotExist",
            "task.validation.error.triggerFieldNotExist"};
    private static final String TASK_ACTION_VALIDATION_ERRORS = "task.validation.error.actionNotExist";
    private static final String[] TASK_DATA_PROVIDER_VALIDATION_ERRORS = new String[]{"task.validation.error.providerObjectFieldNotExist",
            "task.validation.error.providerObjectNotExist",
            "task.validation.error.providerObjectLookupNotExist"};

    @Autowired
    public TaskServiceImpl(TasksDataService tasksDataService, ChannelService channelService,
                           TaskDataProviderService providerService, EventRelay eventRelay) {
        this.tasksDataService = tasksDataService;
        this.channelService = channelService;
        this.providerService = providerService;
        this.eventRelay = eventRelay;
    }

    @Override
    public void save(final Task task) {
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

        addOrUpdate(task);
        LOG.info(format("Saved task: %s", task.getId()));
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
    public List<Task> getTasksDataService() {
        return tasksDataService.retrieveAll();
    }

    @Override
    public List<Task> findTasksForTrigger(final TriggerEvent trigger) {
        List<Task> list;

        if (trigger != null && isNotBlank(trigger.getSubject())) {
            list = tasksDataService.retrieveAll();
            CollectionUtils.filter(list, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return object instanceof Task
                            && null != ((Task) object).getTrigger()
                            && ((Task) object).getTrigger().getSubject().equalsIgnoreCase(trigger.getSubject());
                }
            });
        } else {
            list = new ArrayList<>();
        }

        return list;
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
    public Task getTask(Long taskId) {
        return tasksDataService.retrieve("id", taskId);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task t = getTask(taskId);

        if (t == null) {
            throw new TaskNotFoundException(taskId);
        }

        tasksDataService.delete(t);
    }

    @MotechListener(subjects = CHANNEL_UPDATE_SUBJECT)
    public void validateTasksAfterChannelUpdate(MotechEvent event) {
        String moduleName = event.getParameters().get(CHANNEL_MODULE_NAME).toString();
        Channel channel = channelService.getChannel(moduleName);

        LOG.debug(String.format("Handling Channel update %s for module %s", channel.getDisplayName(), moduleName));

        List<Task> tasks = dependentOnModule(moduleName);
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

        for (Task task : getTasksDataService()) {
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

    @MotechListener(subjects = EventSubjects.CHANNEL_REGISTER_SUBJECT)
    public void activateTasksAfterChannelRegister(MotechEvent motechEvent) {
        ChannelRegisterEvent event = new ChannelRegisterEvent(motechEvent);
        List<Task> tasks = dependentOnModule(event.getChannelModuleName());
        for (Task task : tasks) {
            LOG.debug(String.format("Registering channel for task %s", task.getName()));
            task.setHasRegisteredChannel(true);
            addOrUpdate(task);
        }
    }

    @MotechListener(subjects = CHANNEL_DEREGISTER_SUBJECT)
    public void deactivateTasksAfterChannelDeregister(MotechEvent motechEvent) {
        ChannelDeregisterEvent event = new ChannelDeregisterEvent(motechEvent);
        List<Task> tasks = dependentOnModule(event.getChannelModuleName());
        for (Task task : tasks) {
            LOG.debug(String.format("Deregistering channel for task %s", task.getName()));
            task.setHasRegisteredChannel(false);
            addOrUpdate(task);
        }
    }

    @Override
    public String exportTask(Long taskId) {
        Task task = getTask(taskId);

        if (null != task) {
            ObjectNode node = new ObjectMapper().valueToTree(task);
            // remove unnecessary fields
            node.remove(asList("validationErrors", "type", "id"));

            return node.toString();
        } else {
            throw new TaskNotFoundException(taskId);
        }
    }

    @Override
    public void importTask(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Task task = mapper.readValue(json, Task.class);
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

        LOG.info(message);
        MotechEvent motechEvent = new MotechEvent("org.motechproject.message", params);
        eventRelay.sendEventMessage(motechEvent);
    }

    private List<Task> dependentOnModule(String moduleName) {
        List<Task> tasks = tasksDataService.retrieveAll();
        Iterator<Task> iterator = tasks.iterator();

        while (iterator.hasNext()) {
            Task next = iterator.next();
            boolean byTrigger = false;
            boolean byAction = false;

            if (null != next.getTrigger()) {
                byTrigger = equalsIgnoreCase(next.getTrigger().getModuleName(), moduleName);
            }

            if (null != next.getActions()) {
                for (TaskActionInformation action : next.getActions()) {
                    byAction = byAction || equalsIgnoreCase(action.getModuleName(), moduleName);
                }
            }

            if (!byTrigger && !byAction) {
                iterator.remove();
            }
        }

        return tasks;
    }

    private void addOrUpdate(Task task) {
        Task existing = tasksDataService.retrieve("id", task.getId());

        if (null != existing) {
            existing.setActions(task.getActions());
            existing.setDescription(task.getDescription());
            existing.setEnabled(task.isEnabled());
            existing.setHasRegisteredChannel(task.hasRegisteredChannel());
            existing.setTaskConfig(task.getTaskConfig());
            existing.setTrigger(task.getTrigger());
            existing.setName(task.getName());
            existing.setValidationErrors(task.getValidationErrors());

            tasksDataService.update(existing);
        } else {
            tasksDataService.create(task);
        }
    }
}
