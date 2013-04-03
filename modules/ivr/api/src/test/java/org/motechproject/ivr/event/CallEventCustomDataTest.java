package org.motechproject.ivr.event;

import org.junit.Test;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

public class CallEventCustomDataTest {
    @Test
    public void add() {
        CallEventCustomData callEventCustomData = new CallEventCustomData();
        String key1 = "key1";
        String key2 = "key2";
        callEventCustomData.put(key1, "bar");
        callEventCustomData.put(key1, "bar");
        callEventCustomData.put(key2, "baz");
        assertEquals(asList("bar", "bar"), callEventCustomData.getAll(key1));
        assertEquals(asList("baz"), callEventCustomData.getAll(key2));
    }

    @Test
    public void getFirst() {
        CallEventCustomData callEventCustomData = new CallEventCustomData();
        assertEquals(null, callEventCustomData.getFirst("foo"));
    }

    @Test
    public void update() {
        CallEventCustomData callEventCustomData = new CallEventCustomData();
        String key = "foo";
        callEventCustomData.put(key, "bar");
        callEventCustomData.update(key, "baz");
        assertEquals("baz", callEventCustomData.getFirst(key));
    }
}
