package org.motechproject.couch.mrs.repository.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.CouchProviderImpl;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AllCouchProvidersImpl extends MotechBaseRepository<CouchProviderImpl> implements AllCouchProviders {

    @Autowired
    protected AllCouchProvidersImpl(@Qualifier("couchProviderDatabaseConnector") CouchDbConnector db) {
        super(CouchProviderImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_providerId", map = "function(doc) { if (doc.type ==='Provider') { emit(doc.providerId, doc._id); }}")
    public List<CouchProviderImpl> findByProviderId(String providerId) {
        if (providerId == null) {
            return Collections.emptyList();
        }
        ViewQuery viewQuery = createQuery("by_providerId").key(providerId).includeDocs(true);
        return db.queryView(viewQuery, CouchProviderImpl.class);
    }

    @Override
    public void addProvider(CouchProviderImpl provider) {

        if (provider.getProviderId() == null) {
            throw new NullPointerException("Provider id cannot be null.");
        }

        List<CouchProviderImpl> providers = findByProviderId(provider.getProviderId());

        if (!providers.isEmpty()) {
            CouchProviderImpl couchProvider = providers.get(0);
            couchProvider.setPersonId(provider.getPersonId());
            update(couchProvider);
            return;
        }

        try {
            super.add(provider);
        } catch (IllegalArgumentException e) {
            throw new MRSCouchException(e.getMessage(), e);
        }
    }

    @Override
    public void update(CouchProviderImpl provider) {
        super.update(provider);
    }

    @Override
    public void remove(CouchProviderImpl provider) {
        super.remove(provider);
    }

    @Override
    public List<CouchProviderImpl> getAllProviders() {
        return this.getAll();
    }

}
