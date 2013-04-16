package org.motechproject.couch.mrs.impl;

import org.motechproject.couch.mrs.model.CouchObservationImpl;
import org.motechproject.couch.mrs.repository.AllCouchObservations;
import org.motechproject.couch.mrs.util.CouchDAOBroker;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class CouchObservationAdapter implements MRSObservationAdapter {

    @Autowired
    private AllCouchObservations allCouchObservations;

    @Autowired
    private CouchDAOBroker daoBroker;

    @Override
    public void voidObservation(MRSObservation mrsObservation, String reason, String mrsUserMotechId)
            throws ObservationNotFoundException {

        MRSObservation obs = getObservationById(mrsObservation.getObservationId());

        if (obs == null) {
            throw new ObservationNotFoundException("The observation with id: " + mrsObservation.getObservationId() + " was not found in the Couch database");
        }

        allCouchObservations.removeObservation(obs);
    }

    @Override
    public MRSObservation findObservation(String patientMotechId, String conceptName) {
        return daoBroker.returnObs(allCouchObservations.findByMotechIdAndConceptName(patientMotechId, conceptName));
    }

    @Override
    public List<MRSObservation> findObservations(String patientMotechId, String conceptName) {
        List<CouchObservationImpl> obsList = allCouchObservations.findByMotechIdAndConceptName(patientMotechId, conceptName);

        return generateObsList(obsList);
    }

    private List<MRSObservation> generateObsList(List<CouchObservationImpl> obsList) {

        List<MRSObservation> observations = new ArrayList<MRSObservation>();

        if (obsList != null && obsList.size() > 0) {
            for (CouchObservationImpl obs : obsList) {
                observations.add(daoBroker.buildFullObservation(obs));
            }
        }

        return observations;
    }

    @Override
    public MRSObservation getObservationById(String observationId) {
        return daoBroker.returnObs(allCouchObservations.findByObservationId(observationId));
    }
}
