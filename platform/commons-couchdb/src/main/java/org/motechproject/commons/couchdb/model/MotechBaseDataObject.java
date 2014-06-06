package org.motechproject.commons.couchdb.model;

import org.ektorp.support.CouchDbDocument;

/**
 * @deprecated As of release 0.24, MDS replaces CouchDB for persistence
 */
@Deprecated
public abstract class MotechBaseDataObject extends CouchDbDocument {

    private String type;

    protected MotechBaseDataObject() {
        this.type = this.getClass().getSimpleName();
    }

    protected MotechBaseDataObject(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }

    private static final long serialVersionUID = 1L;
}
