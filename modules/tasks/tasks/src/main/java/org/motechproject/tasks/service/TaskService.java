package org.motechproject.tasks.service;

import org.motechproject.commons.api.TasksEventParser;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;

import java.io.IOException;
import java.util.List;

public interface TaskService {

    void save(final Task task);

    ActionEvent getActionEventFor(TaskActionInformation taskActionInformation) throws ActionNotFoundException;

    List<Task> getAllTasks();

    List<Task> findTasksForTrigger(final TriggerEvent trigger);

    List<Task> findTasksForTriggerSubject(final String subject);

    List<Task> findTasksDependentOnModule(String moduleName);

    TriggerEvent findTrigger(String subject) throws TriggerNotFoundException;

    /**
     * Looks for implementations of the {@link org.motechproject.commons.api.TasksEventParser} that have
     * been exposed as OSGi services by bundles. For all found implementations, this method will match
     * names returned by the <code>getName()</code> method of the <code>TasksEventParser</code> and
     * passed as parameter to this method. If a match is found, the implementation is returned.
     *
     * @param name A name of the parser, that will be matched with <code>getName()</code> of the
     *             implementations
     * @return Implementation of the {@link org.motechproject.commons.api.TasksEventParser} that returns
     *         the same name via <code>getName()</code> method as the name passed to the method
     */
    TasksEventParser findCustomParser(String name);

    Task getTask(Long taskId);

    void deleteTask(Long taskId);

    String exportTask(Long taskId);


    Task importTask(String json) throws IOException;
}
