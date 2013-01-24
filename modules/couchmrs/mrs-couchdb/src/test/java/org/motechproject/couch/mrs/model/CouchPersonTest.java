package org.motechproject.couch.mrs.model;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CouchPersonTest {

    Initializer init;

    @Before
    public void initialize() {
        init = new Initializer();
    }

    @Test
    public void shouldCreateCouchMRSPersonWithAttributes() {
        CouchPerson person1 = init.initializePerson1();
        assertEquals("FirstName", person1.getFirstName());
        assertEquals("LastName", person1.getLastName());
        assertEquals("female", person1.getGender());
        assertEquals("12345", person1.getPersonId());
        assertEquals("1234567890", person1.attrValue("phone number"));
    }

    @Test
    public void shouldHandleNullValueForExternalId() {
        CouchPerson person2 = new CouchPerson();
        person2.setFirstName("FirstName");
        person2.setLastName("LastName");
        person2.addAttribute(new CouchAttribute("parity", "G4P3"));
        assertEquals(null, person2.getPersonId());
    }
}
