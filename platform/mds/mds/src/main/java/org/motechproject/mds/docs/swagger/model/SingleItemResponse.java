package org.motechproject.mds.docs.swagger.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by pawel on 16.02.15.
 */
public class SingleItemResponse extends Response {

    private static final long serialVersionUID = -6225258723604108709L;

    private static final String REF = "$ref";

    private Map<String, String> schema;

    public SingleItemResponse() {
        super(null);
    }

    public SingleItemResponse(String description, String ref) {
        super(description);
        this.schema = new LinkedHashMap<>();
        schema.put(REF, ref);
    }

    public Map<String, String> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, String> schema) {
        this.schema = schema;
    }
}
