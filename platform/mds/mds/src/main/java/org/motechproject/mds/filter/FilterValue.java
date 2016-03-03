package org.motechproject.mds.filter;

import org.codehaus.jackson.annotate.JsonCreator;

import java.util.List;

import static org.motechproject.mds.filter.BooleanFilterValue.BOOLEAN_FILTER_VALUES;
import static org.motechproject.mds.filter.DateFilterValue.DATE_FILTER_VALUES;

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

    private String value;

    /**
     * @return parameter that will be passed to the query for this filter
     */
    public abstract Object valueForQuery();

    /**
     * @return fully qualified class name of the parameters pased to the filter
     */
    public abstract String paramTypeForQuery();

    /**
     * The operator that will be used to perform database query for this filter.
     * @return a list of operators
     */
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
