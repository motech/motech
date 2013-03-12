package org.motechproject.tasks.validation;

import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.FieldParameter;
import org.motechproject.tasks.domain.Parameter;
import org.osgi.framework.Version;

import java.util.Collection;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

public abstract class GeneralValidator {

    protected static ValidationResult validateEventParameter(String objectName, String field, EventParameter parameter) {
        ValidationResult result = validateParameter(objectName, field, parameter);

        if (parameter != null) {
            result.addError(checkBlankValue(objectName + "." + field, "eventKey", parameter.getEventKey()));
        }

        return result;
    }

    protected static ValidationResult validateFieldParameter(String objectName, String field, FieldParameter parameter) {
        ValidationResult result = validateParameter(objectName, field, parameter);

        if (parameter != null) {
            result.addError(checkBlankValue(objectName + "." + field, "fieldKey", parameter.getFieldKey()));
        }

        return result;
    }

    protected static ValidationResult validateActionParameter(String objectName, String field, ActionParameter parameter) {
        ValidationResult result = validateParameter(objectName, field, parameter);

        if (parameter != null) {
            String name = objectName + "." + field;

            result.addError(checkBlankValue(name, "key", parameter.getKey()));
        }

        return result;
    }

    protected static BlankTaskError checkBlankValue(String objectName, String field, String value) {
        return isBlank(value) ? new BlankTaskError(objectName, field) : null;
    }

    protected static NullTaskError checkNullValue(String objectName, String field, Object value) {
        return null == value ? new NullTaskError(objectName, field) : null;
    }

    protected static EmptyCollectionTaskError checkEmpty(String objectName, String field, Collection<?> collection) {
        return isEmpty(collection) ? new EmptyCollectionTaskError(objectName, field) : null;
    }

    protected static VersionTaskError checkVersion(String objectName, String field, String value) {
        VersionTaskError error = null;

        try {
            Version.parseVersion(value);
        } catch (Exception e) {
            error = new VersionTaskError(objectName, field);
        }

        return error;
    }

    private static ValidationResult validateParameter(String objectName, String field, Parameter parameter) {
        ValidationResult result = new ValidationResult();

        result.addError(checkNullValue(objectName, field, parameter));

        if (result.isValid()) {
            String name = objectName + "." + field;

            result.addError(checkNullValue(name, "type", parameter.getType()));
            result.addError(checkBlankValue(name, "displayName", parameter.getDisplayName()));
        }

        return result;
    }

}
