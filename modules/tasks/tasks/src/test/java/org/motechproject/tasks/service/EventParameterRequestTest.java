package org.motechproject.tasks.service;

import org.junit.Test;
import org.motechproject.tasks.domain.ParameterType;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EventParameterRequestTest {

    @Test
    public void shouldSetUnicodeAsDefaultType() {
        assertThat(new EventParameterRequest("bar", "foo").getType(), is(ParameterType.UNICODE.getValue()));
        assertThat(new EventParameterRequest("bar", "foo", "blah").getType(), is("blah"));
    }

}
