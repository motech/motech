package org.motechproject.tasks.service;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.tasks.service.HandlerUtil.ADDITIONAL_DATA_PREFIX;
import static org.motechproject.tasks.service.HandlerUtil.TRIGGER_PREFIX;

public class KeyInformationTest {
    private static final String KEY_VALUE = "key";
    private static final String DATA_PROVIDER_ID = "12345";
    private static final String OBJECT_TYPE = "Test";
    private static final Long OBJECT_ID = 1L;

    @Test
    public void shouldGetInformationFromTriggerKey() {
        String original = String.format("%s.%s", TRIGGER_PREFIX, KEY_VALUE);
        KeyInformation key = new KeyInformation(original);

        assertTrue(key.fromTrigger());
        assertFalse(key.fromAdditionalData());

        assertEquals(KEY_VALUE, key.getEventKey());
        assertEquals(original, key.getOriginalKey());

        assertNull(key.getObjectId());
        assertNull(key.getObjectType());
        assertNull(key.getDataProviderId());
        assertNull(key.getManipulations());
    }

    @Test
    public void shouldGetInformationFromTriggerKeyWithManipulations() {
        String original = String.format("%s.%s?toupper?join(-)", TRIGGER_PREFIX, KEY_VALUE);
        KeyInformation key = new KeyInformation(original);

        assertTrue(key.fromTrigger());
        assertFalse(key.fromAdditionalData());

        assertEquals(KEY_VALUE, key.getEventKey());
        assertEquals(original, key.getOriginalKey());

        assertNull(key.getObjectId());
        assertNull(key.getObjectType());
        assertNull(key.getDataProviderId());

        assertNotNull(key.getManipulations());

        List<String> manipulations = key.getManipulations();

        assertEquals(2, manipulations.size());
        assertEquals("toupper", manipulations.get(0));
        assertEquals("join(-)", manipulations.get(1));
    }

    @Test
    public void shouldGetInformationFromAdditionalDataKey() {
        String original = String.format("%s.%s.%s#%d.%s", ADDITIONAL_DATA_PREFIX, DATA_PROVIDER_ID, OBJECT_TYPE, OBJECT_ID, KEY_VALUE);
        KeyInformation key = new KeyInformation(original);

        assertTrue(key.fromAdditionalData());
        assertFalse(key.fromTrigger());

        assertEquals(DATA_PROVIDER_ID, key.getDataProviderId());
        assertEquals(OBJECT_TYPE, key.getObjectType());
        assertEquals(OBJECT_ID, key.getObjectId());
        assertEquals(KEY_VALUE, key.getEventKey());
        assertEquals(original, key.getOriginalKey());

        assertNull(key.getManipulations());
    }

    @Test
    public void shouldGetInformationFromAdditionalDataKeyWithManipulations() {
        String original = String.format("%s.%s.%s#%d.%s?toupper?join(-)", ADDITIONAL_DATA_PREFIX, DATA_PROVIDER_ID, OBJECT_TYPE, OBJECT_ID, KEY_VALUE);
        KeyInformation key = new KeyInformation(original);

        assertTrue(key.fromAdditionalData());
        assertFalse(key.fromTrigger());

        assertEquals(DATA_PROVIDER_ID, key.getDataProviderId());
        assertEquals(OBJECT_TYPE, key.getObjectType());
        assertEquals(OBJECT_ID, key.getObjectId());
        assertEquals(KEY_VALUE, key.getEventKey());
        assertEquals(original, key.getOriginalKey());

        assertNotNull(key.getManipulations());

        List<String> manipulations = key.getManipulations();

        assertEquals(2, manipulations.size());
        assertEquals("toupper", manipulations.get(0));
        assertEquals("join(-)", manipulations.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenKeyIsFromUnknownSource() {
        new KeyInformation("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenAdditionalDataKeyHasIncorrectFormat() {
        String original = String.format("%s.%s.%s#.%s?toupper?join(-)", ADDITIONAL_DATA_PREFIX, DATA_PROVIDER_ID, OBJECT_TYPE, KEY_VALUE);
        new KeyInformation(original);
    }

}
