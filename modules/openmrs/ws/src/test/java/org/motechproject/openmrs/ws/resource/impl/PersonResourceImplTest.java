package org.motechproject.openmrs.ws.resource.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Attribute;
import org.motechproject.openmrs.ws.resource.model.Attribute.AttributeType;
import org.motechproject.openmrs.ws.resource.model.AttributeTypeListResult;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.resource.model.Person.PreferredAddress;
import org.motechproject.openmrs.ws.resource.model.Person.PreferredName;

public class PersonResourceImplTest extends AbstractResourceImplTest {
    private PersonResourceImpl impl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        impl = new PersonResourceImpl(getClient(), getInstance());
    }

    @Test
    public void shouldGetPersonById() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/person-response.json"));

        Person person = impl.getPersonById("PPP");

        assertNotNull(person);
    }

    @Test
    public void shouldCreatePerson() throws HttpException, IOException {
        Person person = buildPerson();

        impl.createPerson(person);

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postForJson(Mockito.any(URI.class), sentJson.capture());
        String expectedJson = readJsonFromFile("json/person-create.json");

        assertEquals(stringToJsonElement(expectedJson), stringToJsonElement(sentJson.getValue()));
    }

    private Person buildPerson() {
        Person person = new Person();
        person.setGender("M");

        PreferredName name = new PreferredName();
        name.setGivenName("John");
        name.setMiddleName("E");
        name.setFamilyName("Doe");
        person.setNames(Arrays.asList(name));

        PreferredAddress addr = new PreferredAddress();
        addr.setAddress1("5 Main St");
        person.setAddresses(Arrays.asList(addr));
        return person;
    }

    @Test
    public void shouldCreateAttribute() throws HttpException, IOException {
        AttributeType at = new AttributeType();
        at.setUuid("AAA");
        Attribute attr = new Attribute();
        attr.setValue("Motech");
        attr.setAttributeType(at);

        impl.createPersonAttribute("PPP", attr);

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postForJson(Mockito.any(URI.class), sentJson.capture());

        String expectedJson = readJsonFromFile("json/person-attribute-create.json");
        assertEquals(stringToJsonElement(expectedJson), stringToJsonElement(sentJson.getValue()));
    }

    @Test
    public void shouldGetAttributeTypeByName() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/person-attribute-type-response.json"));
        AttributeTypeListResult result = impl.queryPersonAttributeTypeByName("Citizenship");

        assertEquals(1, result.getResults().size());
    }

    @Test
    public void shouldNotIncludeUuidOnPersonUpdate() throws HttpException, IOException {
        Person person = new Person();
        person.setUuid("AAA");
        person.setGender("F");

        impl.updatePerson(person);

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postWithEmptyResponseBody(Mockito.any(URI.class), sentJson.capture());

        String expectedJson = readJsonFromFile("json/person-update.json");
        assertEquals(stringToJsonElement(expectedJson), stringToJsonElement(sentJson.getValue()));
    }

    @Test
    public void shouldNotIncludeUuidInPersonNameUpdate() throws HttpException, IOException {
        PreferredName name = new PreferredName();
        name.setUuid("AAA");
        name.setGivenName("Motech");
        name.setMiddleName("E");
        name.setGivenName("Test");

        impl.updatePersonName("CCC", name);

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postWithEmptyResponseBody(Mockito.any(URI.class), sentJson.capture());

        String expectedJson = readJsonFromFile("json/person-name-update.json");
        assertEquals(stringToJsonElement(expectedJson), stringToJsonElement(sentJson.getValue()));
    }

    @Test
    public void shouldNotIncludeUuidInPersonAddressUpdate() throws HttpException, IOException {
        PreferredAddress addr = new PreferredAddress();
        addr.setAddress1("Test");
        addr.setUuid("AAA");

        impl.updatePersonAddress("CCC", addr);

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postWithEmptyResponseBody(Mockito.any(URI.class), sentJson.capture());

        String expectedJson = "{\"address1\":\"Test\"}";
        assertEquals(stringToJsonElement(expectedJson), stringToJsonElement(sentJson.getValue()));
    }
}
