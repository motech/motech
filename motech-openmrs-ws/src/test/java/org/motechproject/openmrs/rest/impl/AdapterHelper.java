package org.motechproject.openmrs.rest.impl;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.model.Concept;
import org.motechproject.openmrs.rest.model.Concept.ConceptClass;
import org.motechproject.openmrs.rest.model.Concept.ConceptName;
import org.motechproject.openmrs.rest.model.Concept.DataType;
import org.motechproject.openmrs.rest.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Provides utility methods for deleting/creating OpenMRS entities that are shared across integration tests
 */
@Component
public class AdapterHelper {

    static final String CONCEPT_PATH = "/ws/rest/v1/concept";
    static final String TEST_CONCEPT_NAME = "Test Concept";

    @Autowired
    protected MRSPatientAdapter patientAdapter;

    @Autowired
    MRSPersonAdapterImpl personAdapter;

    @Autowired
    MRSFacilityAdapter facilityAdapter;

    @Autowired
    MRSUserAdapter userAdapter;

    @Autowired
    protected RestClient restfulClient;

    @Autowired
    protected RestOperations restOperations;

    @Value("${openmrs.url}")
    protected String openmrsUrl;

    protected void deleteFacility(MRSFacility facility) {
        restOperations.delete(openmrsUrl + "/ws/rest/v1/location/{uuid}?purge", facility.getId());
    }

    protected void deletePatient(MRSPatient patient) {
        restOperations.delete(openmrsUrl + "/ws/rest/v1/patient/{uuid}?purge", patient.getId());
    }

    protected MRSPatient createTemporaryPatient(String motechId, MRSPerson person, MRSFacility facility) {
        MRSPatient patient = new MRSPatient(motechId, person, facility);

        return patientAdapter.savePatient(patient);
    }

    protected MRSFacility createTemporaryLocation() throws HttpException, URISyntaxException {
        Location location = new Location();
        location.setName("Temporary Location");
        Gson gson = new Gson();
        String result = restfulClient.postForJson(new URI(openmrsUrl + "/ws/rest/v1/location"), gson.toJson(location));
        MotechJsonReader reader = new MotechJsonReader();
        Location createdLocation = (Location) reader.readFromString(result, Location.class);
        return new MRSFacility(createdLocation.getUuid());
    }

    public void deleteUser(MRSUser user) throws HttpException, URISyntaxException {
        if (user == null || user.getId() == null)
            return;
        restfulClient.delete(new URI(openmrsUrl + "/ws/rest/v1/user/" + user.getId() + "?purge"));
        restfulClient.delete(new URI(openmrsUrl + "/ws/rest/v1/person/" + user.getPerson().getId() + "?purge"));
    }

    public String createTemporaryConcept(String conceptName) throws URISyntaxException, HttpException {
        if (StringUtils.isEmpty(conceptName)) {
            conceptName = TEST_CONCEPT_NAME;
        }

        URI uri = new URI(openmrsUrl + CONCEPT_PATH);
        Concept concept = new Concept();

        ConceptName name = new ConceptName();
        name.setName(conceptName);
        concept.getNames().add(name);

        DataType type = new DataType();
        type.setDisplay("Text");
        concept.setDatatype(type);

        ConceptClass conceptClass = new ConceptClass();
        conceptClass.setDisplay("Test");
        concept.setConceptClass(conceptClass);

        String requestJson = toJson(concept);
        String responseJson = restfulClient.postForJson(uri, requestJson);
        MotechJsonReader reader = new MotechJsonReader();
        concept = (Concept) reader.readFromString(responseJson, Concept.class);

        return concept.getUuid();
    }

    private String toJson(Object object) {
        Gson gson = new GsonBuilder().registerTypeAdapter(DataType.class, new DataTypeSerializer())
                .registerTypeAdapter(ConceptClass.class, new ConceptClassSerializer()).create();
        String requestJson = gson.toJson(object);
        return requestJson;
    }

    static class DataTypeSerializer implements JsonSerializer<DataType> {
        @Override
        public JsonElement serialize(DataType src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getDisplay());
        }
    }

    static class ConceptClassSerializer implements JsonSerializer<ConceptClass> {
        @Override
        public JsonElement serialize(ConceptClass src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getDisplay());
        }
    }

    public MRSUser createTemporaryProvider() throws URISyntaxException, HttpException, UserAlreadyExistsException {
        MRSPerson person = new MRSPerson();
        person.dateOfBirth(new DateMidnight(1970, 1, 1).toDate());
        person.gender("M");
        person.firstName("Troy");
        person.lastName("Parks");

        MRSUser user = new MRSUser();
        user.userName("troy");
        user.securityRole("Provider");
        user.person(person);
        Map<String, Object> result = userAdapter.saveUser(user);

        return (MRSUser) result.get("mrsUser");
    }

    public void deleteEncounter(MRSEncounter persistedEncounter) throws HttpException, URISyntaxException {
        if (persistedEncounter == null)
            return;
        restfulClient.delete(new URI(openmrsUrl + "/ws/rest/v1/encounter/" + persistedEncounter.getId() + "?purge"));
    }

    public void deleteConcept(String tempConceptUuid) throws HttpException, URISyntaxException {
        if (tempConceptUuid == null)
            return;
        restfulClient.delete(new URI(openmrsUrl + CONCEPT_PATH + "/" + tempConceptUuid + "?purge"));
    }
}
