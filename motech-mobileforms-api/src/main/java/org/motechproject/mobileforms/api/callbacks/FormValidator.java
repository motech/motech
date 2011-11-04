package org.motechproject.mobileforms.api.callbacks;

import org.motechproject.mobileforms.api.domain.FormData;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.List;

public interface FormValidator {
    List<FormError> validate(FormData formData);
}
