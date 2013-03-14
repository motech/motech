package org.motechproject.mrs.services.impl;

import org.joda.time.DateTime;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.Provider;
import org.motechproject.mrs.domain.User;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.EncounterDto;
import org.motechproject.mrs.model.FacilityDto;
import org.motechproject.mrs.model.ObservationDto;
import org.motechproject.mrs.model.PatientDto;
import org.motechproject.mrs.model.PersonDto;
import org.motechproject.mrs.services.EncounterAdapter;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.mrs.services.MrsActionProxyService;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.mrs.services.PersonAdapter;
import org.motechproject.mrs.services.ProviderAdapter;
import org.motechproject.mrs.services.UserAdapter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MrsActionProxyServiceImpl implements MrsActionProxyService {

    private List<PatientAdapter> patientAdapters;
    private List<EncounterAdapter> encounterAdapters;
    private List<FacilityAdapter> facilityAdapters;
    private List<UserAdapter> userAdapters;
    private List<ProviderAdapter> providerAdapters;
    private List<PersonAdapter> personAdapters;


    @Override
    public void createPatient(String patientId, String motechId, String facilityId, String personId) {
        Facility facility = null;
        if (facilityId != null) {
            facility = facilityAdapters.get(0).getFacility(facilityId);
        }
        Person person = null;
        if (personId != null) {
            person = personAdapters.get(0).findByPersonId(personId).get(0);
        }
        patientAdapters.get(0).savePatient(new PatientDto(patientId, facility, person, motechId));
    }

    @Override
    public void updatePatient(String patientId, String motechId, String facilityId, String personId) {
        Facility facility = null;
        if (facilityId != null) {
            facility = facilityAdapters.get(0).getFacility(facilityId);
        }
        Person person = null;
        if (personId != null) {
            person = personAdapters.get(0).findByPersonId(personId).get(0);
        }
        patientAdapters.get(0).updatePatient(new PatientDto(patientId, facility, person, motechId));
    }

    @Override
    public void deceasePatient(String motechId, String conceptName, DateTime dateOfDeath, String comment) throws PatientNotFoundException {
        patientAdapters.get(0).deceasePatient(motechId, conceptName, dateOfDeath.toDate(), comment);
    }

    @Override
    public void createEncounter(String motechId, String facilityId, String userId, String providerId, DateTime encounterDate,
                                String encounterType, DateTime observationDate, String conceptName, String patientId, Object value) {
        Patient patient = patientAdapters.get(0).getPatient(motechId);
        Facility facility = facilityAdapters.get(0).getFacility(facilityId);
        User user = userAdapters.get(0).getUserByUserName(userId);
        Provider provider = providerAdapters.get(0).getProviderByProviderId(providerId);
        Date obsDate = null;
        if (observationDate!=null) {
            obsDate = observationDate.toDate();
        }
        Observation obs = new ObservationDto(obsDate, conceptName, patientId, value);
        Set<Observation> observations = new HashSet<>();
        observations.add(obs);
        encounterAdapters.get(0).createEncounter(new EncounterDto(provider, user, facility, encounterDate.toDate(), observations, patient, encounterType));
    }

    @Override
    public void createFacility(String name, String country, String region, String countyDistrict, String stateProvince) {
        facilityAdapters.get(0).saveFacility(new FacilityDto(name, country, region, countyDistrict, stateProvince));
    }

    @Override
    public void createPerson(String personId, String firstName, String middleName, String lastName, String preferredName, String address,
                             DateTime dateOfBirth, String birthDateEstimated, Integer age, String gender, String dead, DateTime deathDate) {
        personAdapters.get(0).addPerson(new PersonDto(personId, firstName, middleName, lastName, preferredName, address, dateOfBirth, Boolean.valueOf(birthDateEstimated), age, gender, Boolean.parseBoolean(dead), null, deathDate));
    }

    @Override
    public void updatePerson(String personId, String firstName, String middleName, String lastName, String preferredName, String address,
                             DateTime dateOfBirth, String birthDateEstimated, Integer age, String gender, String dead, DateTime deathDate) {
        personAdapters.get(0).updatePerson(new PersonDto(personId, firstName, middleName, lastName, preferredName, address, dateOfBirth, Boolean.valueOf(birthDateEstimated), age, gender, Boolean.parseBoolean(dead), null, deathDate));
    }

    @Override
    public void removePerson(String personId, String firstName, String middleName, String lastName, String preferredName, String address,
                             DateTime dateOfBirth, String birthDateEstimated, Integer age, String gender, String dead, DateTime deathDate) {
        personAdapters.get(0).removePerson(new PersonDto(personId, firstName, middleName, lastName, preferredName, address, dateOfBirth, Boolean.valueOf(birthDateEstimated), age, gender, Boolean.parseBoolean(dead), null, deathDate));
    }

    public void setPatientAdapters(List<PatientAdapter> patientAdapters) {
        this.patientAdapters = patientAdapters;
    }

    public void setEncounterAdapters(List<EncounterAdapter> encounterAdapters) {
        this.encounterAdapters = encounterAdapters;
    }

    public void setFacilityAdapters(List<FacilityAdapter> facilityAdapters) {
        this.facilityAdapters = facilityAdapters;
    }

    public void setUserAdapters(List<UserAdapter> userAdapters) {
        this.userAdapters = userAdapters;
    }

    public void setProviderAdapters(List<ProviderAdapter> providerAdapters) {
        this.providerAdapters = providerAdapters;
    }

    public void setPersonAdapters(List<PersonAdapter> personAdapters) {
        this.personAdapters = personAdapters;
    }

}
