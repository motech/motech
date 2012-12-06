package org.motechproject.security.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.security.domain.OpenIdProvider;
import org.motechproject.security.domain.OpenIdProviderCouchDbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

public class AllOpenIdProvidersCouchdbImpl extends MotechBaseRepository<OpenIdProviderCouchDbImpl> implements AllOpenIdProviders {

    @Autowired
    protected AllOpenIdProvidersCouchdbImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(OpenIdProviderCouchDbImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_name", map = "function(doc) { if (doc.type ==='OpenIdProvider') { emit(doc.providerName, doc._id); }}")
    public OpenIdProvider findByName(String name) {
        if (name == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("by_name").key(name).includeDocs(true);
        return singleResult(db.queryView(viewQuery, OpenIdProviderCouchDbImpl.class));
    }

    @Override
    @View(name = "by_url", map = "function(doc) { if (doc.type ==='OpenIdProvider') { emit(doc.providerUrl, doc._id); }}")
    public OpenIdProvider findByUrl(String url) {
        if (url == null) {
            return  null;
        }

        ViewQuery viewQuery = createQuery("by_url").key(url).includeDocs(true);
        return singleResult(db.queryView(viewQuery, OpenIdProviderCouchDbImpl.class));
    }

    @Override
    public List<OpenIdProvider> getProviders() {
        return new ArrayList<OpenIdProvider>(getAll());
    }

    @Override
    public void add(OpenIdProvider provider) {
        if (findByName(provider.getProviderName()) != null) {
            return;
        }

        super.add((OpenIdProviderCouchDbImpl) provider);
    }

    @Override
    public void update(OpenIdProvider provider) {
        super.update((OpenIdProviderCouchDbImpl) provider);
    }

    @Override
    public void remove(OpenIdProvider provider) {
        super.update((OpenIdProviderCouchDbImpl) provider);
    }
}
