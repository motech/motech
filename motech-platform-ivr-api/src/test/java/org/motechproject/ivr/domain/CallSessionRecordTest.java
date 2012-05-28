package org.motechproject.ivr.domain;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class CallSessionRecordTest {

    @Test
    public void shouldBeEqualIfSessionIdsAreSame() {
        CallSessionRecord callSessionRecord1 = new CallSessionRecord("1234");
        CallSessionRecord callSessionRecord2 = new CallSessionRecord("1234");
        CallSessionRecord callSessionRecord3 = new CallSessionRecord("4567");

        assertThat(callSessionRecord1, is(equalTo(callSessionRecord2)));
        assertThat(callSessionRecord1, is(not(equalTo(callSessionRecord3))));
    }

    @Test
    public void shouldHoldKeyValuePairs() {
        CallSessionRecord callSessionRecord1 = new CallSessionRecord("1234");
        callSessionRecord1.add("key", "value");

        assertThat("value", is(callSessionRecord1.valueFor("key")));
        assertNull(callSessionRecord1.valueFor("invalid-key"));
    }
}
