package org.motechproject.couch.mrs.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Provider'")
public class CouchProviderImpl extends MotechBaseDataObject {
    private static final long serialVersionUID = 1L;
    private final String type = "Provider";

    @JsonProperty
    private String personId;
    @JsonProperty
    private String providerId;


    public CouchProviderImpl() {
        super();
        this.setType(type);
    }

    public CouchProviderImpl(String providerId, String personId) {
        this();
        this.providerId = providerId;
        this.personId = personId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
