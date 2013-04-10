package org.motechproject.mrs;

public final class EventKeys {
    public static final String MOTECH_ID = "MotechId";
    public static final String CONCEPT_NAME = "ConceptName";
    public static final String DATE_OF_DEATH = "DateOfDeath";
    public static final String COMMENT = "Comment";
    public static final String PATIENT_ID = "PatientId";
    public static final String PERSON_ID = "PersonId";
    public static final String PERSON_FIRST_NAME = "PersonFirstName";
    public static final String PERSON_MIDDLE_NAME = "PersonMiddleName";
    public static final String PERSON_LAST_NAME = "PersonLastName";
    public static final String PERSON_PREFERRED_NAME = "PersonPreferredName";
    public static final String PERSON_ADDRESS = "PersonAddress";
    public static final String PERSON_DATE_OF_BIRTH = "PersonDateOfBirth";
    public static final String PERSON_BIRTH_DATE_ESTIMATED = "PersonBirthDateEstimated";
    public static final String PERSON_AGE = "PersonAge";
    public static final String PERSON_GENDER = "PersonGender";
    public static final String PERSON_DEAD = "PersonDead";
    public static final String PERSON_DEATH_DATE = "PersonDeathDate";
    public static final String FACILITY_ID = "FacilityId";
    public static final String FACILITY_NAME = "FacilityName";
    public static final String FACILITY_COUNTRY = "FacilityCountry";
    public static final String FACILITY_REGION = "FacilityRegion";
    public static final String FACILITY_COUNTY_DISTRICT = "FacilityCountryDistrict";
    public static final String FACILITY_STATE_PROVINCE = "FacilityStateProvince";
    public static final String OBSERVATION_VALUE = "ObservationValue";
    public static final String OBSERVATION_CONCEPT_NAME = "ObservationConceptName";
    public static final String OBSERVATION_DATE = "ObservationDate";
    public static final String ENCOUNTER_ID = "EncounterID";
    public static final String ENCOUNTER_TYPE = "EncounterType";
    public static final String ENCOUNTER_DATE = "EncounterDate";
    public static final String PROVIDER_ID = "ProviderId";
    public static final String USER_ID = "UserId";

    public static final String BASE_SUBJECT = "org.motechproject.mrs.api.";

    public static final String CREATED_NEW_PATIENT_SUBJECT = BASE_SUBJECT + "Patient.Created";
    public static final String UPDATED_PATIENT_SUBJECT = BASE_SUBJECT + "Patient.Updated";
    public static final String PATIENT_DECEASED_SUBJECT = BASE_SUBJECT + "Patient.Deceased";
    public static final String DELETED_PATIENT_SUBJECT = BASE_SUBJECT + "Patient.Deleted";
    public static final String CREATED_NEW_OBSERVATION_SUBJECT = BASE_SUBJECT + "Observation.Created";
    public static final String CREATED_NEW_ENCOUNTER_SUBJECT = BASE_SUBJECT + "Encounter.Created";
    public static final String UPDATED_ENCOUNTER_SUBJECT = BASE_SUBJECT + "Encounter.Updated";
    public static final String CREATED_NEW_FACILITY_SUBJECT = BASE_SUBJECT + "Facility.Create";
    public static final String UPDATED_FACILITY_SUBJECT = BASE_SUBJECT + "Facility.Updated";
    public static final String DELETED_FACILITY_SUBJECT = BASE_SUBJECT + "Facility.Deleted";
    public static final String CREATED_NEW_PERSON_SUBJECT = BASE_SUBJECT + "Person.Created";
    public static final String UPDATED_PERSON_SUBJECT = BASE_SUBJECT + "Person.Updated";
    public static final String DELETED_PERSON_SUBJECT = BASE_SUBJECT + "Person.Deleted";
    public static final String CREATED_NEW_PROVIDER_SUBJECT = BASE_SUBJECT + "Provider.Created";
    public static final String UPDATED_PROVIDER_SUBJECT = BASE_SUBJECT + "Provider.Updated";
    public static final String DELETED_PROVIDER_SUBJECT = BASE_SUBJECT + "Provider.Deleted";
    public static final String DELETED_OBSERVATION_SUBJECT = BASE_SUBJECT + "Observation.Deleted";
    public static final String CREATED_UPDATED_OBSERVATION_SUBJECT = "Observation.Updated";

    private EventKeys() { }

}
