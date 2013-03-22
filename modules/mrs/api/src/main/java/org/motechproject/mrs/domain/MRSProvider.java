package org.motechproject.mrs.domain;

public interface MRSProvider {

    MRSPerson getPerson();

    void setPerson(MRSPerson person);

    String getProviderId();

    void setProviderId(String providerId);
}
