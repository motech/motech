package org.motechproject.openmrs.ws.impl;

import java.util.HashMap;
import java.util.Map;

import org.motechproject.mrs.exception.MRSException;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.ConceptResource;
import org.motechproject.openmrs.ws.resource.model.Concept;
import org.motechproject.openmrs.ws.resource.model.ConceptListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("conceptAdapter")
public class MRSConceptAdapterImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(MRSConceptAdapterImpl.class);

    private final Map<String, String> conceptCache = new HashMap<String, String>();
    private final ConceptResource conceptResource;

    @Autowired
    public MRSConceptAdapterImpl(ConceptResource conceptResource) {
        this.conceptResource = conceptResource;
    }

    public void clearCachedConcepts() {
        conceptCache.clear();
    }

    public String resolveConceptUuidFromConceptName(String conceptName) {
        if (conceptCache.containsKey(conceptName)) {
            return conceptCache.get(conceptName);
        }

        ConceptListResult results = null;
        try {
            results = conceptResource.queryForConceptsByName(conceptName);
        } catch (HttpException e) {
            LOGGER.error("There was an error retrieving the uuid of the concept with concept name: " + conceptName);
            throw new MRSException(e);
        }

        if (results.getResults().isEmpty()) {
            LOGGER.error("Could not find a concept with name: " + conceptName);
            throw new MRSException(new RuntimeException(
                    "Can't create an encounter because no concept was found with name: " + conceptName));
        }

        Concept concept = results.getResults().get(0);
        conceptCache.put(conceptName, concept.getUuid());
        return concept.getUuid();
    }
}
