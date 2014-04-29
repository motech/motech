package org.motechproject.commons.api;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class MotechEnumUtilsTest {
    private enum FooBar { FOO, BAR, BAZ, BAT }
    private static Set<String> strings;
    private static Set<FooBar> enums;

    private static final Set<String> STRING_SET = new HashSet<String>(Arrays.asList("FOO", "BAR", "BAZ", "BAT"));
    private static final Set<FooBar> ENUM_SET = new HashSet<FooBar>(Arrays.asList(FooBar.FOO, FooBar.BAR, FooBar.BAZ,
            FooBar.BAT));
    private static final Set<String> EMPTY_STRING_SET = new HashSet<String>();
    private static final Set<FooBar> EMPTY_ENUM_SET = new HashSet<FooBar>();


    @BeforeClass
    public static void setUp() throws Exception {
        strings = new HashSet<>();
        strings.add("FOO");
        strings.add("BAR");
        strings.add("BAZ");
        strings.add("BAT");

        enums = new HashSet<>();
        enums.add(FooBar.FOO);
        enums.add(FooBar.BAR);
        enums.add(FooBar.BAZ);
        enums.add(FooBar.BAT);
    }

    @Test
    public void shouldReturnValidStringSet() throws Exception {
        Set<String> actual;
        actual = MotechEnumUtils.toStringSet(ENUM_SET);

        assertEquals(actual, STRING_SET);
    }

    @Test
    public void shouldReturnValidEnumSet() throws Exception {
        Set<FooBar> actual;
        actual = MotechEnumUtils.toEnumSet(FooBar.class, STRING_SET);

        assertEquals(actual, ENUM_SET);
    }

    @Test
    public void shouldReturnEmptyEnumSet() {
        Set<FooBar> actual;

        actual = MotechEnumUtils.toEnumSet(FooBar.class, EMPTY_STRING_SET);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void shouldReturnEmptyStringSet() {
        Set<String> actual;

        actual = MotechEnumUtils.toStringSet(EMPTY_ENUM_SET);

        assertTrue(actual.isEmpty());
    }
}
