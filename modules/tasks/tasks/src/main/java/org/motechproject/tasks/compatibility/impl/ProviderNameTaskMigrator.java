package org.motechproject.tasks.compatibility.impl;

import org.motechproject.tasks.compatibility.TaskMigrator;
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.FilterSet;
import org.motechproject.tasks.domain.Lookup;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ProviderNameTaskMigrator implements TaskMigrator {

    private static final Pattern EXPR_PATTERN = Pattern.compile("(\\{\\{ad\\.)(\\d+)(.+?\\}\\})");
    private static final Pattern FILTER_KEY_PATTERN = Pattern.compile("(ad\\.)(\\d+)(.?+)");
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
        Matcher matcher = pattern.matcher(oldValue);
        if (matcher.matches()) {
            long providerId = Long.parseLong(matcher.group(ID_GROUP_INDEX));

            String providerName = providerIdToName(providerId, task);

            // replace provider id with provider name using regex groups
            return matcher.replaceAll("$1" + providerName + "$3");
        } else {
            return oldValue;
        }
    }

    private String providerIdToName(long providerId, Task task) {
        for (DataSource dataSource : task.getTaskConfig().getDataSources()) {
            if (providerId == dataSource.getProviderId()) {
                return dataSource.getProviderName();
            }
        }
        throw new IllegalArgumentException("Unable to migrate task. Data provider with id " + providerId + " not found");
    }
}
