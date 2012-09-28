package org.motechproject.openmrs.ws.resource;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.ConceptListResult;

public interface ConceptResource {

    ConceptListResult queryForConceptsByName(String name) throws HttpException;

}
