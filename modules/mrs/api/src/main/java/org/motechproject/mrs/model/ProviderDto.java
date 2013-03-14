package org.motechproject.mrs.model;

import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.Provider;

public class ProviderDto implements Provider {

    private String providerId;
    private Person person;

    public ProviderDto() {
    }

    public ProviderDto(String providerId, Person person) {
        this.providerId = providerId;
        this.person = person;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
