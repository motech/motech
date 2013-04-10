package org.motechproject.couch.mrs.impl;

import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.CouchProviderImpl;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchPersons;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.motechproject.couch.mrs.util.CouchMRSConverterUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.MRSProviderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouchProviderAdapter implements MRSProviderAdapter {

    @Autowired
    private AllCouchProviders allCouchProviders;

    @Autowired
    private AllCouchPersons allCouchPersons;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public MRSProvider saveProvider(MRSProvider provider) {
        CouchPerson person = CouchMRSConverterUtil.convertPersonToCouchPerson(provider.getPerson());
        Boolean updated = (getProviderByProviderId(provider.getProviderId()) == null) ? false : true;

        try {
            allCouchPersons.addPerson(person);
            allCouchProviders.addProvider(new CouchProviderImpl(provider.getProviderId(), person.getPersonId()));
            if (!updated) {
                eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PROVIDER_SUBJECT, EventHelper.providerParameters(provider)));
            } else {
                eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_PROVIDER_SUBJECT, EventHelper.providerParameters(provider)));
            }
        } catch (MRSCouchException e) {
            return null;
        }

        return new CouchProvider(provider.getProviderId(), person);
    }

    @Override
    public MRSProvider getProviderByProviderId(String motechId) {
        List<CouchProviderImpl> providers = allCouchProviders.findByProviderId(motechId);

        if (providers != null && providers.size() > 0) {
            CouchPerson person = allCouchPersons.findByPersonId(providers.get(0).getPersonId()).get(0);
            return new CouchProvider(providers.get(0).getProviderId(), person);
        }

        return null;
    }

    @Override
    public void removeProvider(String motechId) {
        List<CouchProviderImpl> providerToRemove = allCouchProviders.findByProviderId(motechId);
        if (providerToRemove != null && providerToRemove.size() > 0) {
            CouchPerson personToRemove = allCouchPersons.findByPersonId(providerToRemove.get(0).getPersonId()).get(0);
            CouchProvider provider = new CouchProvider(providerToRemove.get(0).getProviderId(), personToRemove);
            allCouchPersons.remove(personToRemove);
            allCouchProviders.remove(providerToRemove.get(0));
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_PROVIDER_SUBJECT, EventHelper.providerParameters(provider)));
        }
    }
}
