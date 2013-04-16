package org.motechproject.couch.mrs.repository;

import java.util.List;

import org.motechproject.couch.mrs.model.CouchEncounterImpl;

public interface AllCouchEncounters {

    CouchEncounterImpl findEncounterById(String encounterId);

    List<CouchEncounterImpl> findEncountersByMotechIdAndEncounterType(String motechId, String encounterType);

    void createOrUpdateEncounter(CouchEncounterImpl encounter);

}
