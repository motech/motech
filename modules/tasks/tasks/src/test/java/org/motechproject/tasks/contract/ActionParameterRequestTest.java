package org.motechproject.tasks.contract;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class ActionParameterRequestTest {

    private static final String UNICODE = "UNICODE";

    @Test
    public void shouldSetDefaultParameterType() {
        assertThat(new ActionParameterRequestBuilder().setKey("key").setDisplayName("displayName").setOrder(1).createActionParameterRequest().getType(), Is.is(UNICODE));
    }

    @Test
    public void shouldTestEquality() {
        assertThat(new ActionParameterRequestBuilder().setKey("times-reminders-to-be-sent")
                        .setDisplayName("pillreminder.total.times.sent").setOrder(3).setType("INTEGER")
                        .createActionParameterRequest(),
                is(new ActionParameterRequestBuilder().setKey("times-reminders-to-be-sent")
                        .setDisplayName("pillreminder.total.times.sent").setOrder(3).setType("INTEGER")
                        .createActionParameterRequest()));

        assertThat(new ActionParameterRequestBuilder().setKey("times-reminders-to-be-sery")
                        .setDisplayName("pillreminder.total.times.sent-2").setOrder(4).setType("INTEGER")
                        .createActionParameterRequest(),
               not(new ActionParameterRequestBuilder().setKey("times-reminders-to-be-sent")
                       .setDisplayName("pillreminder.total.times.sent-8").setOrder(3).setType("UNICODE")
                       .createActionParameterRequest()));
    }
}
