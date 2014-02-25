package org.motechproject.mds.web.comparator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.MessageSource;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mds.dto.TypeDto.DOUBLE;
import static org.motechproject.mds.dto.TypeDto.INTEGER;

public class TypeDisplayNameComparatorTest {
    @Mock
    private MessageSource messageSource;

    private TypeDisplayNameComparator comparator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        comparator = new TypeDisplayNameComparator(messageSource);
    }

    @Test
    public void shouldCompareTwoEntities() throws Exception {
        when(messageSource.getMessage(INTEGER.getDisplayName(), null, null))
                .thenReturn(INTEGER.getDisplayName());
        when(messageSource.getMessage(DOUBLE.getDisplayName(), null, null))
                .thenReturn(DOUBLE.getDisplayName());

        assertTrue(comparator.compare(INTEGER, DOUBLE) > 0);
        assertTrue(comparator.compare(DOUBLE, INTEGER) < 0);
        assertTrue(comparator.compare(INTEGER, INTEGER) == 0);
        assertTrue(comparator.compare(DOUBLE, DOUBLE) == 0);
    }
}
