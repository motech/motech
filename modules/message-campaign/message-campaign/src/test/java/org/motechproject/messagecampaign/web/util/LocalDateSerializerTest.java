package org.motechproject.messagecampaign.web.util;

import org.codehaus.jackson.JsonGenerator;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.verify;

public class LocalDateSerializerTest {

    @Mock
    private JsonGenerator jsonGenerator;

    private LocalDateSerializer serializer = new LocalDateSerializer();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSerialization() throws IOException {
        LocalDate localDate = new LocalDate(2013, 10, 9);
        serializer.serialize(localDate, jsonGenerator, null);
        verify(jsonGenerator).writeString("2013-10-09");

        localDate = new LocalDate(2012, 6, 17);
        serializer.serialize(localDate, jsonGenerator, null);
        verify(jsonGenerator).writeString("2012-06-17");
    }
}
