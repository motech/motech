package org.motechproject.mds.web.rest.docs.swagger.model;

import java.io.Serializable;

/**
 * Represents the license object from the info section of the swagger
 * JSON model.
 */
public class License implements Serializable {

    private static final long serialVersionUID = -1426854668940971720L;

    private String name;
    private String url;

    public License() {
    }

    public License(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
