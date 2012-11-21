package org.motechproject.openmrs.atomfeed.repository;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

/**
 * Represents a time when the OpenMRS Atom Feed was last updated. There should
 * only ever be one instance saved in the database at a time. The last update is
 * represented as a time, and an id of the last processed entity. In theory,
 * it's possible 2 entities can be updated at the same time, but with different
 * ids.
 */
@TypeDiscriminator("doc.type === 'AtomFeedUpdate'")
public class AtomFeedUpdate extends MotechBaseDataObject {

    private static final long serialVersionUID = -3867362351258037767L;

    @JsonProperty
    private String lastUpdateTime;

    @JsonProperty
    private String lastId;

    public AtomFeedUpdate() {
    }

    public AtomFeedUpdate(String lastUpdateTime, String lastId) {
        this.lastUpdateTime = lastUpdateTime;
        this.lastId = lastId;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }
}
