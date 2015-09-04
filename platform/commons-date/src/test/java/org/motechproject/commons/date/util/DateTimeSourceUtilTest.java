package org.motechproject.commons.date.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.date.util.datetime.DateTimeSource;
import org.motechproject.commons.date.util.datetime.DefaultDateTimeSource;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DateTimeSourceUtilTest {

    private DateTime now = new DateTime();

    private LocalDate today = new LocalDate();

    private LocalDateTime javaNow = LocalDateTime.now();

    @Mock
    private DateTimeSource dateTimeSource;

    @Before
    public void setUp() throws Exception {
        when(dateTimeSource.now()).thenReturn(now);
        when(dateTimeSource.today()).thenReturn(today);
        when(dateTimeSource.javaTimeNow()).thenReturn(javaNow);
        DateTimeSourceUtil.setSourceInstance(dateTimeSource);
    }

    @After
    public void tearDown() throws Exception {
        DateTimeSourceUtil.setSourceInstance(new DefaultDateTimeSource());
    }

    @Test
    public void shouldUseDateSource() throws Exception {
        assertEquals(DateUtil.now(), now);
        assertEquals(DateUtil.today(), today);
        assertEquals(DateUtil.javaTimeNow(), javaNow);
    }
}
