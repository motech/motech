package org.motechproject.mobileforms.api.validator;

import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.List;

public class TestFormValidator extends FormValidator {
    @Override
    public List<FormError> validate(FormBean formBean, FormBeanGroup formGroup) {
        return super.validate(formBean, formGroup);
    }
}