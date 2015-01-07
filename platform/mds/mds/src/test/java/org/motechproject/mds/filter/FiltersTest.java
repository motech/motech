package org.motechproject.mds.filter;


import org.joda.time.DateMidnight;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.commons.date.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FiltersTest {

    public static final String EXPECTED_FILTER = "(bool == arg0 || bool == arg1) && date >= arg2 && (list.contains(arg3) || list.contains(arg4))";
    public static final String EXPECTED_PARAMS = "java.lang.Boolean arg0,java.lang.Boolean arg1,org.joda.time.DateTime arg2,java.lang.String arg3,java.lang.String arg4";
    public static Filters filters;

    @BeforeClass
    public static void setUp() {
        Filter[] filtersAsArray = new Filter[3];

        FilterValue[] boolValues = new FilterValue[2];
        boolValues[0] = FilterValue.fromString(FilterValue.YES);
        boolValues[1] = FilterValue.fromString(FilterValue.NO);
        filtersAsArray[0] = new Filter("bool", boolValues);

        filtersAsArray[1] = new Filter("date", FilterValue.THIS_YEAR);

        FilterValue[] comboboxValues = new FilterValue[2];
        comboboxValues[0] = FilterValue.fromString("TestValue1");
        comboboxValues[1] = FilterValue.fromString("TestValue2");
        filtersAsArray[2] = new Filter("list", comboboxValues);
        filtersAsArray[2].setMultiSelect();

        filters = new Filters(filtersAsArray);
    }

    @Test
    public void shouldValuesForQuery() throws Exception {
        Object[] expected = getExpectedValues();

        Object[] result = filters.valuesForQuery();

        assertArrayEquals(expected, result);
    }

    @Test
    public void shouldRequiresFiltering() throws Exception {
        boolean result = filters.requiresFiltering();

        assertTrue(result);
    }

    @Test
    public void shouldFilterForQuery() throws Exception {
        String result = filters.filterForQuery();

        assertEquals(EXPECTED_FILTER, result);
    }

    @Test
    public void shouldParamsDeclarationForQuery() throws Exception {
        String result = filters.paramsDeclarationForQuery();

        assertEquals(EXPECTED_PARAMS, result);
    }

    private Object[] getExpectedValues() {
        List<Object> expected = new ArrayList<>();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        expected.add(new DateMidnight(DateUtil.now()).withDayOfYear(1).toDateTime());
        expected.add("TestValue1");
        expected.add("TestValue2");
        return expected.toArray();
    }



}