package org.motechproject.mobileforms.api.domain;

import org.motechproject.mobileforms.api.validator.FormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.join;
import static ch.lambdaj.Lambda.on;

public class FormGroupValidator {
    private final Logger log = LoggerFactory.getLogger(FormGroupValidator.class);

    public void validate(FormBeanGroup formGroup, Map<String, FormValidator> validators, List<FormBean> allForms) {
        try {
            final List<FormBean> formBeansOrderedByPriority = formGroup.sortByDependency();
            final Map<String, FormBean> formBeansIndexedByName = index(formBeansOrderedByPriority, on(FormBean.class).getFormname());
            for (FormBean formBean : formBeansOrderedByPriority) {
                final List<String> invalidDependentForms = getInvalidDependentForms(formBean, formBeansIndexedByName);
                if (CollectionUtils.isEmpty(invalidDependentForms)) {
                    try {
                        formBean.addFormErrors(validators.get(formBean.getValidator()).validate(formBean, formGroup, allForms));
                    } catch (Exception e) {
                        formBean.addFormError(new FormError("Form Error:" + formBean.getFormname(), "Server exception, contact your administrator"));
                        log.error("Encountered exception while validating form group, " + formGroup.toString(), e);
                    }
                } else {
                    formBean.addFormError(new FormError("Form Error:" + join(invalidDependentForms, ","), "Dependent form failed"));
                }
            }
        } catch (Exception e) {
            formGroup.markAllFormAsFailed("Server exception, contact your administrator");
            log.error("Encountered exception while validating form group, " + formGroup.toString(), e);
        }
    }

    private List<String> getInvalidDependentForms(FormBean formBean, Map<String, FormBean> formBeansIndexedByName) {
        List<String> failedForms = new ArrayList<String>();
        if (formBean.getDepends() != null && !formBean.getDepends().isEmpty()) {
            for (String name : formBean.getDepends()) {
                if (formBeansIndexedByName.get(name) != null && formBeansIndexedByName.get(name).hasErrors()) {
                    failedForms.add(name);
                }
            }
        }
        return failedForms;
    }

}
