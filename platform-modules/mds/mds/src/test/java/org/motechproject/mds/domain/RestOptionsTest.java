package org.motechproject.mds.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RestOptionsTest {
    private static List<Lookup> expectedLookups;
    private static List<Field> expectedFields;
    private static RestOptions restOptions;

    @BeforeClass
    public static void classSetUp() throws Exception {
        Field field1 = createField(true);
        Field field2 = createField(false);
        Field field3 = createField(true);
        Field field4 = createField(false);

        expectedFields = new ArrayList<>();
        Collections.addAll(expectedFields, field1, field3);

        Lookup lookup1 = createLookup(true);
        Lookup lookup2 = createLookup(false);
        Lookup lookup3 = createLookup(true);
        Lookup lookup4 = createLookup(false);

        expectedLookups = new ArrayList<>();
        Collections.addAll(expectedLookups, lookup1, lookup3);

        Entity entity = new Entity();
        Collections.addAll(entity.getFields(), field1, field2, field3, field4);
        Collections.addAll(entity.getLookups(), lookup1, lookup2, lookup3, lookup4);

        restOptions = new RestOptions(entity);
    }

    @Test
    public void testGetFields() throws Exception {
        assertEquals(expectedFields, restOptions.getFields());
    }

    @Test
    public void testGetLookups() throws Exception {
        assertEquals(expectedLookups, restOptions.getLookups());
    }

    private static Field createField(boolean exposedViaRest) {
        Field field = new Field();
        field.setExposedViaRest(exposedViaRest);

        return field;
    }

    private static Lookup createLookup(boolean exposedViaRest) {
        Lookup lookup = new Lookup();
        lookup.setExposedViaRest(exposedViaRest);

        return lookup;
    }
}
