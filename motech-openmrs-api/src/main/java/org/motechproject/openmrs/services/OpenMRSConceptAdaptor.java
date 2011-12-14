package org.motechproject.openmrs.services;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenMRSConceptAdaptor {

    @Autowired
    ConceptService conceptService;

    public Concept getConceptByName(String conceptName) {
        return conceptService.getConcept(conceptName);
    }
}
