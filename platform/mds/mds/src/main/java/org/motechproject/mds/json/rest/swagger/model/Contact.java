package org.motechproject.mds.json.rest.swagger.model;

import java.io.Serializable;

/**
 * Represents the contact section from the info section.
 */
public class Contact implements Serializable {
    private static final long serialVersionUID = -6411502629809830573L;

    private String name;
    private String email;
    private String url;

    public Contact() {
    }

    public Contact(String name, String email, String url) {
        this.name = name;
        this.email = email;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
