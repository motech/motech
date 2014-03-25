package org.motechproject.mds.filter;

import org.codehaus.jackson.annotate.JsonCreator;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a method of filtering.
 */
public enum FilterType {
    ALL, YES, NO, TODAY, PAST_7_DAYS, THIS_MONTH, THIS_YEAR;

    private static final List<FilterType> BOOLEAN_FILTER_TYPES = Arrays.asList(ALL, YES, NO);
    private static final List<FilterType> DATE_FILTER_TYPES = Arrays.asList(ALL, TODAY, PAST_7_DAYS, THIS_MONTH, THIS_YEAR);

    public Object valueForQuery() {
        switch (this) {
            case YES:
                return Boolean.TRUE;
            case NO:
                return Boolean.FALSE;
            case TODAY:
                return new DateMidnight(DateUtil.now()).toDateTime();
            case PAST_7_DAYS:
                return new DateMidnight(DateUtil.now()).minusDays(7).toDateTime();
            case THIS_MONTH:
                return new DateMidnight(DateUtil.now()).withDayOfMonth(1).toDateTime();
            case THIS_YEAR:
                return new DateMidnight(DateUtil.now()).withDayOfYear(1).toDateTime();
            default:
                return null;
        }
    }

    public String paramTypeForQuery() {
        switch (this) {
            case YES:
            case NO:
                return Boolean.class.getName();
            case TODAY:
            case PAST_7_DAYS:
            case THIS_MONTH:
            case THIS_YEAR:
                return DateTime.class.getName();
            default:
                return "";
        }
    }

    public String operatorForQueryFilter() {
        switch (this) {
            case YES:
            case NO:
                return "==";
            case TODAY:
            case PAST_7_DAYS:
            case THIS_MONTH:
            case THIS_YEAR:
                return ">=";
            default:
                return "";
        }
    }

    public static List<FilterType> availableFilterTypes(Class<?> clazz) {
        return availableFilterTypes(clazz.getName());
    }

    public static List<FilterType> availableFilterTypes(String clazz) {
        switch (clazz) {
            case "java.lang.Boolean":
                return BOOLEAN_FILTER_TYPES;
            case "java.util.Date":
            case "org.joda.time.DateTime":
                return DATE_FILTER_TYPES;
            default:
                return Collections.emptyList();
        }
    }

    @JsonCreator
    public static FilterType fromString(String str) {
        return FilterType.valueOf(str);
    }
}
