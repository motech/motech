package org.motechproject.mds.jdo;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;

import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

public abstract class DateTimeValueGeneratorTest extends AbstractObjectValueGeneratorTest<DateTime> {
    private static final DateTime CURRENT_TIME = DateTime.now();
    private static final DateTime PAST_TIME = CURRENT_TIME.minusHours(7);

    @Before
    public void setUp() throws Exception {
        fakeNow(CURRENT_TIME);
    }

    @After
    public void tearDown() {
        stopFakingTime();
    }

    @Override
    protected DateTime getExpectedValue(boolean isNull) {
        return isNull ? CURRENT_TIME : PAST_TIME;
    }
}
