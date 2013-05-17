package org.motechproject.openmrs.ws.resource;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Concept;
import org.motechproject.openmrs.ws.resource.model.ConceptListResult;

public interface ConceptResource {

    ConceptListResult queryForConceptsByName(String name) throws HttpException;

    Concept getConceptById(String conceptId) throws HttpException;

    Concept createConcept(Concept concept) throws HttpException;

    ConceptListResult getAllConcepts() throws HttpException;

    void updateConcept(Concept concept) throws HttpException;

    void deleteConcept(String conceptId) throws HttpException;
}
