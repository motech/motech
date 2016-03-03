package org.motechproject.mds.docs.swagger.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Represents a definition in the Swagger model. Definitions are the model
 * for the REST API, the represent objects that will be returned or are expected
 * by the API.
 */
public class Definition implements Serializable {

    private static final long serialVersionUID = 8951125073007620498L;

    private List<String> required;
    private Map<String, Property> properties;

    public Definition() {
    }

    /**
     * @param required a list of required field names, these fields will need to be provided when using the model described
     *                 by this definition
     * @param properties a map of all fields in this definition, where keys are the field names
     */
    public Definition(List<String> required, Map<String, Property> properties) {
        this.required = required;
        this.properties = properties;
    }

    /**
     * @return a list of required field names, these fields will need to be provided when using the model described by
     *         this definition
     */
    public List<String> getRequired() {
        return required;
    }

    /**
     * @param required a list of required field names, these fields will need to be provided when using the model
     *                 described by this definition
     */
    public void setRequired(List<String> required) {
        this.required = required;
    }

    /**
     * @return a map of all fields in this definition, where keys are the field names
     */
    public Map<String, Property> getProperties() {
        return properties;
    }

    /**
     * @param properties a map of all fields in this definition, where keys are the field names
     */
    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }
}
