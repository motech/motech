package org.motechproject.couch.mrs.repository.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.CouchObservationImpl;
import org.motechproject.couch.mrs.repository.AllCouchObservations;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.helper.EventHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCouchObservationsImpl extends MotechBaseRepository<CouchObservationImpl>  implements AllCouchObservations {

    @Autowired
    private EventRelay eventRelay;

    @Autowired
    protected AllCouchObservationsImpl(@Qualifier("couchObservationDatabaseConnector") CouchDbConnector db) {
        super(CouchObservationImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "find_by_concept_name_and_motech_id", map = "function(doc) {if(doc.type === 'Observation') emit([doc.patientId, doc.conceptName]);}")
    public List<CouchObservationImpl> findByMotechIdAndConceptName(String patientMotechId, String conceptName) {
        List<CouchObservationImpl> observations = queryView("find_by_concept_name_and_motech_id", ComplexKey.of(patientMotechId, conceptName));
        return observations.isEmpty() ? null : observations;
    }

    @Override
    @View(name = "by_observationId", map = "function(doc) { if (doc.type ==='Observation') { emit(doc.observationId, doc._id); }}")
    public List<CouchObservationImpl> findByObservationId(String observationId) {
        if (observationId == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_observationId").key(observationId).includeDocs(true);
        return db.queryView(viewQuery, CouchObservationImpl.class);
    }

    @Override
    public void addOrUpdateObservation(MRSObservation obs) {
        List<CouchObservationImpl> oldObservations = findByObservationId(obs.getObservationId());
        if (oldObservations != null && oldObservations.size() > 0) {
            this.update(updateOldObsWithNewObs(oldObservations.get(0), obs));
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_UPDATED_OBSERVATION_SUBJECT, EventHelper.observationParameters(obs)));
        } else {
            if (obs.getObservationId() == null || obs.getObservationId().trim().length() == 0) {
                obs.setObservationId(UUID.randomUUID().toString());
            }
            this.add(convertObsToImpl(obs));
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_OBSERVATION_SUBJECT, EventHelper.observationParameters(obs)));
        }
    }

    private CouchObservationImpl convertObsToImpl(MRSObservation obs) {
        return new CouchObservationImpl(obs.getObservationId(), obs.getDate(), obs.getConceptName(), obs.getPatientId(), obs.getValue(), convertToStringIds(obs.getDependantObservations()));
    }

    private Set<String> convertToStringIds(Set<MRSObservation> dependantObservations) {

        if (dependantObservations == null) {
            return null;
        }

        Iterator<MRSObservation> obsIterator = dependantObservations.iterator();
        Set<String> obsIds = new HashSet<String>();

        while (obsIterator.hasNext()) {
            MRSObservation obs = obsIterator.next();
            obsIds.add(obs.getObservationId());

        }

        return obsIds;
    }

    private CouchObservationImpl updateOldObsWithNewObs(CouchObservationImpl oldObs, MRSObservation newObs) {
        oldObs.setConceptName(newObs.getConceptName());
        oldObs.setDate(newObs.getDate());
        oldObs.setDependantObservationIds(convertToStringIds(newObs.getDependantObservations()));
        oldObs.setPatientId(newObs.getPatientId());
        oldObs.setValue(newObs.getValue());
        return oldObs;
    }

    @Override
    public void removeObservation(MRSObservation obs) {
        List<CouchObservationImpl> observations = findByObservationId(obs.getObservationId());
        if (observations != null && observations.size() > 0) {
            this.remove(observations.get(0));
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_OBSERVATION_SUBJECT, EventHelper.observationParameters(obs)));

        }
    }
}
