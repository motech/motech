package org.motechproject.openmrs.ws.util;

import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.openmrs.model.OpenMRSAttribute;
import org.motechproject.openmrs.model.OpenMRSObservation;
import org.motechproject.openmrs.model.OpenMRSFacility;
import org.motechproject.openmrs.model.OpenMRSPatient;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.motechproject.openmrs.ws.resource.model.Attribute;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.Observation;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.resource.model.Person.PreferredAddress;
import org.motechproject.openmrs.ws.resource.model.Person.PreferredName;

import java.util.ArrayList;
import java.util.List;

public final class ConverterUtils {

    private ConverterUtils() {
    }

    public static OpenMRSPerson convertToMrsPerson(Person person) {
        OpenMRSPerson converted = new OpenMRSPerson();
        converted.id(person.getUuid()).birthDateEstimated(person.isBirthdateEstimated())
                .dead(person.isDead()).firstName(person.getPreferredName().getGivenName())
                .middleName(person.getPreferredName().getMiddleName())
                .lastName(person.getPreferredName().getFamilyName()).gender(person.getGender())
                .preferredName(person.getPreferredName().getDisplay());

        if (person.getPreferredAddress() != null) {
            converted.address(person.getPreferredAddress().getAddress1());
        }

        if (person.getBirthdate() != null) {
            converted.dateOfBirth(new DateTime(person.getBirthdate()));
        }

        if (person.getDeathDate() != null) {
            converted.deathDate(new DateTime(person.getDeathDate()));
        }

        for (Attribute attr : person.getAttributes()) {
            // extract name/value from the display property
            // there is no explicit property for name attribute
            // the display attribute is formatted as: name = value
            String display = attr.getDisplay();
            int index = display.indexOf('=');
            String name = display.substring(0, index).trim();

            converted.addAttribute(new org.motechproject.openmrs.model.OpenMRSAttribute(name, attr.getValue()));
        }

        return converted;
    }

    public static Person convertToPerson(MRSPerson person, boolean includeNames) {
        Person converted = new Person();
        converted.setUuid(person.getPersonId());
        if (person.getDateOfBirth() != null) {
            converted.setBirthdate(person.getDateOfBirth().toDate());
        }
        if (person.getDeathDate() != null) {
            converted.setDeathDate(person.getDeathDate().toDate());
        }
        converted.setBirthdateEstimated((Boolean) ObjectUtils.defaultIfNull(person.getBirthDateEstimated(), false));
        converted.setDead(person.isDead());
        converted.setGender(person.getGender());

        if (includeNames) {
            PreferredName name = new PreferredName();
            name.setGivenName(person.getFirstName());
            name.setMiddleName(person.getMiddleName());
            name.setFamilyName(person.getLastName());
            List<PreferredName> names = new ArrayList<PreferredName>();
            names.add(name);
            converted.setNames(names);

            PreferredAddress address = new PreferredAddress();
            address.setAddress1(person.getAddress());
            List<PreferredAddress> addresses = new ArrayList<PreferredAddress>();
            addresses.add(address);
            converted.setAddresses(addresses);
        }

        return converted;
    }

    public static OpenMRSFacility convertLocationToMrsLocation(Location location) {
        return new OpenMRSFacility(location.getUuid(), location.getName(), location.getCountry(), location.getAddress6(),
                location.getCountyDistrict(), location.getStateProvince());
    }

    public static MRSObservation convertObservationToMrsObservation(Observation ob) {
        MRSObservation obs = new OpenMRSObservation(ob.getUuid(), ob.getObsDatetime(), ob.getConcept().getDisplay(), ob.getValue()
                .getDisplay());
        if (ob.getEncounter() != null && ob.getEncounter().getPatient() != null) {
            obs.setPatientId(ob.getEncounter().getPatient().getUuid());
        }
        return obs;
    }

    public static OpenMRSPatient createPatient(MRSPatient patient) {
        MRSFacility facility = patient.getFacility();
        OpenMRSFacility openMRSFacility = null;
        if (facility != null) {
            openMRSFacility = new OpenMRSFacility(facility.getFacilityId());
            openMRSFacility.setCountry(facility.getCountry());
            openMRSFacility.setCountyDistrict(facility.getCountyDistrict());
            openMRSFacility.setName(facility.getName());
            openMRSFacility.setRegion(facility.getRegion());
            openMRSFacility.setStateProvince(facility.getStateProvince());
        }

        OpenMRSPerson openMRSPerson = createPerson(patient.getPerson());

        OpenMRSPatient openMRSPatient = new OpenMRSPatient(patient.getMotechId());
        openMRSPatient.setPatientId(patient.getPatientId());
        openMRSPatient.setFacility(openMRSFacility);
        openMRSPatient.setPerson(openMRSPerson);
        openMRSPatient.setMotechId(patient.getMotechId());

        return openMRSPatient;
    }

    public static OpenMRSPerson createPerson(MRSPerson personMrs) {
        List<MRSAttribute> attributeList = createAttributeList(personMrs.getAttributes());

        OpenMRSPerson person = new OpenMRSPerson();
        person.setPersonId(personMrs.getPersonId());
        person.setAddress(personMrs.getAddress());
        person.setFirstName(personMrs.getFirstName());
        person.setLastName(personMrs.getLastName());
        person.setAge(personMrs.getAge());
        person.setBirthDateEstimated(personMrs.getBirthDateEstimated());
        person.setDateOfBirth(personMrs.getDateOfBirth());
        if (personMrs.isDead() != null) {
            person.setDead(personMrs.isDead());
        }
        person.setDeathDate(personMrs.getDeathDate());
        person.setGender(personMrs.getGender());
        person.setMiddleName(personMrs.getMiddleName());
        person.setPreferredName(personMrs.getPreferredName());
        person.setAttributes(attributeList);

        return person;
    }

    public static List<MRSAttribute> createAttributeList(List<MRSAttribute> attributesMrs) {
        List<MRSAttribute> attributeList = new ArrayList<>();

        if (attributesMrs != null) {
            for (MRSAttribute attribute : attributesMrs) {
                attributeList.add(new OpenMRSAttribute(attribute.getName(), attribute.getValue()));
            }
        }
        return  attributeList;
    }
}
