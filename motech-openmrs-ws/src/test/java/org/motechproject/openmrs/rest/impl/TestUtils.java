package org.motechproject.openmrs.rest.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonProcessingException;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.core.io.ClassPathResource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class TestUtils {
    protected static final String TEST_PERSON_ADDRESS = "5 Main St";
    protected static final String TEST_PERSON_GENDER = "M";
    protected static final String TEST_PERSON_LAST_NAME = "Doe";
    protected static final String TEST_PERSON_MIDDLE_NAME = "E";
    protected static final String TEST_PERSON_FIRST_NAME = "John";
    protected static final String MOTECH_ID_1 = "200-08";
    protected static Date CURRENT_DATE;

    static {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);

        CURRENT_DATE = instance.getTime();
    }

    public static MRSPerson makePerson() {
        MRSPerson person = new MRSPerson().firstName(TEST_PERSON_FIRST_NAME).middleName(TEST_PERSON_MIDDLE_NAME)
                .lastName(TEST_PERSON_LAST_NAME).gender(TEST_PERSON_GENDER).address(TEST_PERSON_ADDRESS)
                .dateOfBirth(CURRENT_DATE);
        return person;
    }

    public static JsonElement parseJsonFile(String fileName) throws IOException, JsonProcessingException {
        String json = parseJsonFileAsString(fileName);
        return parseJsonString(json);
    }

    public static JsonElement parseJsonString(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json);
    }

    public static String parseJsonFileAsString(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        String read = IOUtils.toString(resource.getInputStream());
        resource.getInputStream().close();
        return read;
    }

}
