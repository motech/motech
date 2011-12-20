package org.motechproject.openmrs.services;

import org.motechproject.mrs.model.MRSObservation;
import org.openmrs.*;
import org.openmrs.api.ObsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.math.NumberUtils.isNumber;

public class OpenMRSObservationAdaptor {

    @Autowired
    OpenMRSConceptAdaptor conceptAdaptor;

    @Autowired
    ObsService obsService;

    <T> Obs createOpenMRSObservationForEncounter(MRSObservation<T> mrsObservation, Encounter encounter, Patient patient, Location facility, User staff) {
        Obs openMrsObservation = new Obs();
        openMrsObservation.setConcept(conceptAdaptor.getConceptByName(mrsObservation.getConceptName()));
        openMrsObservation.setPerson(patient);
        openMrsObservation.setLocation(facility);
        openMrsObservation.setCreator(staff);
        openMrsObservation.setEncounter(encounter);
        openMrsObservation.setObsDatetime(mrsObservation.getDate());
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
        } else {
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
        return convertOpenMRSToMRSObservation(obsService.saveObs(createOpenMRSObservationForEncounter(mrsObservation, encounter, patient, facility, creator), null));
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
        if (datatype.isBoolean())
            return createMRSObservation(obs, obs.getValueAsBoolean());
        else if (datatype.isDateTime())
            return createMRSObservation(obs, obs.getValueDatetime());
        else if (datatype.isNumeric())
            return createMRSObservation(obs, obs.getValueNumeric());
        else if (datatype.isText())
            return createMRSObservation(obs, obs.getValueText());
        else
            throw new IllegalArgumentException("Invalid value of the createMRSObservation from DB-" + obs);
    }

    private MRSObservation createMRSObservation(Obs obs, Object value) {
        return new MRSObservation(Integer.toString(obs.getId()), obs.getObsDatetime(), obs.getConcept().getName().getName(), value);
    }
}