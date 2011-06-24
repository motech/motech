package com.motechproject.server.pillreminder;

import com.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import com.motechproject.server.pillreminder.repository.AllPillRegimens;
import com.motechproject.server.pillreminder.service.PillReminderService;
import com.motechproject.server.pillreminder.service.PillReminderServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillReminderServiceTest {

    PillReminderService service;
    @Mock
    PillRegimenBuilder builder;
    @Mock
    private AllPillRegimens allPillRegimens;

    @Before
    public void setUp() {
        initMocks(this);
        service = new PillReminderServiceImpl(allPillRegimens);
    }

    @Test
    public void shouldCreateAPillRegimenFromRequestAndActivateIt(){
         assertTrue(true);
         //TBD
    }

}
