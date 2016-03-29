package org.motechproject.mds.filter;

import java.util.Arrays;
import java.util.List;

/**
 * Represents boolean values (YES/NO) for filtering data in MDS Data Browser.
 * Provides proper value, param and operator for value.
 */
public class BooleanFilterValue extends FilterValue {

    public static final List<String> BOOLEAN_FILTER_VALUES = Arrays.asList(YES, NO);

    public BooleanFilterValue(String value) {
        super.setValue(value);
    }

    public Object valueForQuery() {
        switch (super.getValue()) {
            case YES:
                return Boolean.TRUE;
            case NO:
                return Boolean.FALSE;
            default:
                return super.getValue();
        }
    }

    public List<String> operatorForQueryFilter() {
        return Arrays.asList("==");
    }

    public String paramTypeForQuery() {
        return Boolean.class.getName();
    }

}
