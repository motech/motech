package org.motechproject.couch.mrs.model;

import org.joda.time.DateTime;

public class Initializer {

    public CouchPerson initializePerson1() {
        CouchPerson person1 = new CouchPerson();
        person1.setDateOfBirth(new DateTime(2011, 12, 12, 0, 0));
        person1.setGender("female");
        person1.setPersonId("12345");
        person1.setFirstName("FirstName");
        person1.setLastName("LastName");
        person1.addAttribute(new CouchAttribute("phone number", "1234567890"));
        person1.setAddress("SomeAddress");
        return person1;
    }

    public CouchPerson initializeSecondPerson() {
        CouchPerson person2 = new CouchPerson();
        person2.setAddress("AnAddress");
        person2.setDateOfBirth(new DateTime(2011, 12, 12, 0, 0));
        person2.setGender("female");
        person2.setPersonId("externalId");
        person2.setFirstName("AName");
        person2.setLastName("ALastName");
        return person2;
    }

    public CouchPerson initializeThirdPerson() {
        CouchPerson person2 = new CouchPerson();
        person2.setAddress("New address");
        person2.setDateOfBirth(new DateTime(2011, 12, 12, 0, 0));
        person2.setGender("female");
        person2.setPersonId("54322");
        person2.setFirstName("FirstName");
        person2.setLastName("LastName");
        person2.addAttribute(new CouchAttribute("parity", "G4P3"));
        return person2;
    }

    public CouchPerson initializePerson3WithOnlyExternalIdAndPhone() {
        CouchPerson person3 = new CouchPerson();
        person3.addAttribute(new CouchAttribute("phone number", "1234567890"));
        person3.setPersonId("00000");
        return person3;
    }
}
