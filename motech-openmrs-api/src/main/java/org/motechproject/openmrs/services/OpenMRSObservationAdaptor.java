package org.motechproject.openmrs.services;

import org.motechproject.mrs.model.Observation;
import org.openmrs.Obs;

import java.util.HashSet;
import java.util.Set;


public class OpenMRSObservationAdaptor {

    public Set<Obs> getOpenMrsObservations(Set<Observation> observations){
        Set<Obs> openMrsobservations = new HashSet<org.openmrs.Obs>();
        return openMrsobservations;
    }

    public Set<Observation> getObservations(Set<Obs> obs) {
        return null;  
    }
}