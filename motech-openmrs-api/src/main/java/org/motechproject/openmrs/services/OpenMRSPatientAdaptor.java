package org.motechproject.openmrs.services;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mrs.model.Patient;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OpenMRSPatientAdaptor implements MRSPatientAdaptor {

    @Autowired
    PatientService patientService;

    @Override
    public Patient savePatient(Patient patient) {
        final Person person = new Person();
        for (PersonName name : getAllNames(patient)) {
            person.addName(name);
        }
        final org.openmrs.Patient savedPatient = patientService.savePatient(new org.openmrs.Patient(person));

        PersonName preferredName = getPreferredName(savedPatient);
        return new Patient(preferredName.getGivenName(), preferredName.getMiddleName(), preferredName.getFamilyName(),
                null, savedPatient.getBirthdate(), savedPatient.getGender(), getAddress(savedPatient));
    }

    private List<PersonName> getAllNames(Patient patient) {
        final List<PersonName> personNames = new ArrayList<PersonName>();
        personNames.add(new PersonName(patient.getFirstName(), patient.getMiddleName(), patient.getLastName()));
        if (StringUtils.isNotEmpty(patient.getPreferredName())) {
            final PersonName personName = new PersonName(patient.getPreferredName(), patient.getMiddleName(), patient.getLastName());
            personName.setPreferred(true);
            personNames.add(personName);
        }
        return personNames;
    }

    private PersonName getPreferredName(org.openmrs.Patient patient) {
        final Set<PersonName> personNames = patient.getNames();
        PersonName preferredName = personNames.iterator().next();
        for (PersonName personName : personNames) {
            if (personName.isPreferred()) {
                preferredName = personName;
                break;
            }
        }
        return preferredName;
    }

    private String getAddress(org.openmrs.Patient patient) {
        String address = null;
        final Set<PersonAddress> addresses = patient.getAddresses();
        if (!addresses.isEmpty()) {
            address = addresses.iterator().next().getAddress1();
        }
        return address;
    }
}
