package org.motechproject.tasks.validation;

import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.channel.EventParameter;
import org.motechproject.tasks.domain.mds.task.FieldParameter;
import org.motechproject.tasks.domain.mds.channel.ActionParameter;
import org.motechproject.tasks.domain.enums.ParameterType;
import org.motechproject.tasks.domain.mds.Parameter;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.tasks.domain.enums.TaskErrorType.BLANK;
import static org.motechproject.tasks.domain.enums.TaskErrorType.EMPTY_COLLECTION;
import static org.motechproject.tasks.domain.enums.TaskErrorType.NULL;
import static org.motechproject.tasks.domain.enums.TaskErrorType.VERSION;

/**
 * General class providing utility methods for validating fields for being null, blank or missing. This is an abstract
 * class that serves as a base for task related validators.
 */
public abstract class GeneralValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralValidator.class);

    /**
     * Validates that none of the event parameter fields is either null or blank. Returns the set of {@code TaskError}s
     * encountered during validation.
     *
     * @param objectName  the object name
     * @param field  the field name
     * @param parameter  the event parameter to be validated, null will result in adding {@code TaskError} to the result
     * @return  the set of encountered errors
     */
    protected static Set<TaskError> validateEventParameter(String objectName, String field, EventParameter parameter) {
        Set<TaskError> errors = validateParameter(objectName, field, parameter);

        if (parameter != null) {
            checkBlankValue(errors, objectName + "." + field, "eventKey", parameter.getEventKey());
        }

        return errors;
    }

    /**
     * Validates that none of the field parameter fields is either null or blank. Returns the set of {@code TaskError}s
     * encountered during validation.
     *
     * @param objectName  the object name
     * @param field  the field name
     * @param parameter  the field parameter to be validated, null will result in adding {@code TaskError} to the result
     * @return  the set of encountered errors
     */
    protected static Set<TaskError> validateFieldParameter(String objectName, String field, FieldParameter parameter) {
        Set<TaskError> errors = validateParameter(objectName, field, parameter);

        if (parameter != null) {
            checkBlankValue(errors, objectName + "." + field, "fieldKey", parameter.getFieldKey());
        }

        return errors;
    }

    /**
     * Validates that none of the action parameter fields is either null or blank. Returns the set of {@code TaskError}s
     * encountered during validation.
     *
     * @param objectName  the object name
     * @param field  the field name
     * @param parameter  the action parameter to be validated, null will result in adding {@code TaskError} to the result
     * @return  the set of encountered errors
     */
    protected static Set<TaskError> validateActionParameter(String objectName, String field, ActionParameter parameter) {
        Set<TaskError> errors = validateParameter(objectName, field, parameter);

        if (parameter != null) {
            String name = objectName + "." + field;

            checkNullValue(errors, name, "order", parameter.getOrder());
            checkBlankValue(errors, name, "key", parameter.getKey());

            if(parameter.getType() == ParameterType.SELECT){
                checkEmpty(errors, name, "options", parameter.getOptions());
            }

        }

        return errors;
    }

    /**
     * Checks whether given value is blank. If it is blank, new {@code TaskError} will be added to the {@code errors}.
     *
     * @param errors  the collection of errors that acts as the result of validation, not null
     * @param objectName  the object name
     * @param field  the field name
     * @param value  the value to be validated, null will result in adding {@code TaskError} to the {@code errors}
     */
    protected static void checkBlankValue(Set<TaskError> errors, String objectName, String field, String value) {
        if (isBlank(value)) {
            errors.add(new TaskError(BLANK, field, objectName));
        }
    }

    /**
     * Checks whether given value is null. If it is null, new {@code TaskError} will be added to the {@code errors}.
     *
     * @param errors  the collection of errors that acts as the result of validation, not null
     * @param objectName  the object name
     * @param field  the field name
     * @param value  the value to be checked, null will result in adding {@code TaskError} to the {@code errors}
     */
    protected static void checkNullValue(Set<TaskError> errors, String objectName, String field, Object value) {
        if (null == value) {
            errors.add(new TaskError(NULL, field, objectName));
        }
    }

    /**
     * Checks whether the given collection is empty. If it is empty, new {@code TaskError} will be added to the
     * {@code errors}.
     *
     * @param errors  the collection of errors that acts as the result of validation, not null
     * @param objectName  the object name
     * @param field  the field name
     * @param collection  the collection to be checked, null will result in adding {@code TaskError} to the
     *                    {@code errors} and returning false
     * @return  true if the given collection is empty, false otherwise
     */
    protected static boolean checkEmpty(Set<TaskError> errors, String objectName, String field, Collection<?> collection) {
        boolean empty = isEmpty(collection);

        if (empty) {
            errors.add(new TaskError(EMPTY_COLLECTION, field, objectName));
        }

        return empty;
    }

    /**
     * Checks whether the given version is properly formatted. Passing null or empty {@code String} as {@code value}
     * will NOT result in adding {@code TaskError} to the {@code error}.
     *
     * @param errors  the collection of errors that acts as the result of validation, not null
     * @param objectName  the object name
     * @param field  the field name
     * @param value  the value to be checked
     */
    protected static void checkVersion(Set<TaskError> errors, String objectName, String field, String value) {
        try {
            Version.parseVersion(value);
        } catch (RuntimeException e) {
            LOGGER.error("An exception occurred during validation of field - {}, objectName - {}, value - {}",
                field, objectName, value);
            errors.add(new TaskError(VERSION, field, objectName));
        }
    }

    /**
     * Validates that none of the parameter fields is either null or blank. Returns the set of {@code TaskError}s
     * encountered during validation.
     *
     * @param objectName  the object name
     * @param field  the field name
     * @param parameter  the parameter to be validated, null will result in adding {@code TaskError} to the {@code errors}
     * @return  the set of encountered errors
     */
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
