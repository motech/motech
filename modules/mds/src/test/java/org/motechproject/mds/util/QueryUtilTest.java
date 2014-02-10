package org.motechproject.mds.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryUtilTest {
    private static final String SINGLE_FILTER = "abc==param0";
    private static final String DOUBLE_FILTER = "abc==param0 && def==param1";

    private static final String SINGLE_DECLARATION = "java.lang.Integer param0";
    private static final String DOUBLE_DECLARATION = "java.lang.Integer param0, java.lang.String param1";

    @Test
    public void testCreateFilter() throws Exception {
        String actual = QueryUtil.createFilter(new String[]{"abc"});
        assertEquals(SINGLE_FILTER, actual);

        actual = QueryUtil.createFilter(new String[]{"abc", "def"});
        assertEquals(DOUBLE_FILTER, actual);
    }

    @Test
    public void testCreateDeclareParameters() throws Exception {
        String actual = QueryUtil.createDeclareParameters(new Object[]{10});
        assertEquals(SINGLE_DECLARATION, actual);

        actual = QueryUtil.createDeclareParameters(new Object[]{10, "str"});
        assertEquals(DOUBLE_DECLARATION, actual);
    }
}
