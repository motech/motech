package org.motechproject.mds.web.matcher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.web.ExampleData;
import org.springframework.context.MessageSource;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AvailableTypeMatcherTest {
    private static final List<AvailableTypeDto> TYPES = new ExampleData().getTypes();

    @Mock
    private MessageSource messageSource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        for (AvailableTypeDto type : TYPES) {
            String displayName = type.getType().getDisplayName();
            String name = displayName.substring(displayName.lastIndexOf('.') + 1);

            when(messageSource.getMessage(displayName, null, null)).thenReturn(name);
        }
    }

    @Test
    public void shouldMatchAllTypesIfTermIsBlank() throws Exception {
        matchTypes("    ", 8, 0);
    }

    @Test
    public void shouldMatchTypeByDisplayName() throws Exception {
        matchTypes("Bool", 1, 7);
        matchTypes("Dat", 2, 6);
        matchTypes("D", 3, 5);
    }

    @Test
    public void shouldNotMatchTypeByDisplayName() throws Exception {
        matchTypes("1234", 0, 8);
    }

    private void matchTypes(String term, int matchedCount, int noMatchedCount) {
        AvailableTypeMatcher matcher = new AvailableTypeMatcher(term, messageSource);
        int falseCout = 0;
        int trueCount = 0;

        for (AvailableTypeDto entity : TYPES) {
            if (matcher.evaluate(entity)) {
                ++trueCount;
            } else {
                ++falseCout;
            }
        }

        assertEquals("The number of matched types is incorrect", matchedCount, trueCount);
        assertEquals("The number of no matched types is incorrect", noMatchedCount, falseCout);
    }
}
