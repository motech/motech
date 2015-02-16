package org.motechproject.mds.docs.swagger.model;

import java.io.Serializable;

/**
 * Created by pawel on 16.02.15.
 */
public class Response implements Serializable {

    private static final long serialVersionUID = -7408353652042674710L;

    private String description;

    public Response(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
