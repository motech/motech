package org.motechproject.mobileforms.api.domain;

import java.io.Serializable;

public class FormBean implements Serializable{
    private String studyName;
    private String formname;
    private String xmlContent;
    private String validator;
    private String formtype;

    public void setValidator(String validator){
        this.validator = validator;
    }

    public void setFormname(String formname){
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
}
