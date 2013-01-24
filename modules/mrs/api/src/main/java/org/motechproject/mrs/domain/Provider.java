package org.motechproject.mrs.domain;

public interface Provider {

    Person getPerson();

    void setPerson(Person person);

    String getProviderId();

    void setProviderId(String providerId);
}
