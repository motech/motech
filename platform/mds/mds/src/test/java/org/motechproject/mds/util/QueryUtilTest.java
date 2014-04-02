package org.motechproject.mds.util;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.api.Range;

import javax.jdo.Query;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueryUtilTest {
    private static final String SINGLE_FILTER = "abc==param0";
    private static final String DOUBLE_FILTER = "abc==param0 && def==param1";
    private static final String OWNER_FILTER = " && owner==param2";
    private static final String CREATOR_FILTER = " && creator==param2";

    private static final String SINGLE_DECLARATION = "java.lang.Integer param0";
    private static final String DOUBLE_DECLARATION = "java.lang.Integer param0, java.lang.String param1";
    private static final String RESTRICTION_DECLARATION = ", java.lang.String param2";

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
    public void testCreateFiltersWithRestriction() {
        String[] params = {"abc", "def"};

        InstanceSecurityRestriction restriction = new InstanceSecurityRestriction();
        restriction.setByOwner(true);

        String filter = QueryUtil.createFilter(params, null, restriction);
        assertEquals(DOUBLE_FILTER + OWNER_FILTER, filter);

        restriction.setByCreator(true);
        restriction.setByOwner(false);

        filter = QueryUtil.createFilter(params, null, restriction);

        assertEquals(DOUBLE_FILTER + CREATOR_FILTER, filter);
    }

    @Test
    public void testCreateDeclareParameters() throws Exception {
        String actual = QueryUtil.createDeclareParameters(new Object[]{10});
        assertEquals(SINGLE_DECLARATION, actual);

        actual = QueryUtil.createDeclareParameters(new Object[]{10, "str"});
        assertEquals(DOUBLE_DECLARATION, actual);
    }

    @Test
    public void testCreateParamDeclarationWithRestriction() {
        Object[] values = {10, "str"};

        InstanceSecurityRestriction restriction = new InstanceSecurityRestriction();
        restriction.setByOwner(true);

        String filter = QueryUtil.createDeclareParameters(values, restriction);
        assertEquals(DOUBLE_DECLARATION + RESTRICTION_DECLARATION, filter);
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

    @Test
    public void shouldCreateFiltersAndParamDeclarationForRanges() {
        DateTime now = DateTime.now();
        DateTime later = now.plusHours(2);
        Range<DateTime> range = new Range<>(now, later);

        Object[] values = new Object[]{range, true};

        String filter = QueryUtil.createFilter(new String[]{"prop1", "prop2"}, values, null);
        assertEquals("prop1>=param0lb && prop1<=param0ub && prop2==param1", filter);

        String paramDeclaration = QueryUtil.createDeclareParameters(values);
        assertEquals("org.joda.time.DateTime param0lb, org.joda.time.DateTime param0ub, java.lang.Boolean param1", paramDeclaration);
    }

    @Test
    public void shouldCreateFiltersAndParamDeclarationForSets() {
        HashSet<String> set = new HashSet<>(asList("one", "two", "three"));

        Object[] values = new Object[]{set, true};

        String filter = QueryUtil.createFilter(new String[]{"prop1", "prop2"}, values, null);
        assertEquals("(prop1==param0_0 || prop1==param0_1 || prop1==param0_2) && prop2==param1", filter);

        String paramDeclaration = QueryUtil.createDeclareParameters(values);
        assertEquals("java.lang.String param0_0, java.lang.String param0_1, java.lang.String param0_2, java.lang.Boolean param1",
                paramDeclaration);
    }
}
