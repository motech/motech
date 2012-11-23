package org.motechproject.openmrs.ws.resource.impl;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.OpenMrsInstance;
import org.motechproject.openmrs.ws.RestClient;
import org.motechproject.openmrs.ws.resource.ConceptResource;
import org.motechproject.openmrs.ws.resource.model.ConceptListResult;
import org.motechproject.openmrs.ws.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConceptResourceImpl implements ConceptResource {

    private final OpenMrsInstance openmrsInstance;
    private final RestClient restClient;

    @Autowired
    public ConceptResourceImpl(RestClient restClient, OpenMrsInstance openmrsInstance) {
        this.restClient = restClient;
        this.openmrsInstance = openmrsInstance;
    }

    @Override
    public ConceptListResult queryForConceptsByName(String name) throws HttpException {
        String responseJson = restClient.getJson(openmrsInstance.toInstancePathWithParams("/concept?q={conceptName}",
                name));
        return (ConceptListResult) JsonUtils.readJson(responseJson, ConceptListResult.class);
    }

}
