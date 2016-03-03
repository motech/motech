package org.motechproject.commons.api;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ClassUtilsTest {
    private static List<String> strings;

    @BeforeClass
    public static void setUp() throws Exception {
        strings = new ArrayList<>(5);
        strings.add("motech 0.14");
        strings.add("motech 0.15");
        strings.add("motech 0.16");
        strings.add("motech 0.17");
        strings.add("motech 0.18");
    }

    @Test
    public void shouldCastObjects() throws Exception {
        List<String> actual = ClassUtils.filterByClass(String.class, new Vector(strings).elements());

        assertEquals(strings.size(), actual.size());
        assertEquals(strings, actual);

        List list = new ArrayList(strings);
        list.add(5);
        list.add(6);
        list.add(7);

        actual = ClassUtils.filterByClass(String.class, new Vector(list).elements());

        assertEquals(strings.size(), actual.size());
        assertEquals(strings, actual);
    }

    @Test
    public void shouldReturnEmptyList() {
        assertTrue(ClassUtils.filterByClass(Integer.class, null).isEmpty());
        assertTrue(ClassUtils.filterByClass(Integer.class, new Vector().elements()).isEmpty());
        assertTrue(ClassUtils.filterByClass(Integer.class, new Vector(strings).elements()).isEmpty());
    }

}
