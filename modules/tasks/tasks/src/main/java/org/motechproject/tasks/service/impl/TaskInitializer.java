package org.motechproject.tasks.service.impl;

import org.motechproject.commons.api.DataProvider;
import org.motechproject.tasks.domain.mds.task.DataSource;
import org.motechproject.tasks.domain.mds.task.FilterSet;
import org.motechproject.tasks.domain.mds.task.Lookup;
import org.motechproject.tasks.domain.mds.task.TaskConfigStep;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.util.KeyEvaluator;
import org.motechproject.tasks.service.util.TaskContext;
import org.motechproject.tasks.service.util.TaskFilterExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import static org.motechproject.tasks.constants.TaskFailureCause.DATA_SOURCE;
import static org.motechproject.tasks.constants.TaskFailureCause.FILTER;


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

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskInitializer.class);

    private TaskContext taskContext;

    /**
     * Class constructor.
     *
     * @param taskContext  the task context
     */
    TaskInitializer(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    /**
     * Executes all config steps (loading data from data sources, checking filters) defined for this task.
     *
     * @param dataProviders  the map of data providers, not null or empty
     * @return  true if all steps were executed, false otherwise
     * @throws TaskHandlerException if there were error while handling task
     */
    @Transactional
    public boolean evalConfigSteps(Map<String, DataProvider> dataProviders) throws TaskHandlerException {
        LOGGER.info("Executing all config steps for task: {}", taskContext.getTask().getName());
        Iterator<TaskConfigStep> iterator = taskContext.getTask().getTaskConfig().getSteps().iterator();
        boolean result = true;

        TaskFilterExecutor taskFilterExecutor = new TaskFilterExecutor();

        while (result && iterator.hasNext()) {
            TaskConfigStep step = iterator.next();

            if (step instanceof DataSource) {
                DataSource ds = (DataSource) step;
                taskContext.addDataSourceObject(ds.getObjectId().toString(), getDataSourceObject(ds, dataProviders), ds.isFailIfDataNotFound());
                LOGGER.info("Task data source: {} for task: {} added", ds.getName(), taskContext.getTask().getName());
            } else if (step instanceof FilterSet && !isActionFilter((FilterSet) step)) {
                try {
                    FilterSet filterSet = (FilterSet) step;

                    result = taskFilterExecutor.checkFilters(filterSet.getFilters(), filterSet.getOperator(), taskContext);
                } catch (RuntimeException e) {
                    throw new TaskHandlerException(FILTER, "task.error.filterError", e);
                }
            }
        }
        return result;
    }

    @Transactional
    public int getActionFilters() {
        int firstActionFilterIndex = 0;
        boolean actionFilterExist = false;
        List<FilterSet> filterSetList = new ArrayList<>(taskContext.getTask().getTaskConfig().getFilters());

        for  (int i = 0; i < filterSetList.size(); i++){
            if(isActionFilter(filterSetList.get(i)) && !actionFilterExist) {
                firstActionFilterIndex = i;
                actionFilterExist = true;
            } else if (!isActionFilter(filterSetList.get(i))) {
                firstActionFilterIndex = filterSetList.size();
            }
        }
        return firstActionFilterIndex;
    }

    @Transactional
    public boolean checkActionFilter(int actualFilterIndex, List<FilterSet> filterSetList) throws TaskHandlerException{
        boolean result;
        TaskFilterExecutor taskFilterExecutor = new TaskFilterExecutor();

        try {
            result = taskFilterExecutor.checkFilters(filterSetList.get(actualFilterIndex).getFilters(), filterSetList.get(actualFilterIndex).getOperator(), taskContext);
        } catch (RuntimeException e) {
            throw new TaskHandlerException(FILTER, "task.error.filterError", e);
        }
        return result;
    }

    private boolean isActionFilter(FilterSet filterSet) {
        return filterSet.getActionFilterOrder() != null;
    }

    private Object getDataSourceObject(DataSource dataSource, Map<String, DataProvider> providers)
            throws TaskHandlerException {
        if (providers == null || providers.isEmpty()) {
            throw new TaskHandlerException(
                    DATA_SOURCE, "task.error.notFoundDataProvider", dataSource.getType()
            );
        }

        DataProvider provider = providers.get(dataSource.getProviderName());

        if (provider == null) {
            throw new TaskHandlerException(
                    DATA_SOURCE, "task.error.notFoundDataProvider", dataSource.getType()
            );
        }

        KeyEvaluator keyEvaluator = new KeyEvaluator(taskContext);
        Map<String, String> lookupFields = new HashMap<>();
        for (Lookup lookup : dataSource.getLookup()) {
            lookupFields.put(lookup.getField(), keyEvaluator.evaluateTemplateString(lookup.getValue()));
        }

        return provider.lookup(dataSource.getType(), dataSource.getName(), lookupFields);
    }
}
