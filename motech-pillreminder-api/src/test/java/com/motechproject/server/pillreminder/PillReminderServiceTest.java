package com.motechproject.server.pillreminder;

import com.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import com.motechproject.server.pillreminder.repository.AllPillRegimens;
import com.motechproject.server.pillreminder.service.PillReminderServiceImpl;
import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

public class PillReminderServiceTest {

    PillReminderServiceImpl service;
    @Mock
    PillRegimenBuilder builder;
    @Mock
    private AllPillRegimens repository;

    @Before
    public void setUp() {
        initMocks(this);
        service = new PillReminderServiceImpl(repository);
    }

}
