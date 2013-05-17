/**
 * \defgroup openMRS Open MRS
 */
/**
 * \ingroup openMRS
 * Services offered by Open MRS
 */
package org.motechproject.openmrs.services;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSConcept;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.MRSConceptAdapter;
import org.motechproject.openmrs.model.OpenMRSConcept;
import org.motechproject.openmrs.model.OpenMRSConceptName;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Adapter class to get Concepts in OpenMRS
 */
@Service
public class OpenMRSConceptAdapter implements MRSConceptAdapter{

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private EventRelay eventRelay;

    public Concept getConceptByName(String conceptName) {

        return conceptService.getConcept(conceptName);
    }

    @Override
    public String resolveConceptUuidFromConceptName(String conceptName) {
        List<Concept> conceptList = conceptService.getConceptsByName(conceptName);
        if (CollectionUtils.isNotEmpty(conceptList)) {
            return conceptList.get(0).getUuid();
        }
        return null;
    }

    @Override
    public MRSConcept saveConcept(MRSConcept mrsConcept) {
        Concept concept = new Concept();
        List<Concept> conceptList = conceptService.getConceptsByName(mrsConcept.getName().getName());
        if (CollectionUtils.isNotEmpty(conceptList)) {
            concept = conceptList.get(0);
        } else{
            ConceptName conceptName = new ConceptName();
            conceptName.setName(mrsConcept.getName().getName());
            concept.addName(conceptName);
        }
        conceptService.saveConcept(concept);
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_CONCEPT_SUBJECT, EventHelper.conceptParameters(mrsConcept)));
        return getMrsConcept(concept);
    }

    @Override
    public MRSConcept getConcept(String conceptId) {
        if(conceptId == null) {
            return null;
        }
        Concept openMrsConcept = getOpenMrsConcept (conceptId);
        return (openMrsConcept == null) ? null : getMrsConcept(openMrsConcept);
    }

    private MRSConcept getMrsConcept(Concept concept) {
        ConceptName conceptName = concept.getNames().iterator().next();

        return new OpenMRSConcept(new OpenMRSConceptName(conceptName.getName()));
    }

    private Concept getOpenMrsConcept(String conceptId) {
        return conceptService.getConcept(Integer.parseInt(conceptId));
    }

    @Override
    public List<? extends MRSConcept> search(String name) {
        final List<Concept> concepts = conceptService.getConceptsByName(name);
        final ArrayList<MRSConcept> mrsConcepts = new ArrayList<>();
        for (Concept concept : concepts) {
            mrsConcepts.add(new OpenMRSConcept(new OpenMRSConceptName(concept.getName().getName())));
        }
        return mrsConcepts;
    }

    @Override
    public List<? extends MRSConcept> getAllConcepts() {
        List<Concept> allConcepts = conceptService.getAllConcepts();
        List<MRSConcept> concepts = new ArrayList<>();
        for (Concept concept : allConcepts) {
            try {
                ConceptName conceptName = concept.getNames().iterator().next();
                concepts.add(new OpenMRSConcept(new OpenMRSConceptName(conceptName.getName())));
            } catch (NoSuchElementException x) {
                return null;
            }
        }
        return concepts;
    }

    @Override
    public void deleteConcept(String conceptId) {
        Concept existingOpenMrsConcept = conceptService.getConcept(Integer.parseInt(conceptId));
        if (existingOpenMrsConcept != null) {
            conceptService.purgeConcept(existingOpenMrsConcept);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_CONCEPT_SUBJECT, EventHelper.conceptParameters(new OpenMRSConcept(new OpenMRSConceptName(existingOpenMrsConcept.getName().getName())))));
        }
    }

    @Override
    public MRSConcept updateConcept(MRSConcept concept) {
        return saveConcept(concept);
    }
}
