package org.motechproject.mrs.services.impl;

import org.joda.time.DateTime;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.domain.MRSUser;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.MRSEncounterDto;
import org.motechproject.mrs.model.MRSFacilityDto;
import org.motechproject.mrs.model.MRSObservationDto;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.model.MRSPersonDto;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSPersonAdapter;
import org.motechproject.mrs.services.MRSProviderAdapter;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.motechproject.mrs.services.MrsActionProxyService;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MrsActionProxyServiceImpl implements MrsActionProxyService {

    private List<MRSPatientAdapter> patientAdapters;
    private List<MRSEncounterAdapter> encounterAdapters;
    private List<MRSFacilityAdapter> facilityAdapters;
    private List<MRSUserAdapter> userAdapters;
    private List<MRSProviderAdapter> providerAdapters;
    private List<MRSPersonAdapter> personAdapters;


    @Override
    public void createPatient(String patientId, String motechId, String facilityId, String personId) {
        MRSFacility facility = null;
        if (facilityId != null) {
            facility = facilityAdapters.get(0).getFacility(facilityId);
        }
        MRSPerson person = null;
        if (personId != null) {
            person = personAdapters.get(0).findByPersonId(personId).get(0);
        }
        patientAdapters.get(0).savePatient(new MRSPatientDto(patientId, facility, person, motechId));
    }

    @Override
    public void updatePatient(String patientId, String motechId, String facilityId, String personId) {
        MRSFacility facility = null;
        if (facilityId != null) {
            facility = facilityAdapters.get(0).getFacility(facilityId);
        }
        MRSPerson person = null;
        if (personId != null) {
            person = personAdapters.get(0).findByPersonId(personId).get(0);
        }
        patientAdapters.get(0).updatePatient(new MRSPatientDto(patientId, facility, person, motechId));
    }

    @Override
    public void deceasePatient(String motechId, String conceptName, DateTime dateOfDeath, String comment) throws PatientNotFoundException {
        patientAdapters.get(0).deceasePatient(motechId, conceptName, dateOfDeath.toDate(), comment);
    }

    @Override
    public void deletePatient(String motechId) throws PatientNotFoundException {
        patientAdapters.get(0).deletePatient(new MRSPatientDto(null, null, null, motechId));
    }

    @Override
    public void createEncounter(String motechId, String facilityId, String userId, String providerId, DateTime encounterDate,
                                String encounterType, DateTime observationDate, String conceptName, String patientId, Object value) {
        MRSPatient patient = patientAdapters.get(0).getPatient(motechId);
        MRSFacility facility = facilityAdapters.get(0).getFacility(facilityId);
        MRSUser user = userAdapters.get(0).getUserByUserName(userId);
        MRSProvider provider = providerAdapters.get(0).getProviderByProviderId(providerId);
        Date obsDate = null;
        if (observationDate!=null) {
            obsDate = observationDate.toDate();
        }
        MRSObservation obs = new MRSObservationDto(obsDate, conceptName, patientId, value);
        Set<MRSObservation> observations = new HashSet<>();
        observations.add(obs);
        encounterAdapters.get(0).createEncounter(new MRSEncounterDto(provider, user, facility, encounterDate.toDate(), observations, patient, encounterType));
    }

    @Override
    public void createFacility(String name, String country, String region, String countyDistrict, String stateProvince) {
        facilityAdapters.get(0).saveFacility(new MRSFacilityDto(name, country, region, countyDistrict, stateProvince));
    }

    @Override
    public void updateFacility(String facilityId, String name, String country, String region, String countyDistrict, String stateProvince) {
        MRSFacilityDto facilityDto = new MRSFacilityDto(name, country, region, countyDistrict, stateProvince);
        facilityDto.setFacilityId(facilityId);
        facilityAdapters.get(0).updateFacility(facilityDto);
    }

    @Override
    public void deleteFacility(String facilityId) {
        facilityAdapters.get(0).deleteFacility(facilityId);
    }

    @Override
    public void createPerson(String personId, String firstName, String middleName, String lastName, String preferredName, String address,
                             DateTime dateOfBirth, String birthDateEstimated, Integer age, String gender, String dead, DateTime deathDate) {
        personAdapters.get(0).addPerson(new MRSPersonDto(personId, firstName, middleName, lastName, preferredName, address, dateOfBirth, Boolean.valueOf(birthDateEstimated), age, gender, Boolean.parseBoolean(dead), null, deathDate));
    }

    @Override
    public void updatePerson(String personId, String firstName, String middleName, String lastName, String preferredName, String address,
                             DateTime dateOfBirth, String birthDateEstimated, Integer age, String gender, String dead, DateTime deathDate) {
        personAdapters.get(0).updatePerson(new MRSPersonDto(personId, firstName, middleName, lastName, preferredName, address, dateOfBirth, Boolean.valueOf(birthDateEstimated), age, gender, Boolean.parseBoolean(dead), null, deathDate));
    }

    @Override
    public void removePerson(String personId, String firstName, String middleName, String lastName, String preferredName, String address,
                             DateTime dateOfBirth, String birthDateEstimated, Integer age, String gender, String dead, DateTime deathDate) {
        personAdapters.get(0).removePerson(new MRSPersonDto(personId, firstName, middleName, lastName, preferredName, address, dateOfBirth, Boolean.valueOf(birthDateEstimated), age, gender, Boolean.parseBoolean(dead), null, deathDate));
    }

    public void setPatientAdapters(List<MRSPatientAdapter> patientAdapters) {
        this.patientAdapters = patientAdapters;
    }

    public void setEncounterAdapters(List<MRSEncounterAdapter> encounterAdapters) {
        this.encounterAdapters = encounterAdapters;
    }

    public void setFacilityAdapters(List<MRSFacilityAdapter> facilityAdapters) {
        this.facilityAdapters = facilityAdapters;
    }

    public void setUserAdapters(List<MRSUserAdapter> userAdapters) {
        this.userAdapters = userAdapters;
    }

    public void setProviderAdapters(List<MRSProviderAdapter> providerAdapters) {
        this.providerAdapters = providerAdapters;
    }

    public void setPersonAdapters(List<MRSPersonAdapter> personAdapters) {
        this.personAdapters = personAdapters;
    }

}
