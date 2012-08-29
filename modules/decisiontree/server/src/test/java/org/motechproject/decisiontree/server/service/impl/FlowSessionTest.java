package org.motechproject.decisiontree.server.service.impl;

import org.junit.Test;
import org.motechproject.decisiontree.server.domain.FlowSessionRecord;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class FlowSessionTest {

    @Test
    public void shouldBeEqualIfSessionIdsAreSame() {
        FlowSessionRecord flowSessionRecord1 = new FlowSessionRecord("1234", "1234567890");
        FlowSessionRecord flowSessionRecord2 = new FlowSessionRecord("1234", "1234567890");
        FlowSessionRecord flowSessionRecord3 = new FlowSessionRecord("4567", "1234567890");

        assertThat(flowSessionRecord1, is(equalTo(flowSessionRecord2)));
        assertThat(flowSessionRecord1, is(not(equalTo(flowSessionRecord3))));
    }

    @Test
    public void shouldHoldKeyValuePairs() {
        FlowSessionRecord flowSessionRecord1 = new FlowSessionRecord("1234", "1234567890");
        flowSessionRecord1.set("key", "value");

        assertThat("value", is(flowSessionRecord1.get("key")));
        assertNull(flowSessionRecord1.get("invalid-key"));
    }
}
