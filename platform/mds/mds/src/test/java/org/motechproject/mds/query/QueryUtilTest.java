package org.motechproject.mds.query;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.util.SecurityUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jdo.Query;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtil.class)
public class QueryUtilTest {

    @Mock
    private Query query;

    @Mock
    private QueryParams queryParams;

    @Mock
    private InstanceSecurityRestriction restriction;

    @Test
    public void shouldSetQueryParams() {
        when(queryParams.isOrderSet()).thenReturn(true);
        when(queryParams.isPagingSet()).thenReturn(true);
        when(queryParams.getOrder()).thenReturn(new Order("field", "ascending"));
        when(queryParams.getPage()).thenReturn(2);
        when(queryParams.getPageSize()).thenReturn(10);

        QueryUtil.setQueryParams(query, queryParams);

        verify(query).setRange(10, 20);
        verify(query).setOrdering("field ascending");
    }

    @Test
    public void shouldCreateFiltersAndParamDeclarationForRanges() {
        DateTime now = DateTime.now();
        DateTime later = now.plusHours(2);
        Range<DateTime> range = new Range<>(now, later);

        String[] properties = new String[]{"prop1", "prop2"};
        Object[] values = new Object[]{range, true};

        QueryUtil.useFilter(query, properties, values);

        verify(query).setFilter("prop1>=param0lb && prop1<=param0ub && prop2 == param1");
        verify(query).declareParameters("org.joda.time.DateTime param0lb, org.joda.time.DateTime param0ub, java.lang.Boolean param1");
    }

    @Test
    public void shouldCreateFiltersAndParamDeclarationForSets() {
        HashSet<String> set = new HashSet<>(asList("one", "two", "three"));

        String[] properties = new String[]{"prop1", "prop2"};
        Object[] values = new Object[]{set, true};

        QueryUtil.useFilter(query, properties, values);

        verify(query).setFilter("(prop1 == param0_0 || prop1 == param0_1 || prop1 == param0_2) && prop2 == param1");
        verify(query).declareParameters("java.lang.String param0_0, java.lang.String param0_1, java.lang.String param0_2, java.lang.Boolean param1");
    }

    @Test
    public void shouldCreateQueriesForGivenFormats() {
        EqualProperty eqProperty = new EqualProperty<>("strProp", "text");
        CustomOperatorProperty matchesProperty1 =
                new CustomOperatorProperty<>("textField", "searchStr", "matches()");
        CustomOperatorProperty matchesProperty2 =
                new CustomOperatorProperty<>("textField2", "searchStr", "matches()");

        String pattern = "%s && (%s || %s)";
        List<Property> properties = asList(eqProperty, matchesProperty1, matchesProperty2);

        QueryUtil.useFilterFromPattern(query, pattern, properties);

        verify(query).setFilter("strProp == param0 && (textField.matches(param1) || textField2.matches(param2))");
        verify(query).declareParameters("java.lang.String param0, java.lang.String param1, java.lang.String param2");
    }
    @Test
    public void shouldReturnSearchPatterns() {
        assertEquals(".*something.*", QueryUtil.asMatchesPattern("something"));
        assertEquals(".* .*", QueryUtil.asMatchesPattern(" "));
        assertEquals("", QueryUtil.asMatchesPattern(""));
        assertNull(QueryUtil.asMatchesPattern(null));
    }

    @Test
    public void shouldSetCountResult() {
        QueryUtil.setCountResult(query);
        verify(query).setResult("count(this)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNullQueriesWhenSettingCountResult() {
        QueryUtil.setCountResult(null);
    }
}
