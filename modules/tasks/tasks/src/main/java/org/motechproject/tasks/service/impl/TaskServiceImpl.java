package org.motechproject.tasks.service.impl;

import org.apache.commons.collections.CollectionUtils;
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
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskConfigStep;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.events.constants.EventSubjects;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.repository.AllTasks;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.tasks.domain.DataSource.Lookup;
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

    private AllTasks allTasks;
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
    public TaskServiceImpl(AllTasks allTasks, ChannelService channelService,
                           TaskDataProviderService providerService, EventRelay eventRelay) {
        this.allTasks = allTasks;
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

        TaskEventInformation trigger = task.getTrigger();

        if (trigger != null) {
            errors.addAll(validateTriggerTask(
                    task, channelService.getChannel(trigger.getModuleName())
            ));
        }

        for (TaskActionInformation action : task.getActions()) {
            errors.addAll(validateActionTask(
                    action, channelService.getChannel(action.getModuleName())
            ));
        }

        for (DataSource dataSource : task.getTaskConfig().getDataSources()) {
            TaskDataProvider provider = providerService.getProviderById(dataSource.getProviderId());

            if (null != provider) {
                for (TaskActionInformation action : task.getActions()) {
                    errors.addAll(TaskValidator.validateProvider(
                            action.getValues(), dataSource, provider, task.getTaskConfig().getFilters()
                    ));
                }
            } else {
                errors.add(new TaskError("task.validation.error.providerNotExist", dataSource.getProviderName()));
            }
        }

        if (!isEmpty(errors)) {
            if (task.isEnabled()) {
                throw new ValidationException(TASK, errors);
            } else {
                task.setValidationErrors(errors);
            }
        } else {
            task.setValidationErrors(null);
        }

        allTasks.addOrUpdate(task);
        LOG.info(format("Saved task: %s", task.getId()));
    }

    /**
     * @deprecated As of release 0.20, replaced by {@link #getActionEventFor(org.motechproject.tasks.domain.TaskActionInformation)}
     */
    @Deprecated
    @Override
    public ActionEvent getActionEventFor(Task task) throws ActionNotFoundException {
        return getActionEventFor(task.getAction());
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
        return allTasks.getAll();
    }

    @Override
    public List<Task> findTasksForTrigger(final TriggerEvent trigger) {
        List<Task> list;

        if (trigger != null && isNotBlank(trigger.getSubject())) {
            list = allTasks.byTriggerSubject(trigger.getSubject());
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
    public Task getTask(String taskId) {
        return allTasks.get(taskId);
    }

    @Override
    public void deleteTask(String taskId) {
        Task t = getTask(taskId);

        if (t == null) {
            throw new TaskNotFoundException(taskId);
        }

        allTasks.remove(t);
    }

    @MotechListener(subjects = CHANNEL_UPDATE_SUBJECT)
    public void validateTasksAfterChannelUpdate(MotechEvent event) {
        String moduleName = event.getParameters().get(CHANNEL_MODULE_NAME).toString();
        Channel channel = channelService.getChannel(moduleName);

        for (Task task : getAllTasks()) {
            Set<TaskError> errors;

            if (task.getTrigger() != null) {
                errors = validateTriggerTask(task, channel);
                handleValidationErrors(task, errors, TASK_TRIGGER_VALIDATION_ERRORS);
            }

            for (TaskActionInformation action : task.getActions()) {
                errors = validateActionTask(action, channel);
                handleValidationErrors(task, errors, TASK_ACTION_VALIDATION_ERRORS);
            }
        }
    }

    @MotechListener(subjects = DATA_PROVIDER_UPDATE_SUBJECT)
    public void validateTasksAfterTaskDataProviderUpdate(MotechEvent event) {
        String providerName = event.getParameters().get(DATA_PROVIDER_NAME).toString();

        TaskDataProvider provider = providerService.getProvider(providerName);

        for (Task task : getAllTasks()) {
            for (DataSource dataSource : task.getTaskConfig().getDataSources(provider.getId())) {
                for (TaskActionInformation action : task.getActions()) {
                    Set<TaskError> errors = TaskValidator.validateProvider(
                            action.getValues(), dataSource, provider, task.getTaskConfig().getFilters()
                    );
                    handleValidationErrors(task, errors, TASK_DATA_PROVIDER_VALIDATION_ERRORS);
                }
            }
        }
    }

    @MotechListener(subjects = EventSubjects.CHANNEL_REGISTER_SUBJECT)
    public void activateTasksAfterChannelRegister(MotechEvent motechEvent) {
        ChannelRegisterEvent event = new ChannelRegisterEvent(motechEvent);
        List<Task> tasks = allTasks.dependentOnModule(event.getChannelModuleName());
        for (Task task : tasks) {
            task.setHasRegisteredChannel(true);
            allTasks.addOrUpdate(task);
        }
    }

    @MotechListener(subjects = CHANNEL_DEREGISTER_SUBJECT)
    public void deactivateTasksAfterChannelDeregister(MotechEvent motechEvent) {
        ChannelDeregisterEvent event = new ChannelDeregisterEvent(motechEvent);
        List<Task> tasks = allTasks.dependentOnModule(event.getChannelModuleName());
        for (Task task : tasks) {
            task.setHasRegisteredChannel(false);
            allTasks.addOrUpdate(task);
        }
    }

    @Override
    public String exportTask(String taskId) {
        Task task = getTask(taskId);

        if (null != task) {
            ObjectNode node = new ObjectMapper().valueToTree(task);
            // remove unnecessary fields
            node.remove(asList("validationErrors", "type", "_id", "_rev"));

            return node.toString();
        } else {
            throw new TaskNotFoundException(taskId);
        }
    }

    @Override
    public void importTask(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Task task = mapper.readValue(json, Task.class);
        SortedSet<DataSource> sources = task.getTaskConfig().getDataSources();

        // update data provider IDs
        for (DataSource ds : sources) {
            TaskDataProvider provider = findProviderByName(ds);

            if (null != provider) {
                String oldId = ds.getProviderId();
                String newId = provider.getId();

                if (!oldId.equalsIgnoreCase(newId)) {
                    replaceProviderId(task, oldId, newId);
                    ds.setProviderId(newId);
                }
            }
        }

        save(task);
    }

    private void replaceProviderId(Task task, String oldId, String newId) {
        for (TaskConfigStep step : task.getTaskConfig().getSteps()) {
            if (step instanceof DataSource) {
                DataSource source = (DataSource) step;

                for (Lookup lookup : source.getLookup()) {
                    lookup.setValue(lookup.getValue().replace(oldId, newId));
                }
            } else if (step instanceof FilterSet) {
                FilterSet set = (FilterSet) step;

                for (Filter filter : set.getFilters()) {
                    filter.setKey(filter.getKey().replace(oldId, newId));
                }
            }
        }

        for (TaskActionInformation action : task.getActions()) {
            for (Map.Entry<String, String> row : action.getValues().entrySet()) {
                action.getValues().put(
                        row.getKey(),
                        row.getValue().replace(oldId, newId)
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

    private Set<TaskError> validateTriggerTask(Task task, Channel channel) {
        Set<TaskError> errors = new HashSet<>();

        if (channel == null) {
            errors.add(new TaskError("task.validation.error.triggerChannelNotRegistered"));
            return errors;
        }

        TaskEventInformation trigger = task.getTrigger();

        if (channel.getModuleName().equalsIgnoreCase(trigger.getModuleName())) {
            errors.addAll(TaskValidator.validateTrigger(task, channel));
        }

        return errors;
    }

    private Set<TaskError> validateActionTask(TaskActionInformation actionInformation,
                                              Channel channel) {
        Set<TaskError> errors = new HashSet<>();

        if (channel == null) {
            errors.add(new TaskError("task.validation.error.actionChannelNotRegistered"));
            return errors;
        }

        if (channel.getModuleName().equalsIgnoreCase(actionInformation.getModuleName())) {
            errors.addAll(TaskValidator.validateAction(actionInformation, channel));
        }

        return errors;
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
        allTasks.addOrUpdate(task);
    }

    private void removeTaskValidationErrors(Task task, String... messages) {
        for (String message : messages) {
            task.removeValidationError(message);
        }

        allTasks.addOrUpdate(task);
    }

    private void publishTaskDisabledMessage(String taskName, String level) {
        Map<String, Object> params = new HashMap<>();
        params.put("message", format("Task: %s was disabled due to validation errors.", taskName));
        params.put("level", level);
        params.put("moduleName", "tasks");

        MotechEvent motechEvent = new MotechEvent("org.motechproject.message", params);

        eventRelay.sendEventMessage(motechEvent);
    }
}
