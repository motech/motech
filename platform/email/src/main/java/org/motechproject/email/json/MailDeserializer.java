package org.motechproject.email.json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.motechproject.email.domain.Mail;

import java.io.IOException;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.email.constants.SendEmailConstants.FROM_ADDRESS;
import static org.motechproject.email.constants.SendEmailConstants.MESSAGE;
import static org.motechproject.email.constants.SendEmailConstants.SUBJECT;
import static org.motechproject.email.constants.SendEmailConstants.TO_ADDRESS;

public class MailDeserializer extends JsonDeserializer<Mail> {
    private JsonNode jsonNode;

    @Override
    public Mail deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        jsonNode = jsonParser.readValueAsTree();

        return new Mail(
                getValue(FROM_ADDRESS, true), getValue(TO_ADDRESS, true),
                getValue(SUBJECT, false), getValue(MESSAGE, false)
        );
    }

    private String getValue(String key, boolean required) throws JsonMappingException {
        String value = null;

        if (jsonNode.has(key)) {
            value = jsonNode.get(key).getTextValue();
        }

        if (required && isBlank(value)) {
            throw new JsonMappingException(String.format("Property %s is required", key));
        }

        return value;
    }
}
