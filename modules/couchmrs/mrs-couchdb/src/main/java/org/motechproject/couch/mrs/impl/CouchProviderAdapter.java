package org.motechproject.couch.mrs.impl;

import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.Provider;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.ProviderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouchProviderAdapter implements ProviderAdapter {

    @Autowired
    private AllCouchProviders allCouchProviders;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public Provider saveProvider(Provider provider) {

        CouchProvider couchProvider = new CouchProvider(provider.getProviderId(), provider.getPerson());

        try {
            allCouchProviders.addProvider(couchProvider);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PROVIDER_SUBJECT, EventHelper.providerParameters(provider)));
        } catch (MRSCouchException e) {
            return null;
        }

        return couchProvider;
    }

    @Override
    public Provider getProviderByProviderId(String motechId) {
        List<CouchProvider> providers = allCouchProviders.findByProviderId(motechId);

        if (providers != null && providers.size() > 0) {
            return providers.get(0);
        }

        return null;
    }

}
