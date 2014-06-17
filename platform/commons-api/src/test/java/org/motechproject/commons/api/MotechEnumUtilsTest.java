package org.motechproject.commons.api;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MotechEnumUtilsTest {
    private enum FooBar { FOO, BAR, BAZ, BAT }

    private static final Set<String> STRING_SET = new HashSet<>(Arrays.asList("FOO", "BAR", "BAZ", "BAT"));
    private static final Set<String> STRING_SET2 = new HashSet<>(Arrays.asList("foo", "baR", "bAZ", "BAT"));
    private static final Set<String> STRING_SET3 = new HashSet<>(Arrays.asList("BAR", "BAZ", "FOO", "BAT"));
    private static final Set<String> STRING_SET4 = new HashSet<>(Arrays.asList("FOO", "BAR", "BAZ", "BAT",
            "BAT"));
    private static final Set<FooBar> ENUM_SET = new HashSet<>(Arrays.asList(FooBar.FOO, FooBar.BAR, FooBar.BAZ,
            FooBar.BAT));
    private static final String CSV_STRING = "FOO,BAR,BAZ,BAT";
    private static final String CSV_STRING2 = "foo,baR,bAZ,BAT";
    private static final Set<String> EMPTY_STRING_SET = new HashSet<>();
    private static final Set<FooBar> EMPTY_ENUM_SET = new HashSet<>();
    private static final String EMPTY_STRING = "";


    @Test
    public void shouldReturnValidStringSet() throws Exception {
        Set<String> actual;
        actual = MotechEnumUtils.toStringSet(ENUM_SET);

        assertEquals(actual, STRING_SET);
    }

    @Test
    public void shouldReturnValidString() throws Exception {
        String actual;
        actual = MotechEnumUtils.toString(ENUM_SET);

        // We can't guarantee the order in which the string is built, so transform it into a set and compare sets
        Set<String> actualSet = new HashSet<>(Arrays.asList(actual.split(",")));

        assertEquals(actualSet, STRING_SET);
    }

    @Test
    public void shouldReturnValidEnumSetFromStringSet() throws Exception {
        Set<FooBar> actual;
        actual = MotechEnumUtils.toEnumSet(FooBar.class, STRING_SET);
        assertEquals(actual, ENUM_SET);

        actual = MotechEnumUtils.toEnumSet(FooBar.class, STRING_SET2);
        assertEquals(actual, ENUM_SET);

        actual = MotechEnumUtils.toEnumSet(FooBar.class, STRING_SET3);
        assertEquals(actual, ENUM_SET);

        actual = MotechEnumUtils.toEnumSet(FooBar.class, STRING_SET4);
        assertEquals(actual, ENUM_SET);
    }

    @Test
    public void shouldReturnValidEnumSetFromCsvString() throws Exception {
        Set<FooBar> actual;

        actual = MotechEnumUtils.toEnumSet(FooBar.class, CSV_STRING);
        assertEquals(actual, ENUM_SET);

        actual = MotechEnumUtils.toEnumSet(FooBar.class, CSV_STRING2);
        assertEquals(actual, ENUM_SET);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenGivenInvalidEnumStringVal() throws Exception {
        Set<String> invalidStringSet = new HashSet<>(STRING_SET);

        invalidStringSet.add("INVALID_ENUM_STRING_VALUE");
        MotechEnumUtils.toEnumSet(FooBar.class, invalidStringSet);
    }

    @Test
    public void shouldReturnEmptyEnumSet() {
        Set<FooBar> actual;

        actual = MotechEnumUtils.toEnumSet(FooBar.class, EMPTY_STRING_SET);
        assertTrue(actual.isEmpty());

        actual = MotechEnumUtils.toEnumSet(FooBar.class, EMPTY_STRING);
        assertTrue(actual.isEmpty());

        actual = MotechEnumUtils.toEnumSet(FooBar.class, (String) null);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void shouldReturnEmptyStringSet() {
        Set<String> actual;

        actual = MotechEnumUtils.toStringSet(EMPTY_ENUM_SET);
        assertTrue(actual.isEmpty());
    }
}
