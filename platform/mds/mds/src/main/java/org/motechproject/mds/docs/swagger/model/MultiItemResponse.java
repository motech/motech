package org.motechproject.mds.docs.swagger.model;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.motechproject.mds.docs.swagger.SwaggerConstants.REF;

/**
 * Created by pawel on 1/19/15.
 */
public class MultiItemResponse extends Response {

    private static final long serialVersionUID = -3218389365227942942L;

    private Schema schema;

    public MultiItemResponse() {
        super(null);
    }

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

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }
}
