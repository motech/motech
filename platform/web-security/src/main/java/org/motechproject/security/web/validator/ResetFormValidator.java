package org.motechproject.security.web.validator;

import org.motechproject.security.web.form.ResetForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ResetFormValidator implements Validator {


    public boolean supports(Class<?> clazz) {
        return ResetForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "security.password" ,"security.passwordRequired");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirmation", "security.confirmationRequired");

        if (!errors.getFieldValue("security.password").toString().equals(errors.getFieldValue("passwordConfirmation").toString())) {
            errors.reject("security.samePassword");
        }
    }
}
