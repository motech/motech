package org.motechproject.decisiontree.model;

import org.motechproject.model.MotechAuditableDataObject;

/**
 *
 */
public abstract class Prompt extends MotechAuditableDataObject {

    private static final long serialVersionUID =1L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Prompt{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prompt prompt = (Prompt) o;

        if (name != null ? !name.equals(prompt.name) : prompt.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
