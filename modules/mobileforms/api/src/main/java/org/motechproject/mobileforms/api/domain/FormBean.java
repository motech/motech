package org.motechproject.mobileforms.api.domain;

/**
 * \ingroup MobileForms
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.util.CollectionUtils.isEmpty;


/**
 * Implementer should extend this class to add required fields, field values will be mapped by
 * standard bean injection, i.e., matching field name and xml tag name. Implementation class/bean can be
 * used in json configuration of {@link Form}.
 */

public abstract class FormBean implements Serializable {
    private String studyName;
    private String formname;
    private String xmlContent;
    private String validator;
    private String formtype;
    private List<String> depends;

    public abstract String groupId();

    private List<FormError> formErrors;

    public FormBean() {
        formErrors = new ArrayList<FormError>();
    }

    protected FormBean(String studyName, String formName, String xmlContent, String validator, String formtype, List<String> depends) {
        this();
        this.studyName = studyName;
        this.formname = formName;
        this.xmlContent = xmlContent;
        this.validator = validator;
        this.formtype = formtype;
        this.depends = depends;
    }

    public FormBean(String xmlContent) {
        this();
        this.xmlContent = xmlContent;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public void setFormname(String formname) {
        this.formname = formname;
    }

    public String getValidator() {
        return validator;
    }

    public String getFormname() {
        return formname;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public String getFormtype() {
        return formtype;
    }

    public void setFormtype(String formtype) {
        this.formtype = formtype;
    }

    public List<FormError> getFormErrors() {
        return formErrors;
    }

    public void addFormErrors(List<FormError> formErrors) {
        this.formErrors.addAll(formErrors);
    }

    public void addFormError(FormError formError) {
        formErrors.add(formError);
    }

    public Boolean hasErrors() {
        return !isEmpty(formErrors);
    }

    public List<String> getDepends() {
        return depends;
    }

    public void setDepends(List<String> depends) {
        this.depends = depends;
    }

    public void clearFormErrors() {
        getFormErrors().clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FormBean)) {
            return false;
        }

        FormBean formBean = (FormBean) o;

        return Objects.equals(depends, formBean.depends) && Objects.equals(formErrors, formBean.formErrors) &&
                Objects.equals(formtype, formBean.formtype) && Objects.equals(formname, formBean.formname) &&
                Objects.equals(studyName, formBean.studyName) && Objects.equals(validator, formBean.validator) &&
                Objects.equals(xmlContent, formBean.xmlContent);
    }

    @Override
    public int hashCode() {
        int result = studyName != null ? studyName.hashCode() : 0;
        result = 31 * result + (formname != null ? formname.hashCode() : 0);
        result = 31 * result + (xmlContent != null ? xmlContent.hashCode() : 0);
        result = 31 * result + (validator != null ? validator.hashCode() : 0);
        result = 31 * result + (formtype != null ? formtype.hashCode() : 0);
        result = 31 * result + (depends != null ? depends.hashCode() : 0);
        result = 31 * result + (formErrors != null ? formErrors.hashCode() : 0);
        return result;
    }
}
