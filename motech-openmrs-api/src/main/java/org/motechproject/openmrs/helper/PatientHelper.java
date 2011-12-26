package org.motechproject.openmrs.helper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.openmrs.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
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

    public Patient buildOpenMrsPatient(MRSPatient patient, String systemGeneratedId,
                                       PatientIdentifierType patientIdentifierType, Location location,
                                       List<PersonAttributeType> allPersonAttributeTypes) {

        final Patient openMRSPatient = new Patient(createPersonWithNames(patient));
        openMRSPatient.addIdentifier(new PatientIdentifier(getMotechId(patient, systemGeneratedId), patientIdentifierType, location));
        openMRSPatient.setGender(patient.getPerson().getGender());
        openMRSPatient.setBirthdate(patient.getPerson().getDateOfBirth());
        openMRSPatient.setBirthdateEstimated(patient.getPerson().getBirthDateEstimated());
        setPatientAddress(openMRSPatient, patient.getPerson().getAddress());
        setPersonAttributes(patient, openMRSPatient, allPersonAttributeTypes);
        return openMRSPatient;
    }

    private String getMotechId(MRSPatient patient, String systemGeneratedId) {
        return StringUtils.isNotEmpty(patient.getMotechId()) ? patient.getMotechId() : systemGeneratedId;
    }

    public Person createPersonWithNames(MRSPatient patient) {
        final Person person = new Person();
        for (PersonName name : getAllNames(patient)) {
            person.addName(name);
        }
        return person;
    }

    private List<PersonName> getAllNames(MRSPatient patient) {
        final List<PersonName> personNames = new ArrayList<PersonName>();
        MRSPerson mrsPerson = patient.getPerson();
        personNames.add(new PersonName(mrsPerson.getFirstName(), mrsPerson.getMiddleName(), mrsPerson.getLastName()));
        if (StringUtils.isNotEmpty(mrsPerson.getPreferredName())) {
            final PersonName personName = new PersonName(mrsPerson.getPreferredName(), mrsPerson.getMiddleName(), mrsPerson.getLastName());
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

    private void setPersonAttributes(MRSPatient patient, Patient openMRSPatient,
                                     List<PersonAttributeType> allPersonAttributeTypes) {
        MRSPerson mrsPerson = patient.getPerson();
        if(CollectionUtils.isNotEmpty(mrsPerson.getAttributes())){
            for (Attribute attribute : mrsPerson.getAttributes()) {
                PersonAttributeType attributeType = (PersonAttributeType) selectUnique(allPersonAttributeTypes,
                        having(on(PersonAttributeType.class).getName(), equalTo(attribute.name())));
                openMRSPatient.addAttribute(new PersonAttribute(attributeType, attribute.value()));
            }
        }
    }
}
