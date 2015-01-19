package org.motechproject.mds.json.rest.swagger.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by pawel on 1/19/15.
 */
public class Parameter implements Serializable {

    private static final long serialVersionUID = 2770559337188359837L;

    private String name;
    private String in;
    private String description;
    private boolean required;
    private String type;
    private String format;
    private Map<String, String> items;
    private String collectionFormat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }

    public String getCollectionFormat() {
        return collectionFormat;
    }

    public void setCollectionFormat(String collectionFormat) {
        this.collectionFormat = collectionFormat;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
