package org.motechproject.mobileforms.api.domain;

import org.motechproject.MotechException;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;

/**
 * Represents set of forms, also supports sorting based on dependency.
 */
public class FormBeanGroup implements Serializable {

    private List<FormBean> formBeans;

    public FormBeanGroup(List<FormBean> formBeans) {
        this.formBeans = formBeans;
    }

    public List<FormBean> getFormBeans() {
        return formBeans;
    }

    public void validate() {

    }

    public List<FormBean> sortByDependency() {
        final ArrayList<FormBean> formBeansClone = new ArrayList<FormBean>(formBeans);

        List<FormBean> sortedFormBeans = new ArrayList<FormBean>();
        for (FormBean formBean : formBeans) {
            if (CollectionUtils.isEmpty(formBean.getDepends())) {
                sortedFormBeans.add(formBean);
                formBeansClone.remove(formBean);
            }
        }

        final List<String> namesOfForms = collect(formBeans, on(FormBean.class).getFormname());
        resolveDependency(sortedFormBeans, formBeansClone, namesOfForms);
        return sortedFormBeans;
    }


    private void resolveDependency(List<FormBean> sortedFormBeans, List<FormBean> unprocessedFormBeans, List<String> namesOfForms) {
        if (!unprocessedFormBeans.isEmpty()) {
            int processedNodes = 0;
            for (FormBean unprocessedFormBean : new ArrayList<FormBean>(unprocessedFormBeans)) {
                boolean independent = true;
                for (String dependent : unprocessedFormBean.getDepends()) {
                    if (namesOfForms.contains(dependent)) {
                        List<String> processedFormNames = collect(sortedFormBeans, on(FormBean.class).getFormname());
                        if (!processedFormNames.contains(dependent)) {
                            independent = false;
                            break;
                        }
                    }
                }

                if (independent) {
                    sortedFormBeans.add(unprocessedFormBean);
                    unprocessedFormBeans.remove(unprocessedFormBean);
                    processedNodes++;
                }
            }

            if (processedNodes > 0) {
                resolveDependency(sortedFormBeans, unprocessedFormBeans, namesOfForms);
            } else {
                throw new MotechException("Detected cyclic mobile form dependencies");
            }
        }
    }

    public void markAllFormAsFailed(String message) {
        for (FormBean formBean : formBeans) {
            formBean.addFormError(new FormError("Form Error:" + formBean.getFormname(), message));
        }
    }

    public List<FormBean> validForms() {
        return filter(having(on(FormBean.class).getFormErrors().isEmpty()), formBeans);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FormBeanGroup)) {
            return false;
        }

        FormBeanGroup that = (FormBeanGroup) o;
        if (formBeans != null ? !formBeans.equals(that.formBeans) : that.formBeans != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return formBeans != null ? formBeans.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FormBeanGroup{" +
                "formBeans=" + formBeans +
                '}';
    }
}
