package org.motechproject.openmrs.util;

import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.openmrs.*;

import java.util.Date;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PatientTestUtil {

    public org.openmrs.Patient setUpOpenMRSPatient(Person person, String first, String middle, String last, String address1, Date birthdate, boolean birthdateEstimated, String gender, MRSFacility facility) {
        PersonName personName = new PersonName(first, middle, last);
        person.addName(personName);
        setAddress(person, address1);
        final org.openmrs.Patient mrsPatient = new org.openmrs.Patient(person);
        mrsPatient.setBirthdate(birthdate);
        mrsPatient.setBirthdateEstimated(birthdateEstimated);
        mrsPatient.setGender(gender);
        mrsPatient.addIdentifier(new PatientIdentifier(null, null, new Location(Integer.parseInt(facility.getId()))));
        return mrsPatient;
    }

    private void setAddress(Person person, String address1) {
        final PersonAddress address = new PersonAddress();
        address.setAddress1(address1);
        final HashSet<PersonAddress> addresses = new HashSet<PersonAddress>();
        addresses.add(address);
        person.setAddresses(addresses);
    }

    public void verifyReturnedPatient(String first, String middle, String last, String address1, Date birthdate, Boolean birthDateEstimated, String gender, MRSFacility facility, MRSPatient actualPatient) {
        MRSPerson actualMRSPerson = actualPatient.getPerson();
        assertThat(actualMRSPerson.getFirstName(), is(first));
        assertThat(actualMRSPerson.getLastName(), is(last));
        assertThat(actualMRSPerson.getMiddleName(), is(middle));
        assertThat(actualMRSPerson.getAddress(), is(address1));
        assertThat(actualMRSPerson.getDateOfBirth(), is(birthdate));
        assertThat(actualMRSPerson.getGender(), is(gender));
        assertThat(actualPatient.getFacility(), is(equalTo(facility)));
    }
}
