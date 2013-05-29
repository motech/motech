package org.motechproject.sms.api.json;

import org.codehaus.jackson.JsonParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.motechproject.sms.api.DeliveryStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.sms.api.DeliveryStatus.ABORTED;
import static org.motechproject.sms.api.DeliveryStatus.DELIVERED;
import static org.motechproject.sms.api.DeliveryStatus.DELIVERY_CONFIRMED;
import static org.motechproject.sms.api.DeliveryStatus.DISPATCHED;
import static org.motechproject.sms.api.DeliveryStatus.INPROGRESS;
import static org.motechproject.sms.api.DeliveryStatus.KEEPTRYING;
import static org.motechproject.sms.api.DeliveryStatus.PENDING;
import static org.motechproject.sms.api.DeliveryStatus.RECEIVED;
import static org.motechproject.sms.api.DeliveryStatus.UNKNOWN;

@RunWith(Parameterized.class)
public class DeliveryStatusDeserializerTest {
    private DeliveryStatus expectedStatus;
    private JsonParser jsonParser;

    public DeliveryStatusDeserializerTest(DeliveryStatus expectedStatus, DeliveryStatus given) throws IOException {
        this.expectedStatus = expectedStatus;
        this.jsonParser = mock(JsonParser.class);

        when(jsonParser.getText()).thenReturn(given.name());
    }

    @Parameters
    public static Collection<Object[]> testParameters() {
        return Arrays.asList(new Object[][]{
                {UNKNOWN, UNKNOWN},
                {DISPATCHED, INPROGRESS},
                {DELIVERY_CONFIRMED, DELIVERED},
                {KEEPTRYING, KEEPTRYING},
                {ABORTED, ABORTED},
                {PENDING, PENDING},
                {RECEIVED, RECEIVED},
                {DISPATCHED, DISPATCHED},
                {DELIVERY_CONFIRMED, DELIVERY_CONFIRMED}
        });
    }

    @Test
    public void shouldDeserializeJson() throws IOException {
        assertEquals(expectedStatus, new DeliveryStatusDeserializer().deserialize(jsonParser, null));
    }
}
