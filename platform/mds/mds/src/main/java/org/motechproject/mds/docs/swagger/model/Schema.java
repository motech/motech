package org.motechproject.mds.docs.swagger.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents a schema of a multi item response.
 * Generally used for referencing definitions.
 * @see org.motechproject.mds.docs.swagger.model.Definition
 */
public class Schema implements Serializable {

    private static final long serialVersionUID = -6514177795552932633L;

    private String type;
    private Map<String, String> items;

    public Schema() {
    }

    /**
     * @param type the type of this schema, if a ref is used this can only be an array type
     * @param items the items this schema, this will contain the ref
     */
    public Schema(String type, Map<String, String> items) {
        this.type = type;
        this.items = items;
    }

    /**
     * @return the type of this schema, if a ref is used this can only be an array type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type of this schema, if a ref is used this can only be an array type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the items this schema, this will contain the ref
     */
    public Map<String, String> getItems() {
        return items;
    }

    /**
     * @param items the items this schema, this will contain the ref
     */
    public void setItems(Map<String, String> items) {
        this.items = items;
    }
}
