package org.motechproject.couch.mrs.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.repository.AllCouchEncounters;
import org.motechproject.couch.mrs.repository.AllCouchObservations;
import org.motechproject.couch.mrs.util.CouchDAOBroker;
import org.motechproject.couch.mrs.util.CouchMRSConverterUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouchEncounterAdapter implements MRSEncounterAdapter {

    @Autowired
    private CouchDAOBroker daoBroker;
    @Autowired
    private AllCouchEncounters allEncounters;
    @Autowired
    private AllCouchObservations allObservations;
    @Autowired
    private EventRelay eventRelay;


    @Override
    public MRSEncounter createEncounter(MRSEncounter mrsEncounter) {
        verifyObsIds(mrsEncounter.getObservations());
        allEncounters.createOrUpdateEncounter(CouchMRSConverterUtil.convertEncounterToEncounterImpl(mrsEncounter));

        Set<? extends MRSObservation> observations = mrsEncounter.getObservations();

        if (observations != null && observations.size() > 0) {
            Iterator<? extends MRSObservation> obsIterator = observations.iterator();
            while (obsIterator.hasNext()) {
                MRSObservation obs = obsIterator.next();
                allObservations.addOrUpdateObservation(obs);
            }
        }

        eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_ENCOUNTER_SUBJECT, EventHelper.encounterParameters(mrsEncounter)));
        return mrsEncounter;
    }

    private void verifyObsIds(Set<? extends MRSObservation> observations) {
        if (observations != null && observations.size() > 0) {
            Iterator<? extends MRSObservation> obsIterator = observations.iterator();
            while (obsIterator.hasNext()) {
                MRSObservation obs = obsIterator.next();
                if (obs.getObservationId() == null || obs.getObservationId().trim().length() == 0) {
                    obs.setObservationId(UUID.randomUUID().toString());
                }
            }
        }
    }

    @Override
    public MRSEncounter getLatestEncounterByPatientMotechId(String motechId, String encounterType) {
        throw new UnsupportedOperationException("Not yet implemented in CouchDB bundle");
    }

    @Override
    public MRSEncounter getEncounterById(String id) {
        CouchEncounterImpl encounter = allEncounters.findEncounterById(id);
        return (encounter == null) ? null : daoBroker.buildFullEncounter(encounter);
    }

    @Override
    public List<MRSEncounter> getEncountersByEncounterType(String motechId, String encounterType) {
        List<MRSEncounter> encounters = new ArrayList<MRSEncounter>();

        List<CouchEncounterImpl> couchEncounters = allEncounters.findEncountersByMotechIdAndEncounterType(motechId, encounterType);

        if (couchEncounters != null) {
            for (CouchEncounterImpl encounter : couchEncounters) {
                encounters.add(daoBroker.buildFullEncounter(encounter));
            }
        } else {
            return Collections.emptyList();
        }

        return encounters;
    }
}
