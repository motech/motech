package org.motechproject.couch.mrs.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.motechproject.couch.mrs.model.CouchEncounter;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.CouchObservation;
import org.motechproject.couch.mrs.model.CouchObservationImpl;
import org.motechproject.couch.mrs.model.CouchPatient;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.motechproject.couch.mrs.repository.AllCouchObservations;
import org.motechproject.couch.mrs.repository.AllCouchPatients;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.domain.MRSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouchDAOBroker {

    @Autowired
    private AllCouchFacilities allFacilities;
    @Autowired
    private AllCouchObservations allObservations;
    @Autowired
    private AllCouchPatients allPatients;
    @Autowired
    private AllCouchProviders allProviders;

    public MRSEncounter buildFullEncounter(CouchEncounterImpl encounter) {

        MRSUser creator = null;
        MRSPatient patient = null;
        MRSProvider provider = null;
        Set<MRSObservation> observations = null;
        MRSFacility facility = null;

        //        String creatorId = encounter.getCreatorId();
        String patientId = encounter.getPatientId();
        String providerId = encounter.getProviderId();
        Set<String> observationIds = encounter.getObservationIds();
        String facilityId = encounter.getFacilityId();

        //        if (creatorId != null && creatorId.trim().length() > 0) {
        //            //Currently no user implementation
        //        }

        patient = buildFullPatient(allPatients.findByMotechId(patientId));

        List<CouchProvider> providers = allProviders.findByProviderId(providerId);

        if (providers != null && providers.size() > 0) {
            provider = providers.get(0);
        }

        List<CouchFacility> facilities = allFacilities.findByFacilityId(facilityId);

        if (facilities != null && facilities.size() > 0) {
            facility = facilities.get(0);
        }

        observations = generateObsIdSet(observationIds);

        return new CouchEncounter(encounter.getEncounterId(), provider, creator, facility, encounter.getDate(), observations, patient, encounter.getEncounterType());
    }



    private Set<MRSObservation> generateObsIdSet(Set<String> observationIds) {
        if (observationIds == null || observationIds.size() == 0) {
            return null;
        } else {
            Set<MRSObservation> obsSet = new HashSet<MRSObservation>();

            Iterator<String> obsIterator = observationIds.iterator();

            while (obsIterator.hasNext()) {
                String obsId = obsIterator.next();

                List<CouchObservationImpl> couchObservations = allObservations.findByObservationId(obsId);

                if (couchObservations != null && couchObservations.size() > 0) {
                    obsSet.add(buildFullObservation(couchObservations.get(0)));
                }
            }

            return obsSet;
        }
    }

    public MRSPatient buildFullPatient(List<CouchPatientImpl> couchPatients) {

        if (couchPatients != null && couchPatients.size() > 0) {
            CouchPatientImpl couchPatient = couchPatients.get(0);
            String facilityId = couchPatient.getFacilityId();
            List<CouchFacility> facilities = allFacilities.findByFacilityId(facilityId);
            CouchFacility facility = null;
            if (facilities != null && facilities.size() > 0) {
                facility = facilities.get(0);
            }
            return new CouchPatient(couchPatient.getPatientId(), couchPatient.getMotechId(), couchPatient.getPerson(), facility);
        }

        return null;
    }


    public MRSObservation buildFullObservation(CouchObservationImpl obs) {
        Set<MRSObservation> dependantObs = new HashSet<MRSObservation>();
        if (obs.getDependantObservationIds() != null && obs.getDependantObservationIds().size() > 1) {
            dependantObs = getDependantObsById(obs.getDependantObservationIds());
        }

        MRSObservation couchObservation = new CouchObservation(obs.getObservationId(), obs.getDate(), obs.getConceptName(), obs.getValue());
        couchObservation.setDependantObservations(dependantObs);

        return couchObservation;
    }

    private Set<MRSObservation> getDependantObsById(Set<String> dependantObservationIds) {
        Set<MRSObservation> dependantObs = new HashSet<MRSObservation>();

        Iterator<String> iterator = dependantObservationIds.iterator();

        while (iterator.hasNext()) {
            String obsId = iterator.next();
            MRSObservation obs = returnObs(allObservations.findByObservationId(obsId));
            if (obs != null) {
                dependantObs.add(obs);
            }
        }

        return dependantObs;

    }

    public MRSObservation returnObs(List<CouchObservationImpl> couchObs) {

        if (couchObs != null && couchObs.size() > 0) {
            CouchObservationImpl obs = couchObs.get(0);
            return buildFullObservation(obs);
        }

        return null;
    }

}
