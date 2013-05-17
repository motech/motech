package org.motechproject.openmrs.ws.resource.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.OpenMrsInstance;
import org.motechproject.openmrs.ws.RestClient;
import org.motechproject.openmrs.ws.resource.ConceptResource;
import org.motechproject.openmrs.ws.resource.model.Concept;
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

    @Override
    public Concept getConceptById(String conceptId) throws HttpException {
        String responseJson = restClient.getJson(openmrsInstance.toInstancePathWithParams("/concept?q={conceptId}",conceptId));
        return (Concept) JsonUtils.readJson(responseJson,Concept.class);
    }

    @Override
    public Concept createConcept(Concept concept) throws HttpException {
        Gson gson = new GsonBuilder().create();

        String requestJson = gson.toJson(concept);
        String responseJson = restClient.postForJson(openmrsInstance.toInstancePath("/concept"), requestJson);

        return (Concept) JsonUtils.readJson(responseJson, Concept.class);
    }

    @Override
    public ConceptListResult getAllConcepts() throws HttpException {
        String json = restClient.getJson(openmrsInstance.toInstancePath("/concept?v=full"));
        return (ConceptListResult) JsonUtils.readJson(json, ConceptListResult.class);
    }

    @Override
    public void updateConcept(Concept concept) throws HttpException {
        Gson gson = new GsonBuilder().create();
        // uuid cannot be set on an update call
        String conceptUuid = concept.getUuid();
        concept.setUuid(null);
        String jsonRequest = gson.toJson(concept);
        restClient.postWithEmptyResponseBody(openmrsInstance.toInstancePathWithParams("/concept/{uuid}", conceptUuid),
                jsonRequest);
    }

    @Override
    public void deleteConcept(String conceptId) throws HttpException {
        restClient.delete(openmrsInstance.toInstancePathWithParams("/concept/{uuid}?purge", conceptId));
    }

}
