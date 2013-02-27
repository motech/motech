package org.motechproject.tasks.service;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.motechproject.event.listener.annotations.MotechListenerAbstractProxy;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.event.listener.annotations.MotechListenerNamedParametersProxy;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HandlerPredicateTest {

    @Test
    public void shouldFoundCorrectObject() {
        MotechListenerEventProxy expected = new MotechListenerEventProxy("def", null, null);

        List<MotechListenerAbstractProxy> list = new ArrayList<>();
        list.add(new MotechListenerNamedParametersProxy("abc", null, null));
        list.add(expected);
        list.add(new MotechListenerEventProxy("ghi", null, null));

        MotechListenerAbstractProxy actual = (MotechListenerAbstractProxy) CollectionUtils.find(list, new HandlerPredicate("def"));

        assertTrue(actual instanceof MotechListenerEventProxy);
        assertEquals(expected.getIdentifier(), actual.getIdentifier());
    }

    @Test
    public void shouldNotFoundCorrectObject() {
        List<MotechListenerAbstractProxy> list = new ArrayList<>();
        list.add(new MotechListenerNamedParametersProxy("abc", null, null));
        list.add(new MotechListenerEventProxy("def", null, null));
        list.add(new MotechListenerEventProxy("ghi", null, null));

        MotechListenerAbstractProxy actual = (MotechListenerAbstractProxy) CollectionUtils.find(list, new HandlerPredicate("abc"));

        assertNull(actual);
    }
}
