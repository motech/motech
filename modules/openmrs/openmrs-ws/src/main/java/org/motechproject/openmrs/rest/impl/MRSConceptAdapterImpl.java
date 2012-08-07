package org.motechproject.openmrs.rest.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.motechproject.mrs.exception.MRSException;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.model.Concept;
import org.motechproject.openmrs.rest.model.ConceptListResult;
import org.motechproject.openmrs.rest.util.JsonUtils;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("conceptAdapter")
public class MRSConceptAdapterImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(MRSConceptAdapterImpl.class);

    private final RestClient restfulClient;
    private final OpenMrsUrlHolder urlHolder;
    private final Map<String, String> conceptCache = new HashMap<String, String>();

    @Autowired
    public MRSConceptAdapterImpl(RestClient restfulClient, OpenMrsUrlHolder urlHolder) {
        this.restfulClient = restfulClient;
        this.urlHolder = urlHolder;
    }

    public void clearCachedConcepts() {
        conceptCache.clear();
    }

    public String resolveConceptUuidFromConceptName(String conceptName) {
        if (conceptCache.containsKey(conceptName)) {
            return conceptCache.get(conceptName);
        }

        try {
            String encodedConceptName = URLEncoder.encode(conceptName, "UTF-8");
            String responseJson = restfulClient.getJson(urlHolder.getConceptSearchByName(encodedConceptName));

            ConceptListResult results = (ConceptListResult) JsonUtils.readJson(responseJson, ConceptListResult.class);

            if (results.getResults().isEmpty()) {
                LOGGER.error("Could not find a concept with name: " + conceptName);
                throw new MRSException(new RuntimeException(
                        "Can't create an encounter because no concept was found with name: " + conceptName));
            }

            Concept concept = results.getResults().get(0);
            conceptCache.put(conceptName, concept.getUuid());
            return concept.getUuid();
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Could not URL Encode the concept name: " + conceptName);
            throw new MRSException(e);
        } catch (HttpException e) {
            LOGGER.error("There was an error retrieving the uuid of the concept with concept name: " + conceptName);
            throw new MRSException(e);
        }
    }
}
