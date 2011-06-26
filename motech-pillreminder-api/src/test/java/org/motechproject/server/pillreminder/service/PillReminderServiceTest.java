package org.motechproject.server.pillreminder.service;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.contract.ReminderRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Reminder;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.domain.Medicine;

import java.util.*;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.argThat;
import static org.motechproject.server.pillreminder.util.TestUtil.newDate;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillReminderServiceTest {
    PillReminderService service;

    @Mock
    private AllPillRegimens allPillRegimens;

    @Before
    public void setUp() {
        initMocks(this);
        service = new PillReminderServiceImpl(allPillRegimens);
    }

    @Test
    public void shouldCreateAPillRegimenFromRequestAndPersist() {
        Date startDate = newDate(2011, 5, 20);
        Date endDate = newDate(2011, 5, 21);
        String externalId = "123";
        List<String> medicineRequests = asList("m1");
        List<ReminderRequest> reminderRequests = asList(new ReminderRequest(1, 30, 5, 300));

        DosageRequest dosageRequest = new DosageRequest(medicineRequests, reminderRequests);
        PillRegimenRequest pillRegimenRequest = new PillRegimenRequest(externalId, startDate, endDate, asList(dosageRequest));

        service.createNew(pillRegimenRequest);
        verify(allPillRegimens).add(argThat(new PillRegimenArgumentMatcher()));
    }


    private class PillRegimenArgumentMatcher extends BaseMatcher<PillRegimen> {
        @Override
        public boolean matches(Object o) {
            PillRegimen pillRegimen = (PillRegimen) o;
            return pillRegimen.getExternalId().equals("123") && pillRegimen.getDosages().size() == 1;
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}
