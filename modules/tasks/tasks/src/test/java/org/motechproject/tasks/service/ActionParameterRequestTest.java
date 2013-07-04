package org.motechproject.tasks.service;

import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.ParameterType;

import java.lang.reflect.Type;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class ActionParameterRequestTest {

    @Test
    public void shouldSetDefaultParameterType() {
        assertThat(new ActionParameterRequest("key", "displayName", 1).getType(), is(ParameterType.UNICODE.getValue()));
    }

    @Test
    public void shouldTestEquality() {
        assertThat(new ActionParameterRequest("times-reminders-to-be-sent", "pillreminder.total.times.sent", 3, "INTEGER"),
                is(new ActionParameterRequest("times-reminders-to-be-sent", "pillreminder.total.times.sent", 3, "INTEGER")));

        assertThat(new ActionParameterRequest("times-reminders-to-be-sery", "pillreminder.total.times.sent-2", 4, "INTEGER"),
               not(new ActionParameterRequest("times-reminders-to-be-sent", "pillreminder.total.times.sent-8", 3, "UNICODE")));
    }
}
