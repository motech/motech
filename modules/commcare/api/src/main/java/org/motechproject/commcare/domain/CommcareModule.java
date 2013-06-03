package org.motechproject.commcare.domain;

import java.util.ArrayList;
import java.util.List;

public class CommcareModule {

    private String name;
    private List<FormSchema> formSchemas = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FormSchema> getFormSchemas() {
        return formSchemas;
    }

    public void setFormSchemas(List<FormSchema> formSchemas) {
        this.formSchemas = formSchemas;
    }
}
