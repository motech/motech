package org.motechproject.mobileforms.api.validator;

import org.apache.commons.beanutils.PropertyUtils;
import org.motechproject.MotechException;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.annotations.ValidationMarker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.motechproject.mobileforms.api.utils.CollectionUtils.addIfNotNull;

public abstract class FormValidator {
    public List<FormError> validate(final FormBean formBean) {

        List<FormError> formErrors = new ArrayList<FormError>();
        for (Field field : formBean.getClass().getDeclaredFields()) {
            String fieldName = null;
            try {
                fieldName = field.getName();
                Object value = PropertyUtils.getProperty(formBean, fieldName);
                for (Annotation annotation : field.getAnnotations()) {
                    FormError formError = getValidationHandler(annotation).validate(value, fieldName, field.getType(), annotation);
                    addIfNotNull(formErrors, formError);
                }
            } catch (Exception e) {
                formErrors.add(new FormError(fieldName, "Server exception, contact your administrator"));
            }
        }
        return formErrors;
    }

    private FieldValidator getValidationHandler(Annotation annotation) {
        Annotation validationMarker = annotation.annotationType().getAnnotation(ValidationMarker.class);
        if (validationMarker == null) {
            throw new MotechException("Field validator has not been annotated with ValidationMarker, " + annotation.annotationType().getName());
        }
        try {
            Class<FieldValidator> fieldValidatorClass = (Class<FieldValidator>) validationMarker.annotationType().getMethod("handler").invoke(validationMarker);
            return fieldValidatorClass.newInstance();
        } catch (Exception e) {
            throw new MotechException("Exception while instantiating validation handler, this should never happen", e);
        }
    }
}
