package org.motechproject.mds.docs.swagger.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a response that contains schema information.
 */
public class ResponseWithSchema extends Response {

    private static final long serialVersionUID = -6225258723604108709L;

    private static final String REF = "$ref";

    private Map<String, String> schema;

    public ResponseWithSchema() {
        super(null);
    }

    /**
     * Constructs this response with a reference to the
     * @param description the human readable description of this response
     * @param ref the ref to the definition of the model of this response
     */
    public ResponseWithSchema(String description, String ref) {
        super(description);
        this.schema = new LinkedHashMap<>();
        schema.put(REF, ref);
    }

    /**
     * @return the schema of this response, this will generally contain the ref to the definition
     */
    public Map<String, String> getSchema() {
        return schema;
    }

    /**
     * @param schema the schema of this response, this will generally contain the ref to the definition
     */
    public void setSchema(Map<String, String> schema) {
        this.schema = schema;
    }
}
