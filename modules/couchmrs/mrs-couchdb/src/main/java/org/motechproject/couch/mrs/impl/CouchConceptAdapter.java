package org.motechproject.couch.mrs.impl;

import org.motechproject.couch.mrs.model.CouchConcept;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchConcepts;
import org.motechproject.couch.mrs.util.CouchMRSConverterUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSConcept;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.MRSConceptAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouchConceptAdapter implements MRSConceptAdapter {

    @Autowired
    private AllCouchConcepts allCouchConcepts;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public String resolveConceptUuidFromConceptName(String conceptName) {
        List<CouchConcept> result = allCouchConcepts.findByConceptName(conceptName);
        CouchConcept couchConcept = result.get(0);

        return couchConcept.getUuid();
    }

    @Override
    public MRSConcept saveConcept(MRSConcept concept) {
        CouchConcept couchConcept = (concept instanceof CouchConcept) ? (CouchConcept) concept :
                CouchMRSConverterUtil.convertConceptToCouchConcept(concept);

        try {
            allCouchConcepts.addConcept(couchConcept);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_CONCEPT_SUBJECT, EventHelper.conceptParameters(concept)));
        } catch (MRSCouchException e) {
            return null;
        }

        return concept;
    }

    @Override
    public MRSConcept getConcept(String conceptId) {
        List<CouchConcept> conceptList = allCouchConcepts.findByConceptId(conceptId);
        return conceptList.isEmpty() ? null : conceptList.get(0);
    }

    @Override
    public List<CouchConcept> search(String name) {
        return allCouchConcepts.findByConceptName(name);
    }

    public List<CouchConcept> getAllConcepts(){
        return allCouchConcepts.getAll();
    }

    @Override
    public void deleteConcept(String conceptId) {
        CouchConcept concept = (CouchConcept) getConcept(conceptId);
        if (concept != null) {
            allCouchConcepts.remove(concept);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_CONCEPT_SUBJECT, EventHelper.conceptParameters(concept)));
        }
    }

    @Override
    public MRSConcept updateConcept(MRSConcept concept) {
        CouchConcept conceptToUpdate = (CouchConcept) getConcept(concept.getId());
        if (conceptToUpdate != null) {
            conceptToUpdate.setName(concept.getName());
            allCouchConcepts.update(conceptToUpdate);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_CONCEPT_SUBJECT, EventHelper.conceptParameters(conceptToUpdate)));
            return getConcept(conceptToUpdate.getId());
        } else {
            return null;
        }
    }
}
