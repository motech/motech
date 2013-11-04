package org.motechproject.mds.web.comparator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.springframework.context.MessageSource;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mds.dto.TypeDto.DOUBLE;
import static org.motechproject.mds.dto.TypeDto.INTEGER;

public class AvailableTypeDisplayNameComparatorTest {
    @Mock
    private MessageSource messageSource;

    private AvailableTypeDisplayNameComparator comparator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        comparator = new AvailableTypeDisplayNameComparator(messageSource);
    }

    @Test
    public void shouldCompareTwoEntities() throws Exception {
        AvailableTypeDto integer = new AvailableTypeDto(null, null, INTEGER);
        AvailableTypeDto decimal = new AvailableTypeDto(null, null, DOUBLE);

        when(messageSource.getMessage(INTEGER.getDisplayName(), null, null))
                .thenReturn(INTEGER.getDisplayName());
        when(messageSource.getMessage(DOUBLE.getDisplayName(), null, null))
                .thenReturn(DOUBLE.getDisplayName());

        assertTrue(comparator.compare(integer, decimal) > 0);
        assertTrue(comparator.compare(decimal, integer) < 0);
        assertTrue(comparator.compare(integer, integer) == 0);
        assertTrue(comparator.compare(decimal, decimal) == 0);
    }
}
