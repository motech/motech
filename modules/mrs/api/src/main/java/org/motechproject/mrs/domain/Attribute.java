package org.motechproject.mrs.domain;

import java.io.Serializable;

public interface Attribute extends Serializable {

    String getName();

    void setName(String name);

    String getValue();

    void setValue(String value);
}
