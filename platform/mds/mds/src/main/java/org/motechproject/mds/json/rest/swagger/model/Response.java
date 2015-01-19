package org.motechproject.mds.json.rest.swagger.model;

import java.io.Serializable;

/**
 * Created by pawel on 1/19/15.
 */
public class Response implements Serializable {

    private static final long serialVersionUID = -3218389365227942942L;

    private String description;
    private Schema schema;

    public Response() {
    }

    public Response(String description, Schema schema) {
        this.description = description;
        this.schema = schema;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }
}
