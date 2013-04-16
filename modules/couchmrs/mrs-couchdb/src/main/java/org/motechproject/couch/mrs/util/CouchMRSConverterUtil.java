package org.motechproject.couch.mrs.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.motechproject.couch.mrs.model.CouchAttribute;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.domain.MRSUser;

public final class CouchMRSConverterUtil {

    private CouchMRSConverterUtil() { 
    }

    public static CouchEncounterImpl convertEncounterToEncounterImpl(MRSEncounter mrsEncounter) {

        MRSProvider provider = mrsEncounter.getProvider();
        MRSFacility facility = mrsEncounter.getFacility();
        MRSUser creator = mrsEncounter.getCreator();
        MRSPatient patient = mrsEncounter.getPatient();
        Set<? extends MRSObservation> observations = mrsEncounter.getObservations();

        String providerId = null;
        String facilityId = null;
        String creatorId = null;
        String patientId = null;
        Set<String> observationIds = null;

        if (provider != null) {
            providerId = provider.getProviderId();
        }

        if (facility != null) {
            facilityId = facility.getFacilityId();
        }

        if (creator != null) {
            creatorId = creator.getUserId();
        }

        if (patient != null) {
            patientId = patient.getMotechId();
        }

        if (observations != null && observations.size() > 0) {
            Iterator<? extends MRSObservation> obsIterator = observations.iterator();
            observationIds = new HashSet<String>();
            while (obsIterator.hasNext()) {
                String obsId = obsIterator.next().getObservationId();
                if (obsId != null && obsId.trim().length() > 0) {
                    observationIds.add(obsId);
                }
            }
        }

        return new CouchEncounterImpl(mrsEncounter.getEncounterId(), providerId, creatorId, facilityId, mrsEncounter.getDate(), observationIds, patientId, mrsEncounter.getEncounterType());
    }


    public static CouchPatientImpl createPatient (MRSPatient patient) {
        List<MRSAttribute> attributeList = new ArrayList<>();

        CouchPerson person = new CouchPerson();
        
        if (patient.getPerson() != null) {
            for (MRSAttribute attribute : patient.getPerson().getAttributes()){
                CouchAttribute couchAttribute = new CouchAttribute();
                couchAttribute.setName(attribute.getName());
                couchAttribute.setValue(attribute.getValue());

                attributeList.add(couchAttribute);
            }
            person.setAddress(patient.getPerson().getAddress());
            person.setFirstName(patient.getPerson().getFirstName());
            person.setLastName(patient.getPerson().getLastName());
            person.setAge(patient.getPerson().getAge());
            person.setBirthDateEstimated(patient.getPerson().getBirthDateEstimated());
            person.setDateOfBirth(patient.getPerson().getDateOfBirth());
            person.setDead(patient.getPerson().isDead());
            person.setDeathDate(patient.getPerson().getDeathDate());
            person.setGender(patient.getPerson().getGender());
            person.setMiddleName(patient.getPerson().getMiddleName());
            person.setPersonId(patient.getPerson().getPersonId());
            person.setPreferredName(patient.getPerson().getPreferredName());
            person.setAttributes(attributeList);
        }

        MRSFacility facility = patient.getFacility();

        String facilityId = null;
        if (facility != null) {
            facilityId = facility.getFacilityId();
        }

        return new CouchPatientImpl(patient.getPatientId(), patient.getMotechId(), person, facilityId);
    }

}
