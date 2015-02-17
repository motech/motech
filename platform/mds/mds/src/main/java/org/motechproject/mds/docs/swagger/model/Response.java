package org.motechproject.mds.docs.swagger.model;

import java.io.Serializable;

/**
 * Represents a response returned by API endpoints. While the extensions of this class represent
 * responses that return items, this base class can be used for representing a simple response message
 * that has no model, i.e. 404 not found.
 */
public class Response implements Serializable {

    private static final long serialVersionUID = -7408353652042674710L;

    private String description;

    /**
     * @param description the human readable description of this response
     */
    public Response(String description) {
        this.description = description;
    }

    /**
     * @return the human readable description of this response
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the human readable description of this response
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
