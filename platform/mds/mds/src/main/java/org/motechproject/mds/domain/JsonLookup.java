package org.motechproject.mds.domain;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import static org.motechproject.mds.util.Constants.Util.TRUE;

/**
 * Contains information about single lookup added via JSON file.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = TRUE)
public class JsonLookup {

    @PrimaryKey
    @Persistent
    private String originLookupName;

    @PrimaryKey
    @Persistent
    private String entityClassName;

    public String getOriginLookupName() {
        return originLookupName;
    }

    public void setOriginLookupName(String originLookupName) {
        this.originLookupName = originLookupName;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }
}
