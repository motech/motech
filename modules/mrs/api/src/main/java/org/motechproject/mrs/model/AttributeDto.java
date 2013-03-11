package org.motechproject.mrs.model;

import org.motechproject.mrs.domain.Attribute;
import java.lang.String;

public class AttributeDto implements Attribute {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
