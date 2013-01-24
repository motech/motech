package org.motechproject.couch.mrs.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.Provider;

@TypeDiscriminator("doc.type === 'Provider'")
public class CouchProvider extends MotechBaseDataObject implements Provider {

    private static final long serialVersionUID = 1L;

    private final String type = "Provider";

    @JsonProperty
    @JsonDeserialize(as=CouchPerson.class)
    private CouchPerson person;
    @JsonProperty
    private String providerId;

    CouchProvider() { }

    public CouchProvider(String providerId, Person person) {
        super();
        this.setType(type);
        this.providerId = providerId;
        this.person = (CouchPerson) person;
    }

    public Person getPerson() {
        return person;
    }

    @Override
    public void setPerson(Person person) {
        this.person = (CouchPerson) person;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
