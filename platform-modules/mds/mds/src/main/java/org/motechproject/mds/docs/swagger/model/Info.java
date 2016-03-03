package org.motechproject.mds.docs.swagger.model;

import java.io.Serializable;

/**
 * Represents the info section of the Swagger JSON spec.
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

    /**
     * @return the version of the API, we use MOTECH's version for MDS
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version of the API, we use MOTECH's version for MDS
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the title displayed to the user
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title displayed to the user
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description of the API displayed to the user
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description of the API displayed to the user
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return terms of service, will be displayed to the user
     */
    public String getTermsOfService() {
        return termsOfService;
    }

    /**
     * @param termsOfService terms of service, will be displayed to the user
     */
    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }

    /**
     * @return the contact information for this API
     * @see org.motechproject.mds.docs.swagger.model.Contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * @param contact the contact information for this API
     * @see org.motechproject.mds.docs.swagger.model.Contact
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * @return the license for this API
     * @see org.motechproject.mds.docs.swagger.model.License
     */
    public License getLicense() {
        return license;
    }

    /**
     * @param license the license for this API
     * @see org.motechproject.mds.docs.swagger.model.License
     */
    public void setLicense(License license) {
        this.license = license;
    }
}
