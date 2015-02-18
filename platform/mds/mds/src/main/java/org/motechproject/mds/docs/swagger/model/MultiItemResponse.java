package org.motechproject.mds.docs.swagger.model;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.motechproject.mds.docs.swagger.SwaggerConstants.REF;

/**
 * Represents a multi item response, such as an array response.
 * This means this response will represent a collection of items
 * returned by the REST endpoint.
 */
public class MultiItemResponse extends Response {

    private static final long serialVersionUID = -3218389365227942942L;

    private Schema schema;

    public MultiItemResponse() {
        super(null);
    }

    /**
     * Utility constructor, will construct this response with a reference to a given definition.
     * @param description the human readable description of this response
     * @param ref the ref to the definition of the model, i.e. #/definitions/org.example.Pet
     * @param type the type returned by this response, i.e. array (since this is a multi item response)
     */
    public MultiItemResponse(String description, String ref, String type) {
        super(description);

        Map<String, String> items = null;
        if (ref != null) {
            items = new LinkedHashMap<>();
            items.put(REF, ref);
        }

        if (type != null || items != null) {
            schema = new Schema(type, items);
        }

    }

    /**
     * @return the schema of the response
     * @see org.motechproject.mds.docs.swagger.model.Schema
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * @param schema the schema of the response
     * @see org.motechproject.mds.docs.swagger.model.Schema
     */
    public void setSchema(Schema schema) {
        this.schema = schema;
    }
}
