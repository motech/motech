package org.motechproject.couch.mrs.repository.impl;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.repository.AllCouchEncounters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCouchEncountersImpl extends MotechBaseRepository<CouchEncounterImpl> implements AllCouchEncounters {

    @Autowired
    protected AllCouchEncountersImpl(@Qualifier("couchEncounterDatabaseConnector") CouchDbConnector db) {
        super(CouchEncounterImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_encounterId", map = "function(doc) { if (doc.type ==='Encounter') { emit(doc.encounterId, doc._id); }}")
    public CouchEncounterImpl findEncounterById(String encounterId) {

        if (encounterId == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("by_encounterId").key(encounterId).includeDocs(true);
        List<CouchEncounterImpl> encounters = db.queryView(viewQuery, CouchEncounterImpl.class);

        return (encounters == null || encounters.size() == 0) ? null : encounters.get(0);
    }

    @Override
    @View(name = "find_by_motech_id_and_encounter_type", map = "function(doc) {if(doc.type === 'Encounter') emit([doc.patientId, doc.encounterType]);}")
    public List<CouchEncounterImpl> findEncountersByMotechIdAndEncounterType(String motechId, String encounterType) {
        List<CouchEncounterImpl> encounters = queryView("find_by_motech_id_and_encounter_type", ComplexKey.of(motechId, encounterType));
        return encounters.isEmpty() ? null : encounters;
    }

    @Override
    public void createOrUpdateEncounter(CouchEncounterImpl encounter) {
        CouchEncounterImpl oldEncounter = findEncounterById(encounter.getEncounterId());

        if (oldEncounter == null) {
            this.add(encounter);
        } else {
            this.update(updateEncounter(oldEncounter, encounter));
        }

    }

    private CouchEncounterImpl updateEncounter(CouchEncounterImpl oldEncounter, CouchEncounterImpl newEncounter) {
        oldEncounter.setCreatorId(newEncounter.getCreatorId());
        oldEncounter.setDate(newEncounter.getDate());
        oldEncounter.setEncounterType(newEncounter.getEncounterType());
        oldEncounter.setFacilityId(newEncounter.getFacilityId());
        oldEncounter.setObservationIds(newEncounter.getObservationIds());
        oldEncounter.setPatientId(newEncounter.getPatientId());
        oldEncounter.setProviderId(newEncounter.getProviderId());

        return oldEncounter;
    }

}
