package org.motechproject.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.CouchDbDocument;

public abstract class MotechBaseDataObject extends CouchDbDocument {
    @JsonProperty
    protected String type;

    protected MotechBaseDataObject() {
        this.type = this.getClass().getSimpleName();
    }

    protected MotechBaseDataObject(String type) {
        this.type = type;
    }

    private static final long serialVersionUID = 1L;

}
