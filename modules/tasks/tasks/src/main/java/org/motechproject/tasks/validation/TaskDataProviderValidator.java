package org.motechproject.tasks.validation;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tasks.domain.mds.task.TaskDataProvider;
import org.motechproject.tasks.domain.mds.task.TaskDataProviderObject;
import org.motechproject.tasks.domain.mds.task.TaskError;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Utility class for validating data providers.
 */
public final class TaskDataProviderValidator extends GeneralValidator {

    public static final String TASK_DATA_PROVIDER = "taskDataProvider";

    /**
     * Utility class, should not be instantiated.
     */
    private TaskDataProviderValidator() {
    }

    /**
     * Validates the given data provider by checking if all necessary data is set. Returns the set of {@code TaskError}s
     * containing information about missing fields.
     *
     * @param provider  the data provider for validation, not null
     * @return  the set of encountered errors
     */
    public static Set<TaskError> validate(TaskDataProvider provider) {
        Set<TaskError> errors = new HashSet<>();

        validateProviderName(errors, provider.getName());

        boolean empty = checkEmpty(errors, TASK_DATA_PROVIDER, "objects", provider.getObjects());

        if (!empty) {
            for (int i = 0; i < provider.getObjects().size(); ++i) {
                errors.addAll(validateObject(i, provider.getObjects().get(i)));
            }
        }

        return errors;
    }

    private static Set<TaskError> validateObject(int index, TaskDataProviderObject object) {
        Set<TaskError> errors = new HashSet<>();
        String field = "objects[" + index + "]";

        checkNullValue(errors, TASK_DATA_PROVIDER, field, object);

        if (isEmpty(errors)) {
            String objectName = TASK_DATA_PROVIDER + "." + field;

            checkBlankValue(errors, objectName, "displayName", object.getDisplayName());
            checkBlankValue(errors, objectName, "type", object.getType());

            boolean empty = checkEmpty(errors, objectName, "lookupFields", object.getLookupFields());

            if (!empty) {
                for (int i = 0; i < object.getLookupFields().size(); ++i) {
                    checkBlankValue(errors, objectName, "lookupFields[" + i + "]", object.getLookupFields().get(i).getDisplayName());
                }
            }

        }

        return errors;
    }

    private static void validateProviderName(Set<TaskError> errors, String providerName) {
        checkBlankValue(errors, TASK_DATA_PROVIDER, "name", providerName);

        // check only non-blank, we already validated against blank names one line above
        if (StringUtils.isNotBlank(providerName)) {
            Pattern pattern = Pattern.compile("^[A-Za-z0-9\\-_]+$");
            Matcher matcher = pattern.matcher(providerName);
            if (!matcher.matches()) {
                errors.add(new TaskError("task.validation.error.provider.name", providerName));
            }
        }
    }
}
