package org.motechproject.mobileforms.api.valueobjects;

import java.util.List;

public class GroupNameAndForms {
    private String groupName;
    private List<String> forms;

    public GroupNameAndForms(String groupName, List<String> forms) {
        this.groupName = groupName;
        this.forms = forms;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<String> getForms() {
        return forms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupNameAndForms)) return false;

        GroupNameAndForms that = (GroupNameAndForms) o;

        if (forms != null ? !forms.equals(that.forms) : that.forms != null) return false;
        if (groupName != null ? !groupName.equals(that.groupName) : that.groupName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupName != null ? groupName.hashCode() : 0;
        result = 31 * result + (forms != null ? forms.hashCode() : 0);
        return result;
    }
}
