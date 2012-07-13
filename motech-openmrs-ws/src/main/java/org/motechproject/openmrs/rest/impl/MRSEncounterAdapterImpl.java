package org.motechproject.openmrs.rest.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.model.Concept;
import org.motechproject.openmrs.rest.model.Concept.ConceptSerializer;
import org.motechproject.openmrs.rest.model.Encounter;
import org.motechproject.openmrs.rest.model.Encounter.EncounterType;
import org.motechproject.openmrs.rest.model.Encounter.EncounterTypeSerializer;
import org.motechproject.openmrs.rest.model.EncounterListResult;
import org.motechproject.openmrs.rest.model.Location;
import org.motechproject.openmrs.rest.model.Location.LocationSerializer;
import org.motechproject.openmrs.rest.model.Observation;
import org.motechproject.openmrs.rest.model.Observation.ObservationValue;
import org.motechproject.openmrs.rest.model.Observation.ObservationValueDeserializer;
import org.motechproject.openmrs.rest.model.Observation.ObservationValueSerializer;
import org.motechproject.openmrs.rest.model.Patient;
import org.motechproject.openmrs.rest.model.Patient.PatientSerializer;
import org.motechproject.openmrs.rest.model.Person;
import org.motechproject.openmrs.rest.model.Person.PersonSerializer;
import org.motechproject.openmrs.rest.util.ConverterUtils;
import org.motechproject.openmrs.rest.util.JsonUtils;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component("encounterAdapter")
public class MRSEncounterAdapterImpl implements MRSEncounterAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MRSEncounterAdapterImpl.class);

    private final MRSPatientAdapter patientAdapter;
    private final RestClient restfulClient;
    private final OpenMrsUrlHolder urlHolder;
    private final MRSPersonAdapterImpl personAdapter;
    private final MRSConceptAdapterImpl conceptAdapter;

    @Autowired
    public MRSEncounterAdapterImpl(RestClient restfulClient, MRSPatientAdapter patientAdapter,
            OpenMrsUrlHolder encounterUrl, MRSPersonAdapterImpl personAdapter, MRSConceptAdapterImpl conceptAdapter) {
        this.restfulClient = restfulClient;
        this.patientAdapter = patientAdapter;
        this.urlHolder = encounterUrl;
        this.personAdapter = personAdapter;
        this.conceptAdapter = conceptAdapter;
    }

    @Override
    public MRSEncounter createEncounter(MRSEncounter encounter) {
        validateEncounter(encounter);

        // OpenMRS expects the observations to reference a concept uuid rather
        // than just a concept name. Attempt to map all concept names to concept
        // uuid's for each of the observations
        Set<MRSObservation> updatedObs = resolveConceptUuidForConceptNames(encounter.getObservations());
        MRSEncounter encounterCopy = new MRSEncounter(encounter.getId(), encounter.getProvider(), encounter.getCreator(),
                encounter.getFacility(), encounter.getDate(), encounter.getPatient(), updatedObs,
                encounter.getEncounterType());

        Encounter converted = toEncounter(encounterCopy);

        Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationSerializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .registerTypeAdapter(Patient.class, new PatientSerializer())
                .registerTypeAdapter(Person.class, new PersonSerializer())
                .registerTypeAdapter(Concept.class, new ConceptSerializer())
                .registerTypeAdapter(EncounterType.class, new EncounterTypeSerializer())
                .registerTypeAdapter(ObservationValue.class, new ObservationValueSerializer()).create();

        String requestJson = gson.toJson(converted);

        try {
            String responseJson = restfulClient.postForJson(urlHolder.getEncounterPath(), requestJson);
            Encounter response = (Encounter) JsonUtils.readJson(responseJson, Encounter.class);
            return new MRSEncounter(response.getUuid(), encounter.getProvider(), encounter.getCreator(),
                    encounter.getFacility(), encounter.getDate(), encounter.getPatient(), encounter.getObservations(),
                    encounter.getEncounterType());
        } catch (HttpException e) {
            LOGGER.error("Could not create encounter: " + e.getMessage());
            throw new MRSException(e);
        }
    }

    private void validateEncounter(MRSEncounter encounter) {
        Validate.notNull(encounter, "Encounter cannot be null");
        Validate.notNull(encounter.getPatient(), "Patient cannot be null");
        Validate.notEmpty(encounter.getPatient().getId(), "Patient must have an id");
        Validate.notNull(encounter.getDate(), "Encounter Date cannot be null");
        Validate.notEmpty(encounter.getEncounterType(), "Encounter type cannot be empty");
    }

    private Encounter toEncounter(MRSEncounter encounter) {
        Encounter converted = new Encounter();
        converted.setEncounterDatetime(encounter.getDate());

        EncounterType encounterType = new EncounterType();
        encounterType.setDisplay(encounter.getEncounterType());
        converted.setEncounterType(encounterType);

        Location location = new Location();
        location.setUuid(encounter.getFacility().getId());
        converted.setLocation(location);

        Patient patient = new Patient();
        patient.setUuid(encounter.getPatient().getId());
        converted.setPatient(patient);

        Person person = new Person();
        person.setUuid(encounter.getProvider().getId());
        converted.setProvider(person);

        converted.setObs(convertToObservations(encounter.getObservations()));

        return converted;
    }

    private List<Observation> convertToObservations(Set<MRSObservation> observations) {
        List<Observation> obs = new ArrayList<Observation>();

        for (MRSObservation observation : observations) {
            Observation ob = new Observation();
            ob.setObsDatetime(observation.getDate());

            Concept concept = new Concept();
            concept.setDisplay(observation.getConceptName());
            ob.setConcept(concept);

            ObservationValue value = new ObservationValue();
            value.setDisplay(observation.getValue().toString());
            ob.setValue(value);

            if (CollectionUtils.isNotEmpty(observation.getDependantObservations())) {
                ob.setGroupsMembers(convertToObservations(observation.getDependantObservations()));
            }

            obs.add(ob);
        }

        return obs;
    }

    private Set<MRSObservation> resolveConceptUuidForConceptNames(Set<MRSObservation> originalObservations) {
        Set<MRSObservation> updatedObs = new HashSet<MRSObservation>();
        for (MRSObservation observation : originalObservations) {
            String conceptUuid = conceptAdapter.resolveConceptUuidFromConceptName(observation.getConceptName());
            if (CollectionUtils.isNotEmpty(observation.getDependantObservations())) {
                Set<MRSObservation> updatedDependent = resolveConceptUuidForConceptNames(observation
                        .getDependantObservations());
            }
            updatedObs.add(new MRSObservation(observation.getId(), observation.getDate(), conceptUuid, observation
                    .getValue()));
        }

        return updatedObs;
    }

    @Override
    public MRSEncounter getLatestEncounterByPatientMotechId(String motechId, String encounterType) {
        Validate.notEmpty(motechId, "MoTeCH Id cannot be empty");

        List<MRSEncounter> previousEncounters = getAllEncountersByPatientMotechId(motechId);
        Iterator<MRSEncounter> encounterItr = previousEncounters.iterator();

        // filter out encounters with non matching encounterType
        while (StringUtils.isNotBlank(encounterType) && encounterItr.hasNext()) {
            MRSEncounter enc = encounterItr.next();
            if (!encounterType.equals(enc.getEncounterType())) {
                encounterItr.remove();
            }
        }

        MRSEncounter latestEncounter = null;
        for (MRSEncounter enc : previousEncounters) {
            if (latestEncounter == null) {
                latestEncounter = enc;
            } else {
                latestEncounter = enc.getDate().after(latestEncounter.getDate()) ? enc : latestEncounter;
            }
        }

        return latestEncounter;
    }

    public List<MRSEncounter> getAllEncountersByPatientMotechId(String motechId) {
        Validate.notEmpty(motechId, "MoTeCH Id cannot be empty");

        List<MRSEncounter> encounters = new ArrayList<MRSEncounter>();
        MRSPatient patient = patientAdapter.getPatientByMotechId(motechId);

        if (patient != null) {
            encounters.addAll(getEncountersForPatient(patient));
        }

        return encounters;
    }

    private List<MRSEncounter> getEncountersForPatient(MRSPatient patient) {
        String responseJson = null;
        try {
            responseJson = restfulClient.getJson(urlHolder.getEncountersByPatientUuid(patient.getId()));
        } catch (HttpException e) {
            LOGGER.error("Error retrieving encounters for patient: " + patient.getMotechId());
            throw new MRSException(e);
        }
        Map<Type, Object> adapters = new HashMap<Type, Object>();
        adapters.put(ObservationValue.class, new ObservationValueDeserializer());
        EncounterListResult result = (EncounterListResult) JsonUtils.readJsonWithAdapters(responseJson,
                EncounterListResult.class, adapters);

        if (result.getResults().size() == 0) {
            return Collections.emptyList();
        }

        // the response JSON from the OpenMRS does not contain full information
        // for
        // the provider. therefore, separate request(s) must be made to obtain
        // full provider
        // information. As an optimization, only make 1 request per unique
        // provider
        Map<String, MRSPerson> providers = new HashMap<String, MRSPerson>();
        for (Encounter encounter : result.getResults()) {
            providers.put(encounter.getProvider().getUuid(), null);
        }

        for (String providerUuid : providers.keySet()) {
            MRSPerson provider = personAdapter.getPerson(providerUuid);
            providers.put(providerUuid, provider);
        }

        List<MRSEncounter> updatedEncounters = new ArrayList<MRSEncounter>();
        for (Encounter encounter : result.getResults()) {
            MRSEncounter mrsEncounter = convertToMrsEncounter(encounter,
                    providers.get(encounter.getProvider().getUuid()), patient);
            updatedEncounters.add(mrsEncounter);
        }

        return updatedEncounters;
    }

    private MRSEncounter convertToMrsEncounter(Encounter encounter, MRSPerson mrsPerson, MRSPatient patient) {
        MRSEncounter updated = new MRSEncounter(encounter.getUuid(), mrsPerson, null,
                ConverterUtils.convertLocationToMrsLocation(encounter.getLocation()), encounter.getEncounterDatetime(),
                patient, convertToMrsObservation(encounter.getObs()), encounter.getEncounterType().getDisplay());

        return updated;
    }

    private Set<MRSObservation> convertToMrsObservation(List<Observation> obs) {
        Set<MRSObservation> mrsObs = new HashSet<MRSObservation>();

        for (Observation ob : obs) {
            mrsObs.add(ConverterUtils.convertObservationToMrsObservation(ob));
        }

        return mrsObs;
    }
}
