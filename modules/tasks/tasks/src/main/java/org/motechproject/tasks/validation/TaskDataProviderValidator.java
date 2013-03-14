package org.motechproject.tasks.validation;

import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskDataProviderObject;

public final class TaskDataProviderValidator extends GeneralValidator {
    public static final String TASK_DATA_PROVIDER = "taskDataProvider";

    private TaskDataProviderValidator() {
    }

    public static ValidationResult validate(TaskDataProvider provider) {
        ValidationResult result = new ValidationResult();

        result.addError(checkBlankValue(TASK_DATA_PROVIDER, "name", provider.getName()));

        EmptyCollectionTaskError collectionError = checkEmpty(TASK_DATA_PROVIDER, "objects", provider.getObjects());

        if (null != collectionError) {
            result.addError(collectionError);
        } else {
            for (int i = 0; i < provider.getObjects().size(); ++i) {
                result.addErrors(validateObject(i, provider.getObjects().get(i)));
            }
        }

        return result;
    }

    private static ValidationResult validateObject(int index, TaskDataProviderObject object) {
        ValidationResult result = new ValidationResult();
        String field = "objects[" + index + "]";

        result.addError(checkNullValue(TASK_DATA_PROVIDER, field, object));

        if (result.isValid()) {
            String objectName = TASK_DATA_PROVIDER + "." + field;

            result.addError(checkBlankValue(objectName, "displayName", object.getDisplayName()));
            result.addError(checkBlankValue(objectName, "type", object.getType()));

            EmptyCollectionTaskError collectionError = checkEmpty(objectName, "lookupFields", object.getLookupFields());

            if (null != collectionError) {
                result.addError(collectionError);
            } else {
                for (int i = 0; i < object.getLookupFields().size(); ++i) {
                    result.addError(checkBlankValue(objectName, "lookupFields[" + i + "]", object.getLookupFields().get(i)));
                }
            }

            collectionError = checkEmpty(objectName, "fields", object.getFields());

            if (null != collectionError) {
                result.addError(collectionError);
            } else {
                for (int i = 0; i < object.getFields().size(); ++i) {
                    result.addErrors(validateFieldParameter(objectName, "fields[" + i + "]", object.getFields().get(i)));
                }
            }
        }

        return result;
    }

}
