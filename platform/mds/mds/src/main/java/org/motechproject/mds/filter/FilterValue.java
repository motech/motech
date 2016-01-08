package org.motechproject.mds.filter;

import org.codehaus.jackson.annotate.JsonCreator;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a method of filtering.
 */
public abstract class FilterValue {

    public static final String YES = "YES";
    public static final String NO = "NO";
    public static final String TODAY = "TODAY";
    public static final String PAST_7_DAYS = "PAST_7_DAYS";
    public static final String THIS_MONTH = "THIS_MONTH";
    public static final String THIS_YEAR = "THIS_YEAR";
    public static final String ALL = "ALL";

    public static final List<String> BOOLEAN_FILTER_VALUES = Arrays.asList(YES, NO);

    public static final List<String> DATE_FILTER_VALUES = Arrays.asList(TODAY, PAST_7_DAYS, THIS_MONTH, THIS_YEAR);

    private String value;

    public abstract Object valueForQuery();

    public abstract String paramTypeForQuery();

    public abstract List<String> operatorForQueryFilter();

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonCreator
    public static FilterValue fromString(String str) {
        if (BOOLEAN_FILTER_VALUES.contains(str)) {
            return new BooleanFilterValue(str);
        } else if (DATE_FILTER_VALUES.contains(str)) {
            return new DateFilterValue(str);
        } else {
            return new ComboboxFilterValue(str);
        }
    }
}
