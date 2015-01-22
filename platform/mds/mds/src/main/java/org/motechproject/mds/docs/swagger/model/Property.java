package org.motechproject.mds.docs.swagger.model;

import java.io.Serializable;

/**
 * Created by pawel on 1/19/15.
 */
public class Property implements Serializable {

    private static final long serialVersionUID = -3578038867082692451L;

    private String type;
    private String format;
    private Property items;

    public Property() {
    }

    public Property(String type) {
        this.type = type;
    }

    public Property(String type, String format) {
        this.type = type;
        this.format = format;
    }

    public Property(String type, Property items) {
        this.type = type;
        this.items = items;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Property getItems() {
        return items;
    }

    public void setItems(Property items) {
        this.items = items;
    }
}
