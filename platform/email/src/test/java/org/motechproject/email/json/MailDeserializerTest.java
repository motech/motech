package org.motechproject.email.json;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.motechproject.email.model.Mail;

import java.io.IOException;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.motechproject.email.constants.SendEmailConstants.FROM_ADDRESS;
import static org.motechproject.email.constants.SendEmailConstants.MESSAGE;
import static org.motechproject.email.constants.SendEmailConstants.SUBJECT;
import static org.motechproject.email.constants.SendEmailConstants.TO_ADDRESS;

public class MailDeserializerTest {
    private static final String TEST_FROM = "from@from.com";
    private static final String TEST_TO = "to@to.com";
    private static final String TEST_SUBJECT = "subject";
    private static final String TEST_TEXT = "message";

    private MailDeserializer deserializer = new MailDeserializer();

    @Test
    public void shouldDeserializeJsonToMailObject() throws Exception {
        assertThat(
                deserializer.deserialize(
                        getJsonParser(TEST_FROM, TEST_TO, TEST_SUBJECT, TEST_TEXT), null
                ),
                equalTo(new Mail(TEST_FROM, TEST_TO, TEST_SUBJECT, TEST_TEXT))
        );
    }

    @Test(expected = JsonMappingException.class)
    public void shouldThrowExceptionWhenFromAddressFieldIsBlank() throws Exception {
        deserializer.deserialize(
                getJsonParser(null, TEST_TO, TEST_SUBJECT, TEST_TEXT), null
        );
    }

    @Test(expected = JsonMappingException.class)
    public void shouldThrowExceptionWhenToAddressFieldIsBlank() throws Exception {
        deserializer.deserialize(
                getJsonParser(TEST_FROM, null, TEST_SUBJECT, TEST_TEXT), null
        );
    }

    @Test(expected = JsonMappingException.class)
    public void shouldThrowExceptionWhenSubjectFieldIsBlank() throws Exception {
        deserializer.deserialize(
                getJsonParser(TEST_FROM, TEST_TO, null, TEST_TEXT), null
        );
    }

    @Test(expected = JsonMappingException.class)
    public void shouldThrowExceptionWhenTextFieldIsBlank() throws Exception {
        deserializer.deserialize(
                getJsonParser(TEST_FROM, TEST_TO, TEST_SUBJECT, null), null
        );
    }

    private JsonParser getJsonParser(String from, String to, String subject, String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory(mapper);

        ObjectNode json = mapper.createObjectNode();

        if (isNotBlank(from)) {
            json.put(FROM_ADDRESS, from);
        }

        if (isNotBlank(to)) {
            json.put(TO_ADDRESS, to);
        }

        if (isNotBlank(subject)) {
            json.put(SUBJECT, subject);
        }

        if (isNotBlank(text)) {
            json.put(MESSAGE, text);
        }

        return jsonFactory.createJsonParser(json.toString());
    }


}
