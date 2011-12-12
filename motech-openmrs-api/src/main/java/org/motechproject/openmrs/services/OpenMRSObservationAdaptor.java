package org.motechproject.openmrs.services;

import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.services.MRSObservationAdaptor;
import org.openmrs.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class OpenMRSObservationAdaptor implements MRSObservationAdaptor {

    @Autowired
    OpenMRSConceptAdaptor conceptAdaptor;

    public <T> Obs createOpenMRSObservationForEncounter(MRSObservation<T> mrsObservation, Encounter encounter, Patient patient, Location facility, User staff) {
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

    public <T> void writeValueToOpenMRSObservation(T value, Obs openMRSObservation) {
        if (value instanceof Double) {
            openMRSObservation.setValueNumeric((Double) value);
        } else if (value instanceof String) {
            openMRSObservation.setValueText((String) value);
        } else if (value instanceof Boolean) {
            openMRSObservation.setValueNumeric(Boolean.TRUE.equals(value) ? 1.0 : 0.0);
        } else if (value instanceof Date) {
            openMRSObservation.setValueDatetime((Date) value);
        }
    }

    public Set<Obs> createOpenMRSObservationForEncounters(Set<MRSObservation> mrsObservations, Encounter encounter, Patient patient, Location facility, User staff) {
        Set<Obs> openMrsObservations = new HashSet<Obs>();
        for (MRSObservation observation : mrsObservations) {
            openMrsObservations.add(createOpenMRSObservationForEncounter(observation, encounter, patient, facility, staff));
        }
        return openMrsObservations;
    }

    public Set<MRSObservation> convertOpenMRSToMRSObservations(Set<Obs> openMrsObservations) {
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        for (Obs obs : openMrsObservations) {
            mrsObservations.add(new MRSObservation(Integer.toString(obs.getId()), obs.getObsDatetime(), obs.getConcept().getName().getName(), obs.getValueText()));  // to check how to get the value without knowing its type
        }
        return mrsObservations;  //To change body of created methods use File | Settings | File Templates.
    }


}