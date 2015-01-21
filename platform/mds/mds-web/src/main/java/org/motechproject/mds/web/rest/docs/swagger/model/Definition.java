package org.motechproject.mds.web.rest.docs.swagger.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by pawel on 1/19/15.
 */
public class Definition implements Serializable {

    private static final long serialVersionUID = 8951125073007620498L;

    private List<String> required;
    private Map<String, Property> properties;

    public Definition() {
    }

    public Definition(List<String> required, Map<String, Property> properties) {
        this.required = required;
        this.properties = properties;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }
}
