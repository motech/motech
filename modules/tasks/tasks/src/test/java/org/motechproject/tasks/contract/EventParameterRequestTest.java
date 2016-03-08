package org.motechproject.tasks.contract;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EventParameterRequestTest {

    private static final String UNICODE = "UNICODE";

    @Test
    public void shouldSetGivenType() {

        String type = "fooType";
        EventParameterRequest request = new EventParameterRequest("bar", "foo", type);

        assertThat(request.getType(), is(type));
    }

    @Test
    public void shouldSetUnicodeAsDefaultType() {

        EventParameterRequest request = new EventParameterRequest("bar", "foo");

        assertThat(request.getType(), is(UNICODE));
    }

}
