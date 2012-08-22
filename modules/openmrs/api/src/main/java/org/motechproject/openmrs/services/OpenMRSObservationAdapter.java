package org.motechproject.openmrs.services;

import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.ObsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang.math.NumberUtils.isNumber;

public class OpenMRSObservationAdapter implements MRSObservationAdapter {

    @Autowired
    private OpenMRSConceptAdapter openMRSConceptAdapter;

    @Autowired
    private ObsService obsService;

    @Autowired
    private OpenMRSUserAdapter openMRSUserAdapter;

    @Autowired
    private OpenMRSPatientAdapter openMRSPatientAdapter;

    /**
     * Voids an observation for the `MOTECH user, with the given reason
     *
     * @param mrsObservation MRSObservation to be voided
     * @param reason         Reason for voiding the MRSObservation
     * @param userMotechId   MOTECH ID of the user who's MRSObservation needs to be voided
     * @throws ObservationNotFoundException Exception when the expected Observation does not exist
     */
    @Override
    public void voidObservation(MRSObservation mrsObservation, String reason, String userMotechId) throws ObservationNotFoundException {
        Obs obs = obsService.getObs(Integer.valueOf(mrsObservation.getId()));
        if (obs == null) {
            throw new ObservationNotFoundException("Observation not found for MOTECH id :" + userMotechId + " and with MRS observation id :" + mrsObservation.getId());
        }
        obs.setVoided(true);
        obs.setVoidReason(reason);
        obs.setDateVoided(new Date());
        obs.setVoidedBy(openMRSUserAdapter.getOpenMrsUserByUserName(userMotechId));
        obsService.voidObs(obs, reason);
    }

    /**
     * Returns the Latest MRSObservation of the MRS patient, given the concept name. (e.g. WEIGHT)
     *
     * @param patientMotechId MOTECH Id of the patient
     * @param conceptName     Concept Name of the MRSObservation
     * @return MRSObservation if present, else null.
     */
    @Override
    public MRSObservation findObservation(String patientMotechId, String conceptName) {
        Patient patient = openMRSPatientAdapter.getOpenmrsPatientByMotechId(patientMotechId);
        Concept concept = openMRSConceptAdapter.getConceptByName(conceptName);
        List<Obs> observations = obsService.getObservationsByPersonAndConcept(patient, concept);

        if (!observations.isEmpty()) {
            return convertOpenMRSToMRSObservation(observations.get(0));
        }
        return null;
    }

    <T> Obs createOpenMRSObservationForEncounter(MRSObservation<T> mrsObservation, Encounter encounter, Patient patient, Location location, User staff) {
        Obs openMrsObservation = new Obs();
        openMrsObservation.setConcept(openMRSConceptAdapter.getConceptByName(mrsObservation.getConceptName()));
        openMrsObservation.setPerson(patient);
        openMrsObservation.setLocation(location);
        openMrsObservation.setCreator(staff);
        openMrsObservation.setEncounter(encounter);
        openMrsObservation.setObsDatetime(mrsObservation.getDate());
        if (mrsObservation.getDependantObservations() != null && !mrsObservation.getDependantObservations().isEmpty()) {
            for (MRSObservation observation : mrsObservation.getDependantObservations()) {
                openMrsObservation.addGroupMember(createOpenMRSObservationForEncounter(observation, encounter, patient, location, staff));
            }
        }
        writeValueToOpenMRSObservation(mrsObservation.getValue(), openMrsObservation);
        return openMrsObservation;
    }

    <T> void writeValueToOpenMRSObservation(T value, Obs openMRSObservation) {
        if ((value instanceof Number) && isNumber(value.toString())) {
            openMRSObservation.setValueNumeric(Double.parseDouble(value.toString()));
        } else if (value instanceof String) {
            openMRSObservation.setValueText((String) value);
        } else if (value instanceof Boolean) {
            openMRSObservation.setValueNumeric(Boolean.TRUE.equals(value) ? 1.0 : 0.0);
        } else if (value instanceof Date) {
            openMRSObservation.setValueDatetime((Date) value);
        } else if (value instanceof MRSConcept) {
            openMRSObservation.setValueCoded(openMRSConceptAdapter.getConceptByName(((MRSConcept) value).getName()));
        } else if (value != null) {
            throw new IllegalArgumentException("Invalid value of the createMRSObservation- " + value);
        }
    }

    Set<Obs> createOpenMRSObservationsForEncounter(Set<MRSObservation> mrsObservations, Encounter encounter, Patient patient, Location facility, User staff) {
        Set<Obs> openMrsObservations = new HashSet<Obs>();
        for (MRSObservation observation : mrsObservations) {
            openMrsObservations.add(createOpenMRSObservationForEncounter(observation, encounter, patient, facility, staff));
        }
        return openMrsObservations;
    }

    MRSObservation saveObservation(MRSObservation mrsObservation, Encounter encounter, Patient patient, Location facility, User creator) {
        return convertOpenMRSToMRSObservation(saveObs(mrsObservation, encounter, patient, facility, creator));
    }

    private Obs saveObs(MRSObservation mrsObservation, Encounter encounter, Patient patient, Location facility, User creator) {
        return obsService.saveObs(createOpenMRSObservationForEncounter(mrsObservation, encounter, patient, facility, creator), null);
    }

    Set<MRSObservation> convertOpenMRSToMRSObservations(Set<Obs> openMrsObservations) {
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        for (Obs obs : openMrsObservations) {
            mrsObservations.add(convertOpenMRSToMRSObservation(obs));
        }
        return mrsObservations;
    }

    MRSObservation convertOpenMRSToMRSObservation(Obs obs) {
        ConceptDatatype datatype = obs.getConcept().getDatatype();
        if (datatype.isAnswerOnly()) {
            return createMRSObservation(obs, null);
        } else if (datatype.isBoolean()) {
            return createMRSObservation(obs, obs.getValueAsBoolean());
        } else if (datatype.isDateTime()) {
            return createMRSObservation(obs, obs.getValueDatetime());
        } else if (datatype.isNumeric()) {
            return createMRSObservation(obs, obs.getValueNumeric());
        } else if (datatype.isText()) {
            return createMRSObservation(obs, obs.getValueText());
        } else if (datatype.isCoded()) {
            return createMRSObservation(obs, new MRSConcept(obs.getValueCoded().getName().getName()));
        } else {
            throw new IllegalArgumentException("Invalid value of the createMRSObservation from DB-" + obs);
        }
    }

    private MRSObservation createMRSObservation(Obs obs, Object value) {
        final MRSObservation mrsObservation = new MRSObservation(Integer.toString(obs.getId()), obs.getObsDatetime(),
                obs.getConcept().getName().getName(), value);
        if (obs.hasGroupMembers()) {
            for (Obs observation : obs.getGroupMembers()) {
                mrsObservation.addDependantObservation(convertOpenMRSToMRSObservation(observation));
            }
        }
        return mrsObservation;
    }

    @Override
    public List<MRSObservation> findObservations(String patientMotechId, String conceptName) {
        Patient patient = openMRSPatientAdapter.getOpenmrsPatientByMotechId(patientMotechId);
        Concept concept = openMRSConceptAdapter.getConceptByName(conceptName);
        List<Obs> observations = obsService.getObservationsByPersonAndConcept(patient, concept);
        List<MRSObservation> mrsObservations = new ArrayList<MRSObservation>();
        for (Obs observation : observations) {
            mrsObservations.add(convertOpenMRSToMRSObservation(observation));
        }
        return mrsObservations;
    }
}
