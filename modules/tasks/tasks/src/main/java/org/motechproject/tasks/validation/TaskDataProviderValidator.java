package org.motechproject.tasks.validation;

import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskDataProviderObject;
import org.motechproject.tasks.domain.TaskError;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public final class TaskDataProviderValidator extends GeneralValidator {
    public static final String TASK_DATA_PROVIDER = "taskDataProvider";

    private TaskDataProviderValidator() {
    }

    public static Set<TaskError> validate(TaskDataProvider provider) {
        Set<TaskError> errors = new HashSet<>();

        checkBlankValue(errors, TASK_DATA_PROVIDER, "name", provider.getName());

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
                    checkBlankValue(errors, objectName, "lookupFields[" + i + "]", object.getLookupFields().get(i));
                }
            }

            empty = checkEmpty(errors, objectName, "fields", object.getFields());

            if (!empty) {
                for (int i = 0; i < object.getFields().size(); ++i) {
                    errors.addAll(validateFieldParameter(objectName, "fields[" + i + "]", object.getFields().get(i)));
                }
            }
        }

        return errors;
    }

}
