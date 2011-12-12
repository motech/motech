package org.motechproject.openmrs.services;

import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.services.MRSObservationAdaptor;
import org.openmrs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class OpenMRSObservationAdaptor implements MRSObservationAdaptor{

    @Autowired
    OpenMRSConceptAdaptor conceptAdaptor;

    public <T> Obs createObservationForEncounter(MRSObservation<T> mrsObservation, Encounter encounter, Patient patient, Location facility, User staff) {
        Obs openMrsObservation = new Obs();
        openMrsObservation.setConcept(conceptAdaptor.getConceptByName(mrsObservation.getConceptName()));
        openMrsObservation.setPerson(patient);
        openMrsObservation.setLocation(facility);
        openMrsObservation.setCreator(staff);
        openMrsObservation.setEncounter(encounter);
        openMrsObservation.setObsDatetime(mrsObservation.getDate());
        writeValueToOpenMRSObservation(mrsObservation.getValue(),openMrsObservation);
        return openMrsObservation;
    }

    public <T> void writeValueToOpenMRSObservation(T value,Obs openMRSObservation){
        if(value instanceof Double){
            openMRSObservation.setValueNumeric((Double)value);
        }else if(value instanceof String){
            openMRSObservation.setValueText((String)value);
        }else if(value instanceof Boolean){
            openMRSObservation.setValueNumeric(Boolean.TRUE.equals(value)?1.0:0.0);
        }else if(value instanceof Date){
            openMRSObservation.setValueDatetime((Date)value);
        }
    }

    public Set<Obs> createObservationForEncounters(Set<MRSObservation> mrsObservations, Encounter encounter, Patient patient, Location facility, User staff) {
        Set<Obs> openMrsObservations = new HashSet<Obs>();
        for(MRSObservation observation : mrsObservations){
            openMrsObservations.add(createObservationForEncounter(observation,encounter,patient,facility,staff));
        }
        return openMrsObservations;
    }

    public Set<MRSObservation> getObservations(Set<Obs> obs) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public Set<Obs> getOpenMrsObservations(Set<MRSObservation> observations) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}