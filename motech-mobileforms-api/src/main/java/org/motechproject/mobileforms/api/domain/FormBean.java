package org.motechproject.mobileforms.api.domain;

public class FormBean {
    private String validator;
    private String name;

    public void setValidator(String validator){
        this.validator = validator;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getValidator() {
        return validator;
    }

    public String getName() {
        return name;
    }
}
