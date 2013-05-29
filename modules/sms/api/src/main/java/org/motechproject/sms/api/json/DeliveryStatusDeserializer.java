package org.motechproject.sms.api.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.motechproject.sms.api.DeliveryStatus;

import java.io.IOException;

import static org.motechproject.sms.api.DeliveryStatus.DELIVERY_CONFIRMED;
import static org.motechproject.sms.api.DeliveryStatus.DISPATCHED;

public class DeliveryStatusDeserializer extends JsonDeserializer<DeliveryStatus> {

    @Override
    public DeliveryStatus deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String statusAsString = parser.getText();
        DeliveryStatus status = DeliveryStatus.valueOf(statusAsString);
        DeliveryStatus result;

        switch (status) {
            case INPROGRESS:
                result = DISPATCHED;
                break;
            case DELIVERED:
                result = DELIVERY_CONFIRMED;
                break;
            default:
                result = status;
        }

        return result;
    }
}
