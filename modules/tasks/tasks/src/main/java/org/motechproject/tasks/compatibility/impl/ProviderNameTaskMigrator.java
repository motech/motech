package org.motechproject.tasks.compatibility.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tasks.compatibility.TaskMigrator;
import org.motechproject.tasks.domain.mds.task.DataSource;
import org.motechproject.tasks.domain.mds.task.Filter;
import org.motechproject.tasks.domain.mds.task.FilterSet;
import org.motechproject.tasks.domain.mds.task.Lookup;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.ex.ProviderNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class is responsible for migrating tasks that use the database id as the provider id.
 * Since the database ID is not reliable, and it will not work across different systems (in case of import/export)  we
 * have decided to drop it in favor of using provider names. This migrator will switch the provider ids used in expressions
 * to provider names. Provider names already stored in the current task config are used.
 */
@Component
public class ProviderNameTaskMigrator implements TaskMigrator {

    private static final Pattern EXPR_PATTERN = Pattern.compile("(\\{\\{ad\\.)(\\d+)(.+?\\}\\})");
    private static final Pattern FILTER_KEY_PATTERN = Pattern.compile("(ad\\.)(\\d+)(.+?)");
    private static final int ID_GROUP_INDEX = 2;

    @Override
    public void migrate(Task task) {
        migrateActions(task);
        migrateDataSources(task);
        migrateFilters(task);
    }

    private void migrateActions(Task task) {
        // replace data source references in actions
        for (TaskActionInformation action : task.getActions()) {
            for (Map.Entry<String, String> entry : action.getValues().entrySet()) {
                String oldVal = entry.getValue();

                String newVal = replaceTaskValue(oldVal, EXPR_PATTERN, task);
                entry.setValue(newVal);
            }
        }
    }

    private void migrateDataSources(Task task) {
        // replace data source references in other data source lookups
        for (DataSource dataSource : task.getTaskConfig().getDataSources()) {
            for (Lookup lookup : dataSource.getLookup()) {
                String oldVal = lookup.getValue();

                String newVal = replaceTaskValue(oldVal, EXPR_PATTERN, task);
                lookup.setValue(newVal);
            }
        }
    }

    private void migrateFilters(Task task) {
        for (FilterSet filterSet : task.getTaskConfig().getFilters()) {
            for (Filter filter : filterSet.getFilters()) {
                // replace data source references in filter expressions
                String oldVal = filter.getExpression();

                String newVal = replaceTaskValue(oldVal, EXPR_PATTERN, task);
                filter.setExpression(newVal);
                // also make sure the key is correct
                oldVal = filter.getKey();

                newVal = replaceTaskValue(oldVal, FILTER_KEY_PATTERN, task);
                filter.setKey(newVal);
            }
        }
    }

    private String replaceTaskValue(String oldValue, Pattern pattern, Task task) {
        String result = oldValue;
        if (StringUtils.isNotBlank(oldValue)) {
            Matcher matcher = pattern.matcher(oldValue);
            if (matcher.matches()) {
                long providerId = Long.parseLong(matcher.group(ID_GROUP_INDEX));

                String providerName = providerIdToName(providerId, task);

                // replace provider id with provider name using regex groups
                result = matcher.replaceAll("$1" + providerName + "$3");
            }
        }
        return result;
    }

    private String providerIdToName(long providerId, Task task) {
        for (DataSource dataSource : task.getTaskConfig().getDataSources()) {
            if (providerId == dataSource.getProviderId()) {
                return dataSource.getProviderName();
            }
        }
        throw new ProviderNotFoundException(task.getName(), providerId);
    }
}
