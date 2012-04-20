package org.motechproject.mobileforms.api.validator;

import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.ArrayList;
import java.util.List;

public class TestFormValidator<V extends FormBean> extends FormValidator<V> {

    @Override
    public List<FormError> businessValidations(FormBean formBean) {
        return new ArrayList<FormError>();
    }
}