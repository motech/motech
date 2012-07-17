package org.motechproject.openmrs.rest.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.model.Identifier;
import org.motechproject.openmrs.rest.model.IdentifierType;
import org.motechproject.openmrs.rest.model.IdentifierType.IdentifierTypeSerializer;
import org.motechproject.openmrs.rest.model.Location;
import org.motechproject.openmrs.rest.model.Location.LocationSerializer;
import org.motechproject.openmrs.rest.model.Patient;
import org.motechproject.openmrs.rest.model.PatientIdentifierListResult;
import org.motechproject.openmrs.rest.model.PatientListResult;
import org.motechproject.openmrs.rest.model.Person;
import org.motechproject.openmrs.rest.model.Person.PersonSerializer;
import org.motechproject.openmrs.rest.util.ConverterUtils;
import org.motechproject.openmrs.rest.util.JsonUtils;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component("patientAdapter")
public class MRSPatientAdapterImpl implements MRSPatientAdapter {

    private static Logger logger = LoggerFactory.getLogger(MRSPatientAdapterImpl.class);

    private String motechIdTypeUuid;

    private final RestClient restfulClient;
    private final OpenMrsUrlHolder urlHolder;
    private final MRSPersonAdapterImpl personAdapter;
    private final MRSFacilityAdapter facilityAdapter;
    private final String motechIdName;

    @Autowired
    public MRSPatientAdapterImpl(RestClient restfulClient, OpenMrsUrlHolder patientUrls,
            MRSPersonAdapterImpl personAdapter, MRSFacilityAdapter facilityAdapter,
            @Value("${openmrs.motechIdName}") String motechIdentifierName) {
        this.restfulClient = restfulClient;
        this.urlHolder = patientUrls;
        this.personAdapter = personAdapter;
        this.facilityAdapter = facilityAdapter;
        this.motechIdName = motechIdentifierName;
    }

    @Override
    public Integer getAgeOfPatientByMotechId(String arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MRSPatient getPatientByMotechId(String motechId) {
        Validate.notEmpty(motechId, "Motech Id cannot be empty");

        String responseJson = null;
        try {
            responseJson = restfulClient.getJson(urlHolder.getPatientSearchPathWithTerm(motechId));
        } catch (HttpException e) {
            logger.error("Failed search for patient by MoTeCH Id: " + motechId);
            throw new MRSException(e);
        }

        PatientListResult patientList = (PatientListResult) JsonUtils.readJson(responseJson, PatientListResult.class);
        if (patientList.getResults().size() == 0) {
            return null;
        } else if (patientList.getResults().size() > 1) {
            logger.warn("Search for patient by id returned more than 1 result");
        }

        return getPatient(patientList.getResults().get(0).getUuid());
    }

    @Override
    public MRSPatient getPatient(String patientId) {
        Validate.notEmpty(patientId, "Patient Id cannot be empty");

        String responseJson = null;
        try {
            responseJson = restfulClient.getJson(urlHolder.getFullPatientByUuid(patientId));
        } catch (HttpException e) {
            logger.error("Failed to get patient by id: " + patientId);
            throw new MRSException(e);
        }
        Patient patient = (Patient) JsonUtils.readJson(responseJson, Patient.class);
        Identifier identifier = patient.getIdentifierByIdentifierType(getMotechIdUuid());

        if (identifier == null) {
            logger.warn("No MoTeCH Id found on Patient with id: " + patient.getUuid());
        }

        String facililtyUuid = identifier.getLocation().getUuid();
        return convertToMrsPatient(patient, identifier, facilityAdapter.getFacility(facililtyUuid));
    }

    private MRSPatient convertToMrsPatient(Patient patient, Identifier identifier, MRSFacility facility) {
        MRSPatient converted = new MRSPatient(patient.getUuid(), identifier.getIdentifier(),
                ConverterUtils.convertToMrsPerson(patient.getPerson()), facility);
        return converted;
    }

    private String getMotechIdUuid() {
        if (StringUtils.isNotEmpty(motechIdTypeUuid)) {
            return motechIdTypeUuid;
        }

        String responseJson = null;
        try {
            responseJson = restfulClient.getJson(urlHolder.getPatientIdentifierTypeList());
        } catch (HttpException e) {
            logger.error("There was an exception retrieving the MoTeCH Identifier Type UUID");
            throw new MRSException(e);
        }
        PatientIdentifierListResult result = (PatientIdentifierListResult) JsonUtils.readJson(responseJson,
                PatientIdentifierListResult.class);

        for (IdentifierType type : result.getResults()) {
            if (motechIdName.equals(type.getName())) {
                motechIdTypeUuid = type.getUuid();
                break;
            }
        }

        if (StringUtils.isEmpty(motechIdTypeUuid)) {
            logger.error("Could not find OpenMRS patient identifier with name MoTeCH Id");
            throw new MRSException(
                    new RuntimeException("Could not find OpenMRS patient identifier with name MoTeCH Id"));
        }
        return motechIdTypeUuid;
    }

    @Override
    public MRSPatient savePatient(MRSPatient patient) {
        Validate.notNull(patient, "Patient cannot be null");
        Validate.isTrue(StringUtils.isNotEmpty(patient.getMotechId()), "You must provide a motech id to save a patient");

        MRSPerson savedPerson = personAdapter.savePerson(patient.getPerson());

        Patient converted = fromMrsPatient(patient, savedPerson);
        Gson gson = new GsonBuilder().registerTypeAdapter(Person.class, new PersonSerializer())
                .registerTypeAdapter(IdentifierType.class, new IdentifierTypeSerializer())
                .registerTypeAdapter(Location.class, new LocationSerializer()).create();

        String requestJson = gson.toJson(converted);
        String responseJson = null;
        try {
            responseJson = restfulClient.postForJson(urlHolder.getPatient(), requestJson);
        } catch (HttpException e) {
            logger.error("Failed to create a patient in OpenMRS with MoTeCH Id: " + patient.getMotechId());
            throw new MRSException(e);
        }

        Patient created = (Patient) JsonUtils.readJson(responseJson, Patient.class);

        return new MRSPatient(created.getUuid(), patient.getMotechId(), savedPerson, patient.getFacility());
    }

    private Patient fromMrsPatient(MRSPatient patient, MRSPerson savedPerson) {
        Patient converted = new Patient();
        Person person = new Person();
        person.setUuid(savedPerson.getId());
        converted.setPerson(person);

        Location location = new Location();
        location.setUuid(patient.getFacility().getId());

        IdentifierType type = new IdentifierType();
        type.setUuid(getMotechIdUuid());

        Identifier identifier = new Identifier();
        identifier.setIdentifier(patient.getMotechId());
        identifier.setLocation(location);
        identifier.setIdentifierType(type);

        List<Identifier> identifiers = new ArrayList<Identifier>();
        identifiers.add(identifier);
        converted.setIdentifiers(identifiers);

        return converted;
    }

    @Override
    public List<MRSPatient> search(String name, String id) {
        Validate.notEmpty(name, "Name cannot be empty");

        String responseJson = null;
        try {
            responseJson = restfulClient.getJson(urlHolder.getPatientSearchPathWithTerm(name));
        } catch (HttpException e) {
            logger.error("Failed search for patient with name: " + name + ", and id: " + id);
            throw new MRSException(e);
        }

        PatientListResult result = (PatientListResult) JsonUtils.readJson(responseJson, PatientListResult.class);
        List<MRSPatient> searchResults = new ArrayList<MRSPatient>();

        for (Patient partialPatient : result.getResults()) {
            MRSPatient patient = getPatient(partialPatient.getUuid());
            if (id == null) {
                searchResults.add(patient);
            } else {
                if (patient.getMotechId() != null && patient.getMotechId().contains(id)) {
                    searchResults.add(patient);
                }
            }
        }

        if (searchResults.size() > 0) {
            sortResults(searchResults);
        }

        return searchResults;
    }

    private void sortResults(List<MRSPatient> searchResults) {
        Collections.sort(searchResults, new Comparator<MRSPatient>() {
            @Override
            public int compare(MRSPatient patient1, MRSPatient patient2) {
                if (StringUtils.isNotEmpty(patient1.getMotechId()) && StringUtils.isNotEmpty(patient2.getMotechId())) {
                    return patient1.getMotechId().compareTo(patient2.getMotechId());
                } else if (StringUtils.isNotEmpty(patient1.getMotechId())) {
                    return -1;
                } else if (StringUtils.isNotEmpty(patient2.getMotechId())) {
                    return 1;
                }
                return 0;
            }
        });
    }

    @Override
    public MRSPatient updatePatient(MRSPatient patient) {
        Validate.notNull(patient, "Patient cannot be null");
        Validate.notEmpty(patient.getId(), "Patient Id may not be empty");

        MRSPerson person = patient.getPerson();

        personAdapter.updatePerson(person);
        // the openmrs web service requires an explicit delete request to remove
        // attributes. delete all previous attributes, and then
        // create any attributes attached to the patient
        personAdapter.deleteAllAttributes(person);
        personAdapter.saveAttributesForPerson(person);

        return patient;
    }

    @Override
    public void deceasePatient(String motechId, String conceptName, Date dateOfDeath, String comment)
            throws PatientNotFoundException {
        Validate.notEmpty(motechId, "MoTeCh id cannot be empty");

        MRSPatient patient = getPatientByMotechId(motechId);
        if (patient == null) {
            throw new MRSException(new RuntimeException("Cannot decease patient - No patient found with MoTeCH id: "
                    + motechId));
        }

        personAdapter.savePersonCauseOfDeath(patient.getId(), dateOfDeath, conceptName);
    }
}
