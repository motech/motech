package org.motechproject.openmrs.ws.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.model.OpenMRSEncounter;
import org.motechproject.openmrs.model.OpenMRSObservation;
import org.motechproject.openmrs.model.OpenMRSProvider;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("encounterAdapter")
public class MRSEncounterAdapterImpl implements MRSEncounterAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MRSEncounterAdapterImpl.class);

    private final MRSPatientAdapter patientAdapter;
    private final MRSPersonAdapterImpl personAdapter;
    private final MRSConceptAdapterImpl conceptAdapter;
    private final EncounterResource encounterResource;
    private final EventRelay eventRelay;

    @Autowired
    public MRSEncounterAdapterImpl(EncounterResource encounterResource, MRSPatientAdapter patientAdapter,
            MRSPersonAdapterImpl personAdapter, MRSConceptAdapterImpl conceptAdapter, EventRelay eventRelay) {
        this.encounterResource = encounterResource;
        this.patientAdapter = patientAdapter;
        this.personAdapter = personAdapter;
        this.conceptAdapter = conceptAdapter;
        this.eventRelay = eventRelay;
    }

    @Override
    public MRSEncounter createEncounter(MRSEncounter encounter) {
        validateEncounter(encounter);

        // OpenMRS expects the observations to reference a concept uuid rather
        // than just a concept name. Attempt to map all concept names to concept
        // uuid's for each of the observations
        Set<? extends MRSObservation> updatedObs = resolveConceptUuidForConceptNames(encounter.getObservations());
        MRSEncounter encounterCopy = new OpenMRSEncounter.MRSEncounterBuilder().withId(encounter.getEncounterId())
                .withProvider(encounter.getProvider()).withCreator(encounter.getCreator())
                .withFacility(encounter.getFacility()).withDate(encounter.getDate().toDate())
                .withPatient(encounter.getPatient()).withObservations(updatedObs)
                .withEncounterType(encounter.getEncounterType()).build();

        Encounter converted = toEncounter(encounterCopy);
        Encounter saved = null;
        try {
            saved = encounterResource.createEncounter(converted);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_ENCOUNTER_SUBJECT, EventHelper.encounterParameters(encounter)));
        } catch (HttpException e) {
            LOGGER.error("Could not create encounter: " + e.getMessage());
            return null;
        }

        return new OpenMRSEncounter.MRSEncounterBuilder().withId(saved.getUuid()).withProvider(encounter.getProvider())
                .withCreator(encounter.getCreator()).withFacility(encounter.getFacility())
                .withDate(encounter.getDate().toDate()).withPatient(encounter.getPatient())
                .withObservations(encounter.getObservations()).withEncounterType(encounter.getEncounterType()).build();
    }

    private void validateEncounter(MRSEncounter encounter) {
        Validate.notNull(encounter, "Encounter cannot be null");
        Validate.notNull(encounter.getPatient(), "Patient cannot be null");
        Validate.notEmpty(encounter.getPatient().getPatientId(), "Patient must have an id");
        Validate.notNull(encounter.getDate(), "Encounter Date cannot be null");
        Validate.notEmpty(encounter.getEncounterType(), "Encounter type cannot be empty");
    }

    private Encounter toEncounter(MRSEncounter encounter) {
        Encounter converted = new Encounter();
        converted.setEncounterDatetime(encounter.getDate().toDate());

        EncounterType encounterType = new EncounterType();
        encounterType.setName(encounter.getEncounterType());
        converted.setEncounterType(encounterType);

        Location location = new Location();
        location.setUuid(encounter.getFacility().getFacilityId());
        converted.setLocation(location);

        Patient patient = new Patient();
        patient.setUuid(encounter.getPatient().getPatientId());
        converted.setPatient(patient);

        Person person = new Person();
        person.setUuid(encounter.getProvider().getProviderId());
        converted.setProvider(person);

        converted.setObs(convertToObservations(encounter.getObservations()));

        return converted;
    }

    private List<Observation> convertToObservations(Set<? extends MRSObservation> observations) {
        List<Observation> obs = new ArrayList<>();

        for (MRSObservation observation : observations) {
            Observation ob = new Observation();
            ob.setObsDatetime(observation.getDate().toDate());

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

    private Set<? extends MRSObservation> resolveConceptUuidForConceptNames(Set<? extends MRSObservation> originalObservations) {
        Set<OpenMRSObservation> updatedObs = new HashSet<>();
        for (MRSObservation observation : originalObservations) {
            String conceptUuid = conceptAdapter.resolveConceptUuidFromConceptName(observation.getConceptName());
            if (CollectionUtils.isNotEmpty(observation.getDependantObservations())) {
                resolveConceptUuidForConceptNames(observation.getDependantObservations());
            }
            updatedObs.add(new OpenMRSObservation(observation.getObservationId(), observation.getDate().toDate(), conceptUuid, observation
                    .getValue()));
        }

        return updatedObs;
    }

    @Override
    public MRSEncounter getLatestEncounterByPatientMotechId(String motechId, String encounterType) {
        Validate.notEmpty(motechId, "MoTeCH Id cannot be empty");

        List<MRSEncounter> previousEncounters = getAllEncountersByPatientMotechId(motechId);

        removeEncounters(previousEncounters, encounterType);

        MRSEncounter latestEncounter = null;
        for (MRSEncounter enc : previousEncounters) {
            if (latestEncounter == null) {
                latestEncounter = enc;
            } else {
                latestEncounter = enc.getDate().isAfter(latestEncounter.getDate()) ? enc : latestEncounter;
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
        EncounterListResult result;
        try {
            result = encounterResource.queryForAllEncountersByPatientId(patient.getPatientId());
        } catch (HttpException e) {
            LOGGER.error("Error retrieving encounters for patient: " + patient.getPatientId());
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
        Map<String, MRSPerson> providers = new HashMap<>();
        for (Encounter encounter : result.getResults()) {
            providers.put(encounter.getProvider().getUuid(), null);
        }

        for (String providerUuid : providers.keySet()) {
            MRSPerson provider = personAdapter.getPerson(providerUuid);
            providers.put(providerUuid, provider);
        }

        List<MRSEncounter> updatedEncounters = new ArrayList<>();
        for (Encounter encounter : result.getResults()) {
            MRSPerson person = providers.get(encounter.getProvider().getUuid());
            OpenMRSProvider provider = new OpenMRSProvider(person);
            provider.setProviderId(person.getPersonId());
            MRSEncounter mrsEncounter = convertToMrsEncounter(encounter,
                    provider, patient);
            updatedEncounters.add(mrsEncounter);
        }

        return updatedEncounters;
    }

    private MRSEncounter convertToMrsEncounter(Encounter encounter, MRSProvider mrsPerson, MRSPatient patient) {

        return new OpenMRSEncounter.MRSEncounterBuilder().withId(encounter.getUuid()).withProvider(mrsPerson)
                .withFacility(ConverterUtils.convertLocationToMrsLocation(encounter.getLocation()))
                .withDate(encounter.getEncounterDatetime()).withPatient(patient)
                .withObservations(convertToMrsObservation(encounter.getObs()))
                .withEncounterType(encounter.getEncounterType().getName()).build();
    }

    private Set<? extends MRSObservation> convertToMrsObservation(List<Observation> obs) {
        Set<MRSObservation> mrsObs = new HashSet<>();

        for (Observation ob : obs) {
            mrsObs.add(ConverterUtils.convertObservationToMrsObservation(ob));
        }

        return mrsObs;
    }

    @Override
    public MRSEncounter getEncounterById(String id) {
        try {
            Encounter encounter = encounterResource.getEncounterById(id);
            MRSPatient patient = patientAdapter.getPatient(encounter.getPatient().getUuid());
            MRSPerson person = personAdapter.getPerson(encounter.getProvider().getUuid());
            MRSProvider provider = new org.motechproject.openmrs.model.OpenMRSProvider(person);
            provider.setProviderId(person.getPersonId());
            return convertToMrsEncounter(encounter, provider, patient);
        } catch (HttpException e) {
            return null;
        }
    }

    @Override
    public List<MRSEncounter> getEncountersByEncounterType(String motechId, String encounterType) {
        Validate.notEmpty(motechId, "MoTeCH Id cannot be empty");

        List<MRSEncounter> previousEncounters = getAllEncountersByPatientMotechId(motechId);

        removeEncounters(previousEncounters, encounterType);

        return previousEncounters;
    }

    private void removeEncounters(List<MRSEncounter> previousEncounters, String encounterType) {

        Iterator<MRSEncounter> encounterItr = previousEncounters.iterator();

        // filter out encounters with non matching encounterType
        while (StringUtils.isNotBlank(encounterType) && encounterItr.hasNext()) {
            MRSEncounter enc = encounterItr.next();
            if (!encounterType.equals(enc.getEncounterType())) {
                encounterItr.remove();
            }
        }
    }


}
