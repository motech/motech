package org.motechproject.openmrs.model;

import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSProvider;

public class OpenMRSProvider implements MRSProvider {

    private String providerId;
    private MRSPerson person;

    public OpenMRSProvider(MRSPerson person) {
        this.person = person;
    }

    public OpenMRSProvider() {
    }

    @Override
    public MRSPerson getPerson() {
        return person;
    }

    @Override
    public void setPerson(MRSPerson person) {
        this.person = person;
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
