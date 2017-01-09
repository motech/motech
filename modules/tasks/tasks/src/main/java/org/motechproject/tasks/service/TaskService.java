package org.motechproject.tasks.service;

import org.motechproject.commons.api.TasksEventParser;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.exception.ActionNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Service interface for managing tasks.
 */
public interface TaskService {

    /**
     * Saves the given task in the database and returns the list
     * of errors that will occur when enabling task.
     *
     * @param task  the task to be saved, not null
     * @return list of task errors
     */
    Set<TaskError> save(final Task task);

    /**
     * Sets the task with the given ID enabled or disabled and returns the list
     * of errors that will occur when enabling task.
     *
     * @param task  the task to be set, not null
     * @return list of task errors
     */
    Set<TaskError> setEnabledOrDisabled(final Task task);

    /**
     * Returns the action event that matches the given information about the task action.
     *
     * @param taskActionInformation  the action information, not null
     * @return the action event matching the given information
     * @throws ActionNotFoundException  when action was not found
     */
    ActionEvent getActionEventFor(TaskActionInformation taskActionInformation) throws ActionNotFoundException;

    /**
     * Returns the list of all tasks.
     *
     * @return  the list of all tasks
     */
    List<Task> getAllTasks();

    /**
     * Return the list of tasks with the given name.
     *
     * @param name  the task name, null returns null
     * @return  the list of tasks meeting the given condition
     */
    List<Task> findTasksByName(String name);

    /**
     * Returns the list of active tasks with the given trigger. Used for retrieving tasks to execute when a given
     * trigger fires.
     *
     * @param trigger  the trigger, null returns empty list
     * @return  the list of active tasks
     */
    List<Task> findActiveTasksForTrigger(final TriggerEvent trigger);

    /**
     * Returns the list of active tasks for the given trigger subject. Used for retrieving tasks to execute when a given
     * trigger fires.
     *
     * @param subject  the subject of the trigger, null returns empty list
     * @return  the list of active tasks
     */
    List<Task> findActiveTasksForTriggerSubject(final String subject);

    /**
     * Returns the list of task dependent on the module with the given name.
     *
     * @param moduleName  the name of the module, not null
     * @return  the list of tasks
     */
    List<Task> findTasksDependentOnModule(String moduleName);

    /**
     * Looks for implementations of the {@link org.motechproject.commons.api.TasksEventParser} that have
     * been exposed as OSGi services by bundles. For all found implementations, this method will match
     * names returned by the <code>getName()</code> method of the <code>TasksEventParser</code> and
     * passed as parameter to this method. If a match is found, the implementation is returned.
     *
     * @param name  A name of the parser, that will be matched with <code>getName()</code> of the
     *              implementations
     * @return  Implementation of the {@link org.motechproject.commons.api.TasksEventParser} that returns
     *          the same name via <code>getName()</code> method as the name passed to the method
     */
    TasksEventParser findCustomParser(String name);

    /**
     * Returns the task with the given ID.
     *
     * @param taskId  the ID of the task, null returns null
     * @return  the task with the given ID, null if task with the given ID wasn't found
     */
    Task getTask(Long taskId);

    /**
     * Deletes the task with the given ID.
     *
     * @param taskId  the ID of the task, not null
     */
    void deleteTask(Long taskId);

    /**
     * Exports the task with the given ID as a JSON {@code String}.
     *
     * @param taskId  the ID of the task, not null
     * @return  the JSON as a {@code String}
     */
    String exportTask(Long taskId);

    /**
     * Imports the task from JSON {@code String}.
     *
     * @param json  the task in JSON format
     * @return  the imported task, not null
     * @throws IOException when there were problems while parsing the JSON
     */
    Task importTask(String json) throws IOException;
}
