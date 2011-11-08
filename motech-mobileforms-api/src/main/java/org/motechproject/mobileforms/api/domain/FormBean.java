package org.motechproject.mobileforms.api.domain;

public class FormBean {
    private String validator;
    private String formName;
    private String studyName;

    public void setValidator(String validator){
        this.validator = validator;
    }

    public void setFormName(String formName){
        this.formName = formName;
    }

    public String getValidator() {
        return validator;
    }

    public String getFormName() {
        return formName;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }
}
