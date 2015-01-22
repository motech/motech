package org.motechproject.mds.docs.swagger.model;

import java.io.Serializable;

/**
 * Represents the info section of the Swagger JSON document.
 * This section contains human readable metadata describing the API.
 */
public class Info implements Serializable {

    private static final long serialVersionUID = 3506939688105102114L;

    private String version;
    private String title;
    private String description;
    private String termsOfService;
    private Contact contact;
    private License license;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsOfService() {
        return termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }
}
