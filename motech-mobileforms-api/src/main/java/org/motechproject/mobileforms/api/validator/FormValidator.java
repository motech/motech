package org.motechproject.mobileforms.api.validator;

import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.List;

public interface FormValidator {
    List<FormError> validate(FormBean formBean);
}
