package org.motechproject.openmrs.ws.impl;

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
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSEncounter.MRSEncounterBuilder;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.EncounterResource;
import org.motechproject.openmrs.ws.resource.model.Concept;
import org.motechproject.openmrs.ws.resource.model.Encounter;
import org.motechproject.openmrs.ws.resource.model.Encounter.EncounterType;
import org.motechproject.openmrs.ws.resource.model.EncounterListResult;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.Observation;
import org.motechproject.openmrs.ws.resource.model.Observation.ObservationValue;
import org.motechproject.openmrs.ws.resource.model.Patient;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("encounterAdapter")
public class MRSEncounterAdapterImpl implements MRSEncounterAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MRSEncounterAdapterImpl.class);

    private final MRSPatientAdapter patientAdapter;
    private final MRSPersonAdapterImpl personAdapter;
    private final MRSConceptAdapterImpl conceptAdapter;
    private final EncounterResource encounterResource;

    @Autowired
    public MRSEncounterAdapterImpl(EncounterResource encounterResource, MRSPatientAdapter patientAdapter,
            MRSPersonAdapterImpl personAdapter, MRSConceptAdapterImpl conceptAdapter) {
        this.encounterResource = encounterResource;
        this.patientAdapter = patientAdapter;
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
        MRSEncounter encounterCopy = new MRSEncounterBuilder().withId(encounter.getId())
                .withProvider(encounter.getProvider()).withCreator(encounter.getCreator())
                .withFacility(encounter.getFacility()).withDate(encounter.getDate())
                .withPatient(encounter.getPatient()).withObservations(updatedObs)
                .withEncounterType(encounter.getEncounterType()).build();

        Encounter converted = toEncounter(encounterCopy);
        Encounter saved = null;
        try {
            saved = encounterResource.createEncounter(converted);
        } catch (HttpException e) {
            LOGGER.error("Could not create encounter: " + e.getMessage());
            return null;
        }

        return new MRSEncounterBuilder().withId(saved.getUuid()).withProvider(encounter.getProvider())
                .withCreator(encounter.getCreator()).withFacility(encounter.getFacility())
                .withDate(encounter.getDate()).withPatient(encounter.getPatient())
                .withObservations(encounter.getObservations()).withEncounterType(encounter.getEncounterType()).build();
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
        encounterType.setName(encounter.getEncounterType());
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
        EncounterListResult result = null;
        try {
            result = encounterResource.queryForAllEncountersByPatientId(patient.getId());
        } catch (HttpException e) {
            LOGGER.error("Error retrieving encounters for patient: " + patient.getId());
            return Collections.emptyList();
        }

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
        MRSEncounter updated = new MRSEncounterBuilder().withId(encounter.getUuid()).withProvider(mrsPerson)
                .withFacility(ConverterUtils.convertLocationToMrsLocation(encounter.getLocation()))
                .withDate(encounter.getEncounterDatetime()).withPatient(patient)
                .withObservations(convertToMrsObservation(encounter.getObs()))
                .withEncounterType(encounter.getEncounterType().getName()).build();

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
