package org.motechproject.openmrs.rest.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.rest.model.Attribute;
import org.motechproject.openmrs.rest.model.Location;
import org.motechproject.openmrs.rest.model.Observation;
import org.motechproject.openmrs.rest.model.Person;
import org.motechproject.openmrs.rest.model.Person.PreferredAddress;
import org.motechproject.openmrs.rest.model.Person.PreferredName;

public final class ConverterUtils {

    private ConverterUtils() { }

    public static MRSPerson convertToMrsPerson(Person person) {
        MRSPerson converted = new MRSPerson();
        converted.id(person.getUuid()).address(person.getPreferredAddress().getAddress1())
                .birthDateEstimated(person.isBirthdateEstimated()).dateOfBirth(person.getBirthdate())
                .dead(person.isDead()).deathDate(person.getDeathDate())
                .firstName(person.getPreferredName().getGivenName())
                .middleName(person.getPreferredName().getMiddleName())
                .lastName(person.getPreferredName().getFamilyName()).gender(person.getGender())
                .preferredName(person.getPreferredName().getDisplay());
        for (Attribute attr : person.getAttributes()) {
            // extract name/value from the display property
            // there is no explicit property for name attribute
            // the display attribute is formatted as: name = value
            String display = attr.getDisplay();
            int index = display.indexOf("=");
            String name = display.substring(0, index).trim();

            converted.addAttribute(new org.motechproject.mrs.model.Attribute(name, attr.getValue()));
        }

        return converted;
    }

    public static Person convertToPerson(MRSPerson person, boolean includeNames) {
        Person converted = new Person();
        converted.setUuid(person.getId());
        converted.setBirthdate(person.getDateOfBirth());
        converted.setBirthdateEstimated((Boolean) ObjectUtils.defaultIfNull(person.getBirthDateEstimated(), false));
        converted.setDead(person.isDead());
        converted.setDeathDate(person.deathDate());
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

    public static MRSFacility convertLocationToMrsLocation(Location location) {
        return new MRSFacility(location.getUuid(), location.getName(), location.getCountry(), location.getAddress6(),
                location.getCountyDistrict(), location.getStateProvince());
    }

    public static MRSObservation convertObservationToMrsObservation(Observation ob) {
        return new MRSObservation(ob.getUuid(), ob.getObsDatetime(), ob.getConcept().getDisplay(), ob.getValue()
                .getDisplay());
    }
}
