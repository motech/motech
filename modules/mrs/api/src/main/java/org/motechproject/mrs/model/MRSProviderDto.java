package org.motechproject.mrs.model;

import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSProvider;

public class MRSProviderDto implements MRSProvider {

    private String providerId;
    private MRSPerson person;

    public MRSProviderDto() {
    }

    public MRSProviderDto(String providerId, MRSPerson person) {
        this.providerId = providerId;
        this.person = person;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public MRSPerson getPerson() {
        return person;
    }

    public void setPerson(MRSPerson person) {
        this.person = person;
    }
}
