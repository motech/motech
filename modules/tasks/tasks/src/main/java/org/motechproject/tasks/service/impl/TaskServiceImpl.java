package org.motechproject.tasks.service.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.motechproject.tasks.events.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.events.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.events.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;
import static org.motechproject.tasks.events.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;

@Service("taskService")
public class TaskServiceImpl implements TaskService {
    private static final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

    private AllTasks allTasks;
    private ChannelService channelService;
    private TaskDataProviderService providerService;
    private EventRelay eventRelay;

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

        if (!isEmpty(errors)) {
            throw new ValidationException(TaskValidator.TASK, errors);
        }

        TaskEventInformation trigger = task.getTrigger();
        TaskActionInformation action = task.getAction();

        errors.addAll(validateTaskByTriggerChannel(task, channelService.getChannel(trigger.getModuleName())));
        errors.addAll(validateTaskByActionChannel(task, channelService.getChannel(action.getModuleName())));

        for (String providerId : task.getAdditionalData().keySet()) {
            errors.addAll(TaskValidator.validateByProvider(task, providerService.getProviderById(providerId)));
        }

        if (!isEmpty(errors)) {
            if (task.isEnabled()) {
                throw new ValidationException(TaskValidator.TASK, errors);
            } else {
                task.setValidationErrors(errors);
            }
        } else {
            task.setValidationErrors(null);
        }

        allTasks.addOrUpdate(task);
        LOG.info(String.format("Saved task: %s", task.getId()));
    }

    @Override
    public ActionEvent getActionEventFor(final Task task) throws ActionNotFoundException {
        TaskActionInformation actionInfo = task.getAction();
        Channel channel = channelService.getChannel(actionInfo.getModuleName());
        ActionEvent event = null;

        for (ActionEvent action : channel.getActionTaskEvents()) {
            if (action.accept(actionInfo)) {
                event = action;
                break;
            }
        }

        if (event == null) {
            throw new ActionNotFoundException(String.format("Cant find action for task: %s", task.getId()));
        }

        return event;
    }

    @Override
    public List<Task> getAllTasks() {
        return allTasks.getAll();
    }

    @Override
    public List<Task> findTasksForTrigger(final TriggerEvent trigger) {
        List<Task> tasks = allTasks.getAll();
        List<Task> result = new ArrayList<>(tasks.size());

        for (Task t : tasks) {
            if (t.getTrigger().getSubject().equalsIgnoreCase(trigger.getSubject())) {
                result.add(t);
            }
        }

        return result;
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
            throw new TriggerNotFoundException(String.format("Cant find trigger for subject: %s", subject));
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
            throw new IllegalArgumentException(String.format("Not found task with ID: %s", taskId));
        }

        allTasks.remove(t);
    }

    @MotechListener(subjects = CHANNEL_UPDATE_SUBJECT)
    public void validateTasksAfterChannelUpdate(MotechEvent event) {
        String moduleName = event.getParameters().get(CHANNEL_MODULE_NAME).toString();
        Channel channel = channelService.getChannel(moduleName);

        for (Task task : getAllTasks()) {
            Set<TaskError> errors = validateTaskByTriggerChannel(task, channel);

            if (errors != null) {
                setTaskValidationErrors(task, errors,
                        "validation.error.triggerNotExist",
                        "validation.error.triggerFieldNotExist"
                );
            }

            errors = validateTaskByActionChannel(task, channel);

            if (errors != null) {
                setTaskValidationErrors(task, errors, "validation.error.actionNotExist");
            }
        }
    }

    @MotechListener(subjects = DATA_PROVIDER_UPDATE_SUBJECT)
    public void validateTasksAfterTaskDataProviderUpdate(MotechEvent event) {
        String providerName = event.getParameters().get(DATA_PROVIDER_NAME).toString();

        TaskDataProvider provider = providerService.getProvider(providerName);

        for (Task task : getAllTasks()) {
            if (task.getAdditionalData().containsKey(provider.getId())) {
                Set<TaskError> errors = TaskValidator.validateByProvider(task, provider);

                setTaskValidationErrors(task, errors,
                        "validation.error.providerObjectFieldNotExist",
                        "validation.error.providerObjectNotExist",
                        "validation.error.providerObjectLookupNotExist"
                );
            }
        }
    }

    private Set<TaskError> validateTaskByTriggerChannel(Task task, Channel channel) {
        Set<TaskError> errors = null;
        TaskEventInformation trigger = task.getTrigger();

        if (channel.getModuleName().equalsIgnoreCase(trigger.getModuleName())) {
            errors = new HashSet<>(TaskValidator.validateByTrigger(task, channel));
        }

        return errors;
    }

    private Set<TaskError> validateTaskByActionChannel(Task task, Channel channel) {
        Set<TaskError> errors = null;
        TaskActionInformation action = task.getAction();

        if (channel.getModuleName().equalsIgnoreCase(action.getModuleName())) {
            errors = new HashSet<>(TaskValidator.validateByAction(task, channel));
        }

        return errors;
    }

    private void setTaskValidationErrors(Task task, Set<TaskError> errors, String... messages) {
        if (!isEmpty(errors)) {
            publishTaskDisabledMessage(task.getName(), task.isEnabled() ? "CRITICAL" : "INFO");

            task.setEnabled(false);
            task.addValidationErrors(errors);
            allTasks.addOrUpdate(task);
        } else {
            for (String message : messages) {
                task.removeValidationError(message);
            }

            allTasks.addOrUpdate(task);
        }
    }

    private void publishTaskDisabledMessage(String taskName, String level) {
        Map<String, Object> params = new HashMap<>();
        params.put("message", String.format("Task: %s was disabled due to validation errors.", taskName));
        params.put("level", level);
        params.put("moduleName", "tasks");

        MotechEvent motechEvent = new MotechEvent("org.motechproject.message", params);

        eventRelay.sendEventMessage(motechEvent);
    }

}
