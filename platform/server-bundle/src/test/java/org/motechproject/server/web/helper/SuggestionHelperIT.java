package org.motechproject.server.web.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:testApplicationServerBundleContext.xml")
public class SuggestionHelperIT {

    @Autowired
    private SuggestionHelper suggestionHelper;

    @Test
    public void shouldSuggestActivemqUrl() {
        assertEquals("tcp://localhost:61616", suggestionHelper.suggestActivemqUrl());
    }
}
