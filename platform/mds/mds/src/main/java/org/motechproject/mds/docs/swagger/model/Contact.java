package org.motechproject.mds.docs.swagger.model;

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

    /**
     * @param name the name of the contact
     * @param email the email to contact
     * @param url the url for contact
     */
    public Contact(String name, String email, String url) {
        this.name = name;
        this.email = email;
        this.url = url;
    }

    /**
     * @return the name of the contact
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the contact
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the email to contact
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to contact
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the url for contact
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url for contact
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
