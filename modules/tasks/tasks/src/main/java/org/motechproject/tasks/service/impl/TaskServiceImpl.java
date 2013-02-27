package org.motechproject.tasks.service.impl;

import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.motechproject.tasks.repository.AllTasks;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

@Service("taskService")
public class TaskServiceImpl implements TaskService {
    private static final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

    private AllTasks allTasks;
    private ChannelService channelService;

    @Autowired
    public TaskServiceImpl(AllTasks allTasks, ChannelService channelService) {
        this.allTasks = allTasks;
        this.channelService = channelService;
    }

    @Override
    public void save(final Task task) {
        if (task.getTrigger() == null) {
            throw new IllegalArgumentException("Task must contains information about trigger");
        }

        if (task.getAction() == null) {
            throw new IllegalArgumentException("Task must contains information about action");
        }

        if (task.getActionInputFields() == null) {
            throw new IllegalArgumentException("Task must contains action input fields");
        }

        if (isBlank(task.getName())) {
            throw new IllegalArgumentException("Task must contains name");
        }

        allTasks.addOrUpdate(task);
        LOG.info(String.format("Saved task: %s", task.getId()));
    }

    @Override
    public ActionEvent getActionEventFor(final Task task) throws ActionNotFoundException {
        TaskActionInformation actionInfo = task.getAction();
        Channel channel = channelService.getChannel(actionInfo.getChannelName(), actionInfo.getModuleName(), actionInfo.getModuleVersion());
        ActionEvent event = null;

        if (channel.getActionTaskEvents() != null) {
            for (ActionEvent action : channel.getActionTaskEvents()) {
                if (action.accept(actionInfo)) {
                    event = action;
                    break;
                }
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

}
