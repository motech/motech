package org.motechproject.openmrs.atomfeed.events;

public final class EventSubjects {

    private EventSubjects() {
    }

    /**
     * Base Subject for Motech OpenMRS Atom Feed module
     */
    public static final String BASE_SUBJECT = "org.motechproject.openmrs.atomfeed";

    /**
     * Subject raised to poll OpenMRS for new changes. This is raised only if
     * you have configured the Motech OpenMRS Atom Feed module polling
     */
    public static final String POLLING_SUBJECT = BASE_SUBJECT + ".poll";

    /**
     * Subject raised if there was an exception during the poll to the OpenMRS
     */
    public static final String POLLING_EXCEPTION = BASE_SUBJECT + ".exception";

    /**
     * Subject raised on an OpenMRS Patient create change
     */
    public static final String PATIENT_CREATE = BASE_SUBJECT + ".create.patient";

    /**
     * Subject raised on an OpenMRS Patient update change
     */
    public static final String PATIENT_UPDATE = BASE_SUBJECT + ".update.patient";

    /**
     * Subject raised on an OpenMRS Patient void change
     */
    public static final String PATIENT_VOIDED = BASE_SUBJECT + ".voided.patient";

    /**
     * Subject raised on an OpenMRS Patient deleted change
     */
    public static final String PATIENT_DELETED = BASE_SUBJECT + ".deleted.patient";

    /**
     * Subject raised on an OpenMRS Concept create change
     */
    public static final String CONCEPT_CREATE = BASE_SUBJECT + ".create.concept";

    /**
     * Subject raised on an OpenMRS Concept updated change
     */
    public static final String CONCEPT_UPDATED = BASE_SUBJECT + ".update.concept";

    /**
     * Subject raised on an OpenMRS Concept voided change
     */
    public static final String CONCEPT_VOIDED = BASE_SUBJECT + ".voided.concept";

    /**
     * Subject raised on an OpenMRS Concept deleted change
     */
    public static final String CONCEPT_DELETED = BASE_SUBJECT + ".deleted.concept";

    /**
     * Subject raised on an OpenMRS Encounter create change
     */
    public static final String ENCOUNTER_CREATE = BASE_SUBJECT + ".create.encounter";

    /**
     * Subject raised on an OpenMRS Encounter updated change
     */
    public static final String ENCOUNTER_UPDATE = BASE_SUBJECT + ".update.encounter";

    /**
     * Subject raised on an OpenMRS Encounter voided change
     */
    public static final String ENCOUNTER_VOIDED = BASE_SUBJECT + ".voided.encounter";

    /**
     * Subject raised on an OpenMRS Encounter deleted change
     */
    public static final String ENCOUNTER_DELETED = BASE_SUBJECT + ".deleted.encounter";

    /**
     * Subject raised on an OpenMRS Observation create change
     */
    public static final String OBSERVATION_CREATE = BASE_SUBJECT + ".create.observation";

    /**
     * Subject raised on an OpenMRS Observation updated change
     */
    public static final String OBSERVATION_UPDATE = BASE_SUBJECT + ".update.observation";

    /**
     * Subject raised on an OpenMRS Observation voided change
     */
    public static final String OBSERVATION_VOIDED = BASE_SUBJECT + ".voided.observation";

    /**
     * Subject raised on an OpenMRS Observation deleted change
     */
    public static final String OBSERVATION_DELETED = BASE_SUBJECT + ".deleted.observation";
}
