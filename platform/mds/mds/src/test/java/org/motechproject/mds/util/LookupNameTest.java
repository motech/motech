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
}
