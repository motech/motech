package org.motechproject.tasks.service.impl;

import org.motechproject.commons.couchdb.dao.BusinessIdNotUniqueException;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;
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

import static org.motechproject.tasks.util.TaskUtil.getChannelName;
import static org.motechproject.tasks.util.TaskUtil.getModuleName;
import static org.motechproject.tasks.util.TaskUtil.getModuleVersion;
import static org.motechproject.tasks.util.TaskUtil.getSubject;

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
        if (task.getAction().split(":").length != 4) {
            throw new IllegalArgumentException("Task action must contains channel.displayName:channel.moduleName:channel.moduleVersion:action.subject");
        }

        if (task.getTrigger().split(":").length != 4) {
            throw new IllegalArgumentException("Task trigger must contains channel.displayName:channel.moduleName:channel.moduleVersion:trigger.subject");
        }

        if (task.getActionInputFields() == null) {
            throw new IllegalArgumentException("Task must contains action input fields");
        }

        try {
            allTasks.addOrUpdate(task);
            LOG.info(String.format("Saved task: %s", task.getId()));
        } catch (BusinessIdNotUniqueException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public TaskEvent getActionEventFor(final Task task) throws ActionNotFoundException {
        Channel channel = channelService.getChannel(getChannelName(task.getAction()), getModuleName(task.getAction()),
                getModuleVersion(task.getAction()));
        String taskActionSubject = getSubject(task.getAction());
        TaskEvent event = null;

        if (channel.getActionTaskEvents() != null) {
            for (TaskEvent action : channel.getActionTaskEvents()) {
                if (action.getSubject().equalsIgnoreCase(taskActionSubject)) {
                    event = action;
                    break;
                }
            }
        }

        if (event == null) {
            throw new ActionNotFoundException(String.format("Cant find action for subject: %s", taskActionSubject));
        }

        return event;
    }

    @Override
    public List<Task> getAllTasks() {
        return allTasks.getAll();
    }

    @Override
    public List<Task> findTasksForTrigger(final TaskEvent trigger) {
        List<Task> tasks = allTasks.getAll();
        List<Task> result = new ArrayList<>(tasks.size());

        for (Task t : tasks) {
            if (getSubject(t.getTrigger()).equalsIgnoreCase(trigger.getSubject())) {
                result.add(t);
            }
        }

        return result;
    }

    @Override
    public TaskEvent findTrigger(String subject) throws TriggerNotFoundException {
        List<Channel> channels = channelService.getAllChannels();
        TaskEvent trigger = null;

        for (Channel c : channels) {
            for (TaskEvent t : c.getTriggerTaskEvents()) {
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
