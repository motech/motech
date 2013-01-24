package org.motechproject.couch.mrs.repository.impl;

import java.util.Collections;
import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCouchProvidersImpl extends MotechBaseRepository<CouchProvider> implements AllCouchProviders {

    @Autowired
    protected AllCouchProvidersImpl(@Qualifier("couchProviderDatabaseConnector") CouchDbConnector db) {
        super(CouchProvider.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_providerId", map = "function(doc) { if (doc.type ==='Provider') { emit(doc.providerId, doc._id); }}")
    public List<CouchProvider> findByProviderId(String providerId) {
        if (providerId == null) {
            return Collections.emptyList();
        }
        ViewQuery viewQuery = createQuery("by_providerId").key(providerId).includeDocs(true);
        return db.queryView(viewQuery, CouchProvider.class);
    }

    @Override
    public void addProvider(CouchProvider provider) throws MRSCouchException {

        if (provider.getProviderId() == null) {
            throw new NullPointerException("Provider id cannot be null.");
        }

        List<CouchProvider> providers = findByProviderId(provider.getProviderId());

        if (!providers.isEmpty()) {
            CouchProvider couchProvider = providers.get(0);
            couchProvider.setPerson(provider.getPerson());
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
    public void update(CouchProvider provider) {
        super.update(provider);
    }

    @Override
    public void remove(CouchProvider provider) {
        this.remove(provider);
    }

    @Override
    public List<CouchProvider> getAllProviders() {
        return this.getAll();
    }

}
