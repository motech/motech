package org.motechproject.mobileforms.api.domain;

public class FormError {
    private String parameter;
    private String error;

    public FormError(String parameter, String error) {
        this.parameter = parameter;
        this.error = error;
    }

    public String getParameter() {
        return parameter;
    }

    public String getError() {
        return error;
    }

}
