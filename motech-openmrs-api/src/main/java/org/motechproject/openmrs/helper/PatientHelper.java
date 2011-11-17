package org.motechproject.openmrs.helper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mrs.model.Attribute;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectUnique;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Component
public class PatientHelper {
    public PersonName getFirstName(Patient patient) {
        return filter(having(on(PersonName.class).isPreferred(), is(false)), patient.getNames()).get(0);
    }

    public String getPreferredName(Patient patient) {
        final List<PersonName> preferredNames = filter(having(on(PersonName.class).isPreferred(), is(true)), patient.getNames());
        if (CollectionUtils.isNotEmpty(preferredNames)) {
            return preferredNames.get(0).getGivenName();
        }
        return null;
    }

    public String getAddress(Patient patient) {
        String address = null;
        final Set<PersonAddress> addresses = patient.getAddresses();
        if (!addresses.isEmpty()) {
            address = addresses.iterator().next().getAddress1();
        }
        return address;
    }

    public Patient buildOpenMrsPatient(org.motechproject.mrs.model.Patient patient, String motechId,
                                       PatientIdentifierType patientIdentifierType, Location location,
                                       List<PersonAttributeType> allPersonAttributeTypes) {

        final Patient openMRSPatient = new Patient(createPersonWithNames(patient));
        openMRSPatient.addIdentifier(new PatientIdentifier(getMotechId(patient, motechId), patientIdentifierType, location));
        openMRSPatient.setGender(patient.getGender());
        openMRSPatient.setBirthdate(patient.getDateOfBirth());
        openMRSPatient.setBirthdateEstimated(patient.getBirthDateEstimated());
        setPatientAddress(openMRSPatient, patient.getAddress());
        setPersonAttributes(patient, openMRSPatient, allPersonAttributeTypes);
        return openMRSPatient;
    }

    private String getMotechId(org.motechproject.mrs.model.Patient patient, String motechId) {
        return patient.getId() != null ? patient.getId() : motechId;
    }

    private Person createPersonWithNames(org.motechproject.mrs.model.Patient patient) {
        final Person person = new Person();
        for (PersonName name : getAllNames(patient)) {
            person.addName(name);
        }
        return person;
    }

    private List<PersonName> getAllNames(org.motechproject.mrs.model.Patient patient) {
        final List<PersonName> personNames = new ArrayList<PersonName>();
        personNames.add(new PersonName(patient.getFirstName(), patient.getMiddleName(), patient.getLastName()));
        if (StringUtils.isNotEmpty(patient.getPreferredName())) {
            final PersonName personName = new PersonName(patient.getPreferredName(), patient.getMiddleName(), patient.getLastName());
            personName.setPreferred(true);
            personNames.add(personName);
        }
        return personNames;
    }

    private void setPatientAddress(Patient patient, String address) {
        if (address != null) {
            PersonAddress personAddress = new PersonAddress();
            personAddress.setAddress1(address);
            patient.addAddress(personAddress);
        }
    }

    private void setPersonAttributes(org.motechproject.mrs.model.Patient patient, Patient openMRSPatient,
                                     List<PersonAttributeType> allPersonAttributeTypes) {
        if(CollectionUtils.isNotEmpty(patient.getAttributes())){
            for (Attribute attribute : patient.getAttributes()) {
                PersonAttributeType attributeType = (PersonAttributeType) selectUnique(allPersonAttributeTypes,
                        having(on(PersonAttributeType.class).getName(), equalTo(attribute.name())));
                openMRSPatient.addAttribute(new PersonAttribute(attributeType, attribute.value()));
            }
        }
    }
}
