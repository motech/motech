package org.motechproject.openmrs.ws.resource.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.OpenMrsInstance;
import org.motechproject.openmrs.ws.RestClient;
import org.motechproject.openmrs.ws.resource.EncounterResource;
import org.motechproject.openmrs.ws.resource.model.Concept;
import org.motechproject.openmrs.ws.resource.model.Concept.ConceptSerializer;
import org.motechproject.openmrs.ws.resource.model.Encounter;
import org.motechproject.openmrs.ws.resource.model.Encounter.EncounterType;
import org.motechproject.openmrs.ws.resource.model.Encounter.EncounterTypeSerializer;
import org.motechproject.openmrs.ws.resource.model.EncounterListResult;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.Location.LocationSerializer;
import org.motechproject.openmrs.ws.resource.model.Observation.ObservationValue;
import org.motechproject.openmrs.ws.resource.model.Observation.ObservationValueDeserializer;
import org.motechproject.openmrs.ws.resource.model.Observation.ObservationValueSerializer;
import org.motechproject.openmrs.ws.resource.model.Patient;
import org.motechproject.openmrs.ws.resource.model.Patient.PatientSerializer;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.resource.model.Person.PersonSerializer;
import org.motechproject.openmrs.ws.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class EncounterResourceImpl implements EncounterResource {

    private final RestClient restClient;
    private final OpenMrsInstance openmrsInstance;

    @Autowired
    public EncounterResourceImpl(RestClient restClient, OpenMrsInstance openmrsInstance) {
        this.restClient = restClient;
        this.openmrsInstance = openmrsInstance;
    }

    @Override
    public Encounter createEncounter(Encounter encounter) throws HttpException {
        Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationSerializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .registerTypeAdapter(Patient.class, new PatientSerializer())
                .registerTypeAdapter(Person.class, new PersonSerializer())
                .registerTypeAdapter(Concept.class, new ConceptSerializer())
                .registerTypeAdapter(EncounterType.class, new EncounterTypeSerializer())
                .registerTypeAdapter(ObservationValue.class, new ObservationValueSerializer()).create();

        String requestJson = gson.toJson(encounter);

        String responseJson = restClient.postForJson(openmrsInstance.toInstancePath("/encounter"), requestJson);
        return (Encounter) JsonUtils.readJson(responseJson, Encounter.class);
    }

    @Override
    public EncounterListResult queryForAllEncountersByPatientId(String id) throws HttpException {
        String responseJson = restClient.getJson(openmrsInstance.toInstancePathWithParams(
                "/encounter?patient={id}&v=full", id));

        Map<Type, Object> adapters = new HashMap<Type, Object>();
        adapters.put(ObservationValue.class, new ObservationValueDeserializer());
        EncounterListResult result = (EncounterListResult) JsonUtils.readJsonWithAdapters(responseJson,
                EncounterListResult.class, adapters);

        return result;
    }

    @Override
    public Encounter getEncounterById(String uuid) throws HttpException {
        String responseJson = restClient.getJson(openmrsInstance.toInstancePathWithParams("/encounter/{uuid}?v=full",
                uuid));
        Map<Type, Object> adapters = new HashMap<Type, Object>();
        adapters.put(ObservationValue.class, new ObservationValueDeserializer());
        return (Encounter) JsonUtils.readJsonWithAdapters(responseJson, Encounter.class, adapters);
    }

}
