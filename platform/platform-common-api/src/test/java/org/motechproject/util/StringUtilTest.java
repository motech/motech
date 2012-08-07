package org.motechproject.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilTest {
    @Test
    public void shouldReturnTrueIfStringIsNull() {
        assertEquals(true, StringUtil.isNullOrEmpty(null));
    }

    @Test
    public void shouldReturnTrueIfStringIsEmpty() {
        assertEquals(true, StringUtil.isNullOrEmpty(""));
    }

    @Test
    public void shouldReturnFalseIfStringIsNotNullOrEmpty() {
        assertEquals(false, StringUtil.isNullOrEmpty("str"));
    }
}
