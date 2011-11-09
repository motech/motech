package org.motechproject.mobileforms.api.vo;

import java.util.ArrayList;
import java.util.List;

public class Study {
    private String name;
    private List<String> forms = new ArrayList<String>();

    public Study(String name) {
        this.name = name;
    }

    public Study(String name, List<String> forms) {
        this(name);
        this.forms = forms;
    }

    public String name() {
        return name;
    }

    public List<String> forms() {
        return forms;
    }

    public void addForm(String form) {
        forms.add(form);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Study)) return false;
        Study that = (Study) o;
        if (forms != null ? !forms.equals(that.forms) : that.forms != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (forms != null ? forms.hashCode() : 0);
        return result;
    }
}
