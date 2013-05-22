package org.motechproject.commcare.domain;

public class FormValueAttribute implements FormNode{
    private final String value;

    public FormValueAttribute(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
         return value;
    }
}
