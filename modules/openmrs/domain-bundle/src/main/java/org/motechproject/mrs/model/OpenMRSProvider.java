package org.motechproject.mrs.model;

import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.Provider;

public class OpenMRSProvider implements Provider {

    private String providerId;
    private OpenMRSPerson person;

    public OpenMRSProvider(OpenMRSPerson person) {
        this.person = person;
    }

    public OpenMRSProvider() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public void setPerson(Person person) {
        this.person = (OpenMRSPerson) person;
    }

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

}
