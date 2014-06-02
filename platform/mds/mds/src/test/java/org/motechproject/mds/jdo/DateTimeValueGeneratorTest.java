package org.motechproject.mds.jdo;

import org.joda.time.DateTime;
import org.junit.Before;

public abstract class DateTimeValueGeneratorTest extends AbstractObjectValueGeneratorTest<DateTime> {
    private static final DateTime CURRENT_TIME = DateTime.now();
    private static final DateTime PAST_TIME = CURRENT_TIME.minusHours(7);

    @Before
    public void setUp() throws Exception {
        mockCurrentDate(CURRENT_TIME);
    }

    @Override
    protected DateTime getExpectedValue(boolean isNull) {
        return isNull ? CURRENT_TIME : PAST_TIME;
    }
}
