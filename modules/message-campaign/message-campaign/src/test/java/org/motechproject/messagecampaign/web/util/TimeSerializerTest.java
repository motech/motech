package org.motechproject.messagecampaign.web.util;

import org.codehaus.jackson.JsonGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.commons.date.model.Time;

import java.io.IOException;

import static org.mockito.Mockito.verify;

public class TimeSerializerTest {

    @Mock
    private JsonGenerator jsonGenerator;

    private TimeSerializer serializer = new TimeSerializer();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSerialization() throws IOException {
        Time time = new Time(23, 59);
        serializer.serialize(time, jsonGenerator, null);
        verify(jsonGenerator).writeString("23:59:00");

        time = new Time(9, 7);
        serializer.serialize(time, jsonGenerator, null);
        verify(jsonGenerator).writeString("09:07:00");
    }
}
