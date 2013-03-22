package org.motechproject.couch.mrs.repository;

import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.MRSCouchException;

import java.util.List;

public interface AllCouchProviders {

    List<CouchProvider> findByProviderId(String providerId);

    void addProvider(CouchProvider provider) throws MRSCouchException;

    void update(CouchProvider person);

    void remove(CouchProvider person);

    List<CouchProvider> getAllProviders();
}
