package org.motechproject.openmrs.rest.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.model.Attribute;
import org.motechproject.openmrs.rest.model.Attribute.AttributeType;
import org.motechproject.openmrs.rest.model.Attribute.AttributeTypeSerializer;
import org.motechproject.openmrs.rest.model.AttributeTypeListResult;
import org.motechproject.openmrs.rest.model.Concept;
import org.motechproject.openmrs.rest.model.Concept.ConceptSerializer;
import org.motechproject.openmrs.rest.model.Person;
import org.motechproject.openmrs.rest.model.Person.PreferredAddress;
import org.motechproject.openmrs.rest.model.Person.PreferredName;
import org.motechproject.openmrs.rest.util.ConverterUtils;
import org.motechproject.openmrs.rest.util.JsonUtils;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class MRSPersonAdapterImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(MRSPersonAdapterImpl.class);

    private final Map<String, String> attributeTypeUuidCache = new HashMap<String, String>();

    private final RestClient restfulClient;
    private final OpenMrsUrlHolder urlHolder;

    @Autowired
    public MRSPersonAdapterImpl(RestClient restfulClient, OpenMrsUrlHolder urlHolder) {
        this.restfulClient = restfulClient;
        this.urlHolder = urlHolder;
    }

    public MRSPerson getPerson(String uuid) {
        Person person = retrievePerson(uuid);
        return ConverterUtils.convertToMrsPerson(person);
    }

    private Person retrievePerson(String uuid) {
        String responseJson = null;
        try {
            responseJson = restfulClient.getJson(urlHolder.getPersonFullByUuid(uuid));
        } catch (HttpException e) {
            LOGGER.error("Failed to retrieve person with uuid: " + uuid);
            throw new MRSException(e);
        }
        Person person = (Person) JsonUtils.readJson(responseJson, Person.class);
        return person;
    }

    MRSPerson savePerson(MRSPerson person) {
        Validate.notNull(person, "Person canont be null");
        Person converted = ConverterUtils.convertToPerson(person, true);
        String requestJson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create().toJson(converted);
        String responseJson = null;
        try {
            responseJson = restfulClient.postForJson(urlHolder.getPerson(), requestJson);
        } catch (HttpException e) {
            LOGGER.error("Failed to create person for: " + person.getFullName());
            throw new MRSException(e);
        }

        Person saved = (Person) JsonUtils.readJson(responseJson, Person.class);
        person.id(saved.getUuid());

        saveAttributesForPerson(person);

        return person;
    }

    void saveAttributesForPerson(MRSPerson person) {
        Gson gson = new GsonBuilder().registerTypeAdapter(AttributeType.class, new AttributeTypeSerializer()).create();
        for (org.motechproject.mrs.model.Attribute attribute : person.getAttributes()) {
            try {
                Attribute attr = new Attribute();
                attr.setValue(attribute.value());
                attr.setAttributeType(getAttributeTypeUuid(attribute.name()));
                String requestJson = gson.toJson(attr);
                restfulClient.postForJson(urlHolder.getPersonAttributeAdd(person.getId()), requestJson);
            } catch (HttpException e) {
                LOGGER.warn("Unable to add attribute to person with id: " + person.getId());
            }
        }
    }

    private AttributeType getAttributeTypeUuid(String name) {
        if (!attributeTypeUuidCache.containsKey(name)) {
            String responseJson = null;
            try {
                responseJson = restfulClient.getJson(urlHolder.getPersonAttributeType(name));
            } catch (HttpException e) {
                LOGGER.error("HTTP request failed to get attribute type uuid for attribute with name: " + name);
                throw new MRSException(e);
            }

            AttributeTypeListResult result = (AttributeTypeListResult) JsonUtils.readJson(responseJson,
                    AttributeTypeListResult.class);
            if (result.getResults().size() == 0) {
                LOGGER.error("No attribute found with name: " + name);
                throw new MRSException(new RuntimeException("No attribute with name: " + name + " found in OpenMRS"));
            } else if (result.getResults().size() > 1) {
                LOGGER.warn("Found more than 1 attribute with name: " + name);
            }
            attributeTypeUuidCache.put(name, result.getResults().get(0).getUuid());
        }

        AttributeType type = new AttributeType();
        type.setUuid(attributeTypeUuidCache.get(name));

        return type;
    }

    void deleteAllAttributes(MRSPerson person) {
        String responseJson = null;
        try {
            responseJson = restfulClient.getJson(urlHolder.getPersonFullByUuid(person.getId()));
        } catch (HttpException e) {
            LOGGER.warn("Could retrieve patient with id: " + person.getId());
            return;
        }

        Person saved = (Person) JsonUtils.readJson(responseJson, Person.class);
        List<Attribute> attributes = saved.getAttributes();
        for (Attribute attr : attributes) {
            String uri = attr.getLinks().get(0).getUri();
            try {
                restfulClient.delete(new URI(uri));
            } catch (HttpException e) {
                LOGGER.warn("Failed to delete attribute with link: " + uri);
            } catch (URISyntaxException e) {
                LOGGER.warn("Error with patient attribute uri: " + uri);
            }
        }
    }

    void updatePerson(MRSPerson person) {
        Person converted = ConverterUtils.convertToPerson(person, false);
        // uuid cannot be set on an update call
        converted.setUuid(null);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        try {
            // Must update the name and address separately when updating a
            // person. This requires finding the uuid's of the name/address
            // elements
            Person saved = retrievePerson(person.getId());

            PreferredName name = saved.getPreferredName();
            String nameUuid = name.getUuid();

            name.setGivenName(person.getFirstName());
            name.setMiddleName(person.getMiddleName());
            name.setFamilyName(person.getLastName());
            // setting uuid and display to null so they are not included in
            // request
            name.setDisplay(null);
            name.setUuid(null);

            String requestJson = gson.toJson(name);
            restfulClient.postWithEmptyResponseBody(urlHolder.getPersonNameByUuid(person.getId(), nameUuid),
                    requestJson);

            PreferredAddress addr = saved.getPreferredAddress();
            String addrUuid = addr.getUuid();

            addr.setAddress1(person.getAddress());
            // setting uuid to null so it is not included in request
            addr.setUuid(null);

            requestJson = gson.toJson(addr);
            restfulClient.postWithEmptyResponseBody(urlHolder.getPersonAddressByUuid(person.getId(), addrUuid),
                    requestJson);

            requestJson = gson.toJson(converted);
            restfulClient.postWithEmptyResponseBody(urlHolder.getPersonByUuid(person.getId()), requestJson);
        } catch (HttpException e) {
            LOGGER.error("Failed to update a person in OpenMRS with id: " + person.getId());
            throw new MRSException(e);
        }
    }

    void savePersonCauseOfDeath(String patientId, Date dateOfDeath, String conceptName) {
        Person person = new Person();
        person.setDead(true);
        person.setDeathDate(dateOfDeath);

        Concept concept = new Concept();
        concept.setDisplay(conceptName);

        Gson gson = new GsonBuilder().registerTypeAdapter(Concept.class, new ConceptSerializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

        try {
            restfulClient.postWithEmptyResponseBody(urlHolder.getPersonByUuid(patientId), gson.toJson(person));
        } catch (HttpException e) {
            LOGGER.error("Failed to save cause of death observation for patient id: " + patientId);
            throw new MRSException(e);
        }
    }
}
