package org.motechproject.tasks.validation;

import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.FieldParameter;
import org.motechproject.tasks.domain.Parameter;
import org.motechproject.tasks.domain.TaskError;
import org.osgi.framework.Version;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.tasks.domain.TaskErrorType.BLANK;
import static org.motechproject.tasks.domain.TaskErrorType.EMPTY_COLLECTION;
import static org.motechproject.tasks.domain.TaskErrorType.NULL;
import static org.motechproject.tasks.domain.TaskErrorType.VERSION;

public abstract class GeneralValidator {

    protected static Set<TaskError> validateEventParameter(String objectName, String field, EventParameter parameter) {
        Set<TaskError> errors = validateParameter(objectName, field, parameter);

        if (parameter != null) {
            checkBlankValue(errors, objectName + "." + field, "eventKey", parameter.getEventKey());
        }

        return errors;
    }

    protected static Set<TaskError> validateFieldParameter(String objectName, String field, FieldParameter parameter) {
        Set<TaskError> errors = validateParameter(objectName, field, parameter);

        if (parameter != null) {
            checkBlankValue(errors, objectName + "." + field, "fieldKey", parameter.getFieldKey());
        }

        return errors;
    }

    protected static Set<TaskError> validateActionParameter(String objectName, String field, ActionParameter parameter) {
        Set<TaskError> errors = validateParameter(objectName, field, parameter);

        if (parameter != null) {
            String name = objectName + "." + field;

            checkNullValue(errors, name, "order", parameter.getOrder());
            checkBlankValue(errors, name, "key", parameter.getKey());
        }

        return errors;
    }

    protected static void checkBlankValue(Set<TaskError> errors, String objectName, String field, String value) {
        if (isBlank(value)) {
            errors.add(new TaskError(BLANK, objectName, field));
        }
    }

    protected static void checkNullValue(Set<TaskError> errors, String objectName, String field, Object value) {
        if (null == value) {
            errors.add(new TaskError(NULL, objectName, field));
        }
    }

    protected static boolean checkEmpty(Set<TaskError> errors, String objectName, String field, Collection<?> collection) {
        boolean empty = isEmpty(collection);

        if (empty) {
            errors.add(new TaskError(EMPTY_COLLECTION, objectName, field));
        }

        return empty;
    }

    protected static void checkVersion(Set<TaskError> errors, String objectName, String field, String value) {
        try {
            Version.parseVersion(value);
        } catch (Exception e) {
            errors.add(new TaskError(VERSION, objectName, field));
        }
    }

    private static Set<TaskError> validateParameter(String objectName, String field, Parameter parameter) {
        Set<TaskError> errors = new HashSet<>();

        checkNullValue(errors, objectName, field, parameter);

        if (isEmpty(errors)) {
            String name = objectName + "." + field;

            checkNullValue(errors, name, "type", parameter.getType());
            checkBlankValue(errors, name, "displayName", parameter.getDisplayName());
        }

        return errors;
    }

}
