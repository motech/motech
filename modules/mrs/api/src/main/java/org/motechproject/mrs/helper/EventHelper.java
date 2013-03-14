package org.motechproject.mrs.helper;

import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.Provider;

import java.util.HashMap;
import java.util.Map;

public final class EventHelper {

    private EventHelper() {
    }

    public static Map<String, Object> patientParameters(Patient patient) {
        Map<String, Object> patientParameters = new HashMap<>();
        patientParameters.put(EventKeys.PATIENT_ID, patient.getPatientId());
        if (patient.getFacility() != null) {
            patientParameters.put(EventKeys.FACILITY_ID, patient.getFacility().getFacilityId());
        } else {
            patientParameters.put(EventKeys.FACILITY_ID, null);
        }
        patientParameters.put(EventKeys.MOTECH_ID, patient.getMotechId());
        if (patient.getPerson() != null) {
            patientParameters.put(EventKeys.PERSON_ID, patient.getPerson().getPersonId());
        } else {
            patientParameters.put(EventKeys.PERSON_ID, null);
        }
        return patientParameters;
    }

    public static Map<String, Object> personParameters(Person person) {
        Map<String, Object> personParameters = new HashMap<>();
        personParameters.put(EventKeys.PERSON_ID, person.getPersonId());
        personParameters.put(EventKeys.PERSON_FIRST_NAME, person.getFirstName());
        personParameters.put(EventKeys.PERSON_MIDDLE_NAME, person.getMiddleName());
        personParameters.put(EventKeys.PERSON_LAST_NAME, person.getLastName());
        personParameters.put(EventKeys.PERSON_PREFERRED_NAME, person.getPreferredName());
        personParameters.put(EventKeys.PERSON_ADDRESS, person.getAddress());
        personParameters.put(EventKeys.PERSON_DATE_OF_BIRTH, person.getDateOfBirth());
        personParameters.put(EventKeys.PERSON_BIRTH_DATE_ESTIMATED, person.getBirthDateEstimated());
        personParameters.put(EventKeys.PERSON_AGE, person.getAge());
        personParameters.put(EventKeys.PERSON_GENDER, person.getGender());
        personParameters.put(EventKeys.PERSON_DEAD, person.isDead());
        personParameters.put(EventKeys.PERSON_DEATH_DATE, person.getDeathDate());
        return personParameters;
    }

    public static Map<String, Object> encounterParameters(Encounter encounter) {
        Map<String, Object> encounterParameters = new HashMap<>();
        encounterParameters.put(EventKeys.ENCOUNTER_ID, encounter.getEncounterId());
        if (encounter.getProvider() != null) {
            encounterParameters.put(EventKeys.PROVIDER_ID, encounter.getProvider().getProviderId());
        } else {
            encounterParameters.put(EventKeys.PROVIDER_ID, null);
        }
        if (encounter.getCreator() != null) {
            encounterParameters.put(EventKeys.USER_ID, encounter.getCreator().getUserId());
        } else {
            encounterParameters.put(EventKeys.USER_ID, null);
        }
        if (encounter.getFacility() != null) {
            encounterParameters.put(EventKeys.FACILITY_ID, encounter.getFacility().getFacilityId());
        } else {
            encounterParameters.put(EventKeys.FACILITY_ID, null);
        }
        encounterParameters.put(EventKeys.ENCOUNTER_DATE, encounter.getDate());
        if (!encounter.getObservations().isEmpty()) {
            Observation obs = encounter.getObservations().iterator().next();
            encounterParameters.put(EventKeys.OBSERVATION_DATE, obs.getDate());
            encounterParameters.put(EventKeys.OBSERVATION_CONCEPT_NAME, obs.getConceptName());
            encounterParameters.put(EventKeys.PATIENT_ID, obs.getPatientId());
            encounterParameters.put(EventKeys.OBSERVATION_VALUE, obs.getValue());
        } else {
            encounterParameters.put(EventKeys.OBSERVATION_DATE, null);
            encounterParameters.put(EventKeys.OBSERVATION_CONCEPT_NAME, null);
            encounterParameters.put(EventKeys.PATIENT_ID, null);
            encounterParameters.put(EventKeys.OBSERVATION_VALUE, null);
        }

        encounterParameters.put(EventKeys.ENCOUNTER_TYPE, encounter.getEncounterType());
        return encounterParameters;
    }

    public static Map<String, Object> observationParameters(Observation obs) {
        Map<String, Object> observationParameters = new HashMap<>();
        observationParameters.put(EventKeys.OBSERVATION_DATE, obs.getDate());
        observationParameters.put(EventKeys.OBSERVATION_CONCEPT_NAME, obs.getConceptName());
        observationParameters.put(EventKeys.PATIENT_ID, obs.getPatientId());
        observationParameters.put(EventKeys.OBSERVATION_VALUE, obs.getValue());
        return  observationParameters;
    }

    public static Map<String, Object> facilityParameters(Facility facility) {
        Map<String, Object> facilityParameters = new HashMap<>();
        facilityParameters.put(EventKeys.FACILITY_ID, facility.getFacilityId());
        facilityParameters.put(EventKeys.FACILITY_NAME, facility.getName());
        facilityParameters.put(EventKeys.FACILITY_COUNTRY, facility.getCountry());
        facilityParameters.put(EventKeys.FACILITY_REGION, facility.getRegion());
        facilityParameters.put(EventKeys.FACILITY_COUNTY_DISTRICT, facility.getCountyDistrict());
        facilityParameters.put(EventKeys.FACILITY_STATE_PROVINCE, facility.getStateProvince());
        return facilityParameters;
    }

    public static Map<String, Object> providerParameters(Provider provider) {
        Map<String, Object> providerParameters = new HashMap<>();
        providerParameters.put(EventKeys.PROVIDER_ID, provider.getProviderId());
        if (provider.getPerson() != null) {
            providerParameters.put(EventKeys.PERSON_ID, provider.getPerson().getPersonId());
        } else {
            providerParameters.put(EventKeys.PERSON_ID, null);
        }
        return providerParameters;
    }
}
