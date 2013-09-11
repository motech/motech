package org.motechproject.tasks.service;

import org.motechproject.commons.api.DataProvider;
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.FilterSet;
import org.motechproject.tasks.domain.TaskConfigStep;
import org.motechproject.tasks.ex.TaskHandlerException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.motechproject.tasks.events.constants.TaskFailureCause.DATA_SOURCE;
import static org.motechproject.tasks.events.constants.TaskFailureCause.FILTER;


/**
 * The <code>TaskInitializer</code> class prepares an action in the task definition to execution.
 * <p/>
 * <ul>
 * <li><b>evalConfigSteps</b> - executes all config steps (load data sources, check filters) defined in the task,</li>
 * </ul>
 *
 * @see TaskTriggerHandler
 * @see TaskActionExecutor
 * @since 0.20
 */
class TaskInitializer {

    private TaskContext taskContext;

    TaskInitializer(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    public boolean evalConfigSteps(Map<String, DataProvider> dataProviders) throws TaskHandlerException {
        Iterator<TaskConfigStep> iterator = taskContext.getTask().getTaskConfig().getSteps().iterator();
        boolean result = true;

        TaskFilterExecutor taskFilterExecutor = new TaskFilterExecutor();

        while (result && iterator.hasNext()) {
            TaskConfigStep step = iterator.next();

            if (step instanceof DataSource) {
                DataSource ds = (DataSource) step;
                taskContext.addDataSourceObject(ds.getObjectId().toString(), getDataSourceObject(ds, dataProviders), ds.isFailIfDataNotFound());
            } else if (step instanceof FilterSet) {
                try {
                    result = taskFilterExecutor.checkFilters(((FilterSet) step).getFilters(), taskContext);
                } catch (TaskHandlerException e) {
                    throw e;
                } catch (Exception e) {
                    throw new TaskHandlerException(FILTER, "task.error.filterError", e);
                }
            }
        }
        return result;
    }

    private Object getDataSourceObject(DataSource dataSource, Map<String, DataProvider> providers)
            throws TaskHandlerException {
        if (providers == null || providers.isEmpty()) {
            throw new TaskHandlerException(
                    DATA_SOURCE, "task.error.notFoundDataProvider", dataSource.getType()
            );
        }

        DataProvider provider = providers.get(dataSource.getProviderId());

        if (provider == null) {
            throw new TaskHandlerException(
                    DATA_SOURCE, "task.error.notFoundDataProvider", dataSource.getType()
            );
        }

        KeyEvaluator keyEvaluator = new KeyEvaluator(taskContext);
        Map<String, String> lookupFields = new HashMap<>();
        for (DataSource.Lookup lookup : dataSource.getLookup()) {
            lookupFields.put(lookup.getField(), keyEvaluator.evaluateTemplateString(lookup.getValue()));
        }

        return provider.lookup(dataSource.getType(), lookupFields);
    }
}
