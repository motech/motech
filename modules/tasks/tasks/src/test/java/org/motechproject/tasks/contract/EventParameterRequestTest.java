package org.motechproject.tasks.contract;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EventParameterRequestTest {

    private static final String UNICODE = "UNICODE";

    @Test
    public void shouldSetUnicodeAsDefaultType() {
        assertThat(new EventParameterRequest("bar", "foo").getType(), Is.is(UNICODE));
        assertThat(new EventParameterRequest("bar", "foo", "blah").getType(), is("blah"));
    }

}
