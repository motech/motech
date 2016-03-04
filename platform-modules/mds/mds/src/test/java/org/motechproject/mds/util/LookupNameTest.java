package org.motechproject.mds.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LookupNameTest {

    @Test
    public void shouldCreateLookupMethodNames() {
        assertEquals("sampleLookupMethod", LookupName.lookupMethod("Sample lookup Method"));
        assertEquals("sampleLookupMethod", LookupName.lookupMethod("SampleLookup Method"));
        assertEquals("sampleLookupMethod", LookupName.lookupMethod("sample lookup method"));
        assertEquals("sample", LookupName.lookupMethod("Sample"));
        assertEquals("sample", LookupName.lookupMethod("sample"));
    }

    @Test
    public void shouldCreateCountLookupMethodNames() {
        assertEquals("countSampleLookupMethod", LookupName.lookupCountMethod("Sample lookup Method"));
        assertEquals("countSampleLookupMethod", LookupName.lookupCountMethod("SampleLookup Method"));
        assertEquals("countSampleLookupMethod", LookupName.lookupCountMethod("sample lookup method"));
        assertEquals("countSample", LookupName.lookupCountMethod("Sample"));
        assertEquals("countSample", LookupName.lookupCountMethod("sample"));
    }

    @Test
    public void shouldReturnRelatedName() {
        assertEquals("id", LookupName.getRelatedFieldName("fieldName.id"));
        assertEquals("name", LookupName.getRelatedFieldName("collection.name"));
        assertEquals(null, LookupName.getRelatedFieldName("field"));
    }

    @Test
    public void shouldReturnFieldName() {
        assertEquals("fieldName", LookupName.getFieldName("fieldName.id"));
        assertEquals("collection", LookupName.getFieldName("collection.name"));
        assertEquals("field", LookupName.getFieldName("field"));
    }

    @Test
    public void shouldReturnLookupFieldName() {
        assertEquals("fieldName.id", LookupName.buildLookupFieldName("fieldName", "id"));
        assertEquals("collection.name", LookupName.buildLookupFieldName("collection", "name"));
        assertEquals("field", LookupName.buildLookupFieldName("field", null));
    }
}
