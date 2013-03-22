package org.motechproject.mrs.model;

import java.lang.String;
import org.motechproject.mrs.domain.MRSAttribute;

public class MRSAttributeDto implements MRSAttribute {
    private String name;
    private String value;

    public MRSAttributeDto() {
    }

    public MRSAttributeDto(String name, String value) {
        this.name = name;
        this.value = value;
    }

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
