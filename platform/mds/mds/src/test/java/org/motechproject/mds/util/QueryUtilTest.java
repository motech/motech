package org.motechproject.mds.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jdo.Query;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueryUtilTest {
    private static final String SINGLE_FILTER = "abc==param0";
    private static final String DOUBLE_FILTER = "abc==param0 && def==param1";

    private static final String SINGLE_DECLARATION = "java.lang.Integer param0";
    private static final String DOUBLE_DECLARATION = "java.lang.Integer param0, java.lang.String param1";

    @Mock
    private Query query;

    @Mock
    private QueryParams queryParams;

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

    @Test
    public void shouldSetQueryParams() {
        when(queryParams.isOrderSet()).thenReturn(true);
        when(queryParams.isPagingSet()).thenReturn(true);
        when(queryParams.getOrder()).thenReturn(new Order("field", "ascending"));
        when(queryParams.getPage()).thenReturn(2);
        when(queryParams.getPageSize()).thenReturn(10);

        QueryUtil.setQueryParams(query, queryParams);

        verify(query).setRange(10, 21);
        verify(query).setOrdering("field ascending");
    }
}
