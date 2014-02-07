package org.motechproject.mds.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RestOptionsMappingTest {
    private static List<LookupMapping> expectedLookups;
    private static List<FieldMapping> expectedFields;
    private static RestOptionsMapping restOptions;

    @BeforeClass
    public static void classSetUp() throws Exception {
        FieldMapping field1 = createField(true);
        FieldMapping field2 = createField(false);
        FieldMapping field3 = createField(true);
        FieldMapping field4 = createField(false);

        expectedFields = new ArrayList<>();
        Collections.addAll(expectedFields, field1, field3);

        LookupMapping lookup1 = createLookup(true);
        LookupMapping lookup2 = createLookup(false);
        LookupMapping lookup3 = createLookup(true);
        LookupMapping lookup4 = createLookup(false);

        expectedLookups = new ArrayList<>();
        Collections.addAll(expectedLookups, lookup1, lookup3);

        EntityMapping entity = new EntityMapping();
        Collections.addAll(entity.getFields(), field1, field2, field3, field4);
        Collections.addAll(entity.getLookups(), lookup1, lookup2, lookup3, lookup4);

        restOptions = new RestOptionsMapping(entity);
    }

    @Test
    public void testGetFields() throws Exception {
        assertEquals(expectedFields, restOptions.getFields());
    }

    @Test
    public void testGetLookups() throws Exception {
        assertEquals(expectedLookups, restOptions.getLookups());
    }

    private static FieldMapping createField(boolean exposedViaRest) {
        FieldMapping mapping = new FieldMapping();
        mapping.setExposedViaRest(exposedViaRest);

        return mapping;
    }

    private static LookupMapping createLookup(boolean exposedViaRest) {
        LookupMapping mapping = new LookupMapping();
        mapping.setExposedViaRest(exposedViaRest);

        return mapping;
    }
}
