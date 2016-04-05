package org.motechproject.mds.filter;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Represents Date values used for filtering data in MDS Data Browser.
 * Provides proper value, param and operator for value.
 */
public class DateFilterValue extends FilterValue {

    public static final List<String> DATE_FILTER_VALUES = Arrays.asList(TODAY, PAST_7_DAYS, THIS_MONTH, THIS_YEAR);

    public DateFilterValue(String value) {
        super.setValue(value);
    }

    @Override
    public Object valueForQuery() {
        switch (super.getValue()) {
            case TODAY:
                return new DateMidnight(DateUtil.now()).toDateTime();
            case PAST_7_DAYS:
                return new DateMidnight(DateUtil.now()).minusDays(7).toDateTime();
            case THIS_MONTH:
                return new DateMidnight(DateUtil.now()).withDayOfMonth(1).toDateTime();
            case THIS_YEAR:
                return new DateMidnight(DateUtil.now()).withDayOfYear(1).toDateTime();
            default:
                return super.getValue();
        }
    }

    @Override
    public String paramTypeForQuery() {
        return DateTime.class.getName();
    }

    @Override
    public List<String> operatorForQueryFilter() {
        return Arrays.asList(">=");
    }
}
