package org.motechproject.couch.mrs.repository;

import java.util.List;
import org.motechproject.couch.mrs.model.CouchProviderImpl;

public interface AllCouchProviders {

    List<CouchProviderImpl> findByProviderId(String providerId);

    void addProvider(CouchProviderImpl provider);

    void update(CouchProviderImpl provider);

    void remove(CouchProviderImpl provider);

    List<CouchProviderImpl> getAllProviders();
}
