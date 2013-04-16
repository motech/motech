package org.motechproject.couch.mrs.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.motechproject.couch.mrs.model.CouchAttribute;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
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
        CouchPerson person = new CouchPerson();

        if (patient.getPerson() != null) {
            MRSPerson patientPerson = patient.getPerson();

            person = convertPersonToCouchPerson(patientPerson);
        }

        MRSFacility facility = patient.getFacility();

        String facilityId = null;
        if (facility != null) {
            facilityId = facility.getFacilityId();
        }

        return new CouchPatientImpl(patient.getPatientId(), patient.getMotechId(), person, facilityId);
    }

    public static CouchFacility convertFacilityToCouchFacility(MRSFacility facility) {
        CouchFacility convertedFacility = new CouchFacility();

        convertedFacility.setCountry(facility.getCountry());
        convertedFacility.setCountyDistrict(facility.getCountyDistrict());
        convertedFacility.setFacilityId(facility.getFacilityId());
        convertedFacility.setName(facility.getName());
        convertedFacility.setRegion(facility.getRegion());
        convertedFacility.setStateProvince(facility.getStateProvince());

        return convertedFacility;
    }

    public static CouchPerson convertPersonToCouchPerson(MRSPerson person) {

        if (person instanceof CouchPerson) {
            return (CouchPerson) person;
        }

        CouchPerson convertedPerson = new CouchPerson();

        List<MRSAttribute> attributeList = new ArrayList<>();

        for (MRSAttribute attribute : person.getAttributes()){
            CouchAttribute couchAttribute = new CouchAttribute();
            couchAttribute.setName(attribute.getName());
            couchAttribute.setValue(attribute.getValue());

            attributeList.add(couchAttribute);
        }

        convertedPerson.setAddress(person.getAddress());
        convertedPerson.setFirstName(person.getFirstName());
        convertedPerson.setLastName(person.getLastName());
        convertedPerson.setAge(person.getAge());
        convertedPerson.setBirthDateEstimated(person.getBirthDateEstimated());
        convertedPerson.setDateOfBirth(person.getDateOfBirth());
        convertedPerson.setDead(person.isDead());
        convertedPerson.setDeathDate(person.getDeathDate());
        convertedPerson.setGender(person.getGender());
        convertedPerson.setMiddleName(person.getMiddleName());
        convertedPerson.setPersonId(person.getPersonId());
        convertedPerson.setPreferredName(person.getPreferredName());
        convertedPerson.setAttributes(attributeList);

        return convertedPerson;
    }

    public static CouchProvider convertProviderToCouchProvider(MRSProvider provider) {
        CouchPerson person = new CouchPerson();
        if (provider.getPerson() != null) {
            person = convertPersonToCouchPerson(provider.getPerson());
        }
        CouchProvider convertedProvider = new CouchProvider(provider.getProviderId(), person);

        return convertedProvider;
    }

}
