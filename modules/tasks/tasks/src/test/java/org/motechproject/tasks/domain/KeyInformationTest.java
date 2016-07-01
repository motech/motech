package org.motechproject.tasks.domain;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.tasks.domain.KeyInformation.ADDITIONAL_DATA_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.POST_ACTION_PARAMETER_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.TRIGGER_PREFIX;

public class KeyInformationTest {
    private static final String KEY_VALUE = "key";
    private static final String DATA_PROVIDER_NAME = "data-services";
    private static final String OBJECT_TYPE = "Test";
    private static final String OBJECT_TYPE_2 = "Test_Data";
    private static final Long OBJECT_ID = 1L;

    @Test
    public void shouldGetInformationFromTriggerKey() {
        String original = String.format("%s.%s", TRIGGER_PREFIX, KEY_VALUE);
        KeyInformation key = KeyInformation.parse(original);

        assertKeyFromTrigger(original, key);
    }

    @Test
    public void shouldGetInformationFromTriggerKeyWithManipulations() {
        String original = String.format("%s.%s?toupper?join(-)", TRIGGER_PREFIX, KEY_VALUE);
        KeyInformation key = KeyInformation.parse(original);

        assertKeyFromTrigger(original, key);
    }

    @Test
    public void shouldGetInformationFromAdditionalDataKey() {
        String original = String.format("%s.%s.%s#%d.%s", ADDITIONAL_DATA_PREFIX, DATA_PROVIDER_NAME, OBJECT_TYPE, OBJECT_ID, KEY_VALUE);
        KeyInformation key = KeyInformation.parse(original);

        assertKeyFromAdditionalData(original, key);

        original = String.format("%s.%s.%s#%d.%s", ADDITIONAL_DATA_PREFIX, DATA_PROVIDER_NAME, OBJECT_TYPE_2, OBJECT_ID, KEY_VALUE);
        key = KeyInformation.parse(original);

        assertKeyFromAdditionalData(original, key, OBJECT_TYPE_2);
    }

    @Test
    public void shouldGetInformationFromAdditionalDataKeyWithManipulations() {
        String original = String.format("%s.%s.%s#%d.%s?toupper?join(-)", ADDITIONAL_DATA_PREFIX, DATA_PROVIDER_NAME, OBJECT_TYPE, OBJECT_ID, KEY_VALUE);
        KeyInformation key = KeyInformation.parse(original);

        assertKeyFromAdditionalData(original, key);

        original = String.format("%s.%s.%s#%d.%s?toupper?join(-)", ADDITIONAL_DATA_PREFIX, DATA_PROVIDER_NAME, OBJECT_TYPE_2, OBJECT_ID, KEY_VALUE);
        key = KeyInformation.parse(original);
        assertKeyFromAdditionalData(original, key, OBJECT_TYPE_2);
    }

    @Test
    public void shouldGetInformationFromPostActionParameterKey() {
        String original = String.format("%s.%s.%s", POST_ACTION_PARAMETER_PREFIX, OBJECT_ID, KEY_VALUE);
        KeyInformation key = KeyInformation.parse(original);

        assertKeyFromPostActionParameter(original, key);
    }

    @Test
    public void shouldFindAllKeysInString() {
        String trigger = String.format("%s.%s?toupper?join(-)", TRIGGER_PREFIX, KEY_VALUE);
        String additionalData = String.format("%s.%s.%s#%d.%s?toupper?join(-)", ADDITIONAL_DATA_PREFIX, DATA_PROVIDER_NAME, OBJECT_TYPE, OBJECT_ID, KEY_VALUE);
        String original = String.format("Trigger: {{%s}} Additional data: {{%s}}", trigger, additionalData);

        List<KeyInformation> keys = KeyInformation.parseAll(original);

        assertEquals(2, keys.size());
        assertKeyFromTrigger(trigger, keys.get(0));
        assertKeyFromAdditionalData(additionalData, keys.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenKeyIsFromUnknownSource() {
        KeyInformation.parse("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenAdditionalDataKeyHasIncorrectFormat() {
        String original = String.format("%s.%s.%s#.%s?toupper?join(-)", ADDITIONAL_DATA_PREFIX, DATA_PROVIDER_NAME, OBJECT_TYPE, KEY_VALUE);
        KeyInformation.parse(original);
    }

    private void assertManipulations(KeyInformation key) {
        assertNotNull(key.getManipulations());

        if (!key.getManipulations().isEmpty()) {
            List<String> manipulations = key.getManipulations();

            assertEquals(2, manipulations.size());
            assertEquals("toupper", manipulations.get(0));
            assertEquals("join(-)", manipulations.get(1));
        }
    }

    private void assertKeyFromPostActionParameter(String original, KeyInformation postActionParameterKey) {
        assertTrue(postActionParameterKey.fromPostActionParameter());
        assertFalse(postActionParameterKey.fromAdditionalData());
        assertFalse(postActionParameterKey.fromTrigger());

        assertEquals(KEY_VALUE, postActionParameterKey.getKey());
        assertEquals(original, postActionParameterKey.getOriginalKey());
    }

    private void assertKeyFromTrigger(String original, KeyInformation triggerKey) {
        assertTrue(triggerKey.fromTrigger());
        assertFalse(triggerKey.fromAdditionalData());

        assertEquals(KEY_VALUE, triggerKey.getKey());
        assertEquals(original, triggerKey.getOriginalKey());

        assertNull(triggerKey.getObjectId());
        assertNull(triggerKey.getObjectType());
        assertNull(triggerKey.getDataProviderName());

        assertManipulations(triggerKey);
    }

    private void assertKeyFromAdditionalData(String original, KeyInformation additionalDataKey) {
        assertKeyFromAdditionalData(original, additionalDataKey, OBJECT_TYPE);
    }

    private void assertKeyFromAdditionalData(String original, KeyInformation additionalDataKey, String objectType) {
        assertTrue(additionalDataKey.fromAdditionalData());
        assertFalse(additionalDataKey.fromTrigger());

        assertEquals(DATA_PROVIDER_NAME, additionalDataKey.getDataProviderName());
        assertEquals(objectType, additionalDataKey.getObjectType());
        assertEquals(OBJECT_ID, additionalDataKey.getObjectId());
        assertEquals(KEY_VALUE, additionalDataKey.getKey());
        assertEquals(original, additionalDataKey.getOriginalKey());

        assertManipulations(additionalDataKey);
    }

}
