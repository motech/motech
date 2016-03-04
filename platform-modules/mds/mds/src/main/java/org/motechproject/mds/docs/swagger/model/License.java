package org.motechproject.mds.docs.swagger.model;

import java.io.Serializable;

/**
 * Represents the license object from the info section of the swagger
 * JSON model. This is the general information about our license.
 * An url to the license is displayed in the Swagger API.
 */
public class License implements Serializable {

    private static final long serialVersionUID = -1426854668940971720L;

    private String name;
    private String url;

    public License() {
    }

    /**
     * @param name the name of the license
     * @param url the url to where the license can be read
     */
    public License(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * @return the name of the license
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the license
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the url to where the license can be read
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to where the license can be read
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
