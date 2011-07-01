package org.motechproject.server.pillreminder.service;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.pillreminder.util.Util.getEndDateAfter;

public class PillReminderServiceTest {
    PillReminderService service;

    @Mock
    private AllPillRegimens allPillRegimens;
    @Mock
    private MotechSchedulerService schedulerService;

    @Before
    public void setUp() {
        initMocks(this);
        service = new PillReminderServiceImpl(allPillRegimens, schedulerService);
    }

    @Test
    public void shouldCreateAPillRegimenFromRequestAndPersist() {
        Date startDate = new Date();
        Date endDate = getEndDateAfter(startDate, 2);
        String externalId = "123";

        MedicineRequest medicineRequest1 = new MedicineRequest("m1", startDate, endDate);
        MedicineRequest medicineRequest2 = new MedicineRequest("m2", startDate, getEndDateAfter(startDate, 4));
        List<MedicineRequest> medicineRequests = asList(medicineRequest1, medicineRequest2);

        DosageRequest dosageRequest = new DosageRequest(9, 5, medicineRequests);
        PillRegimenRequest pillRegimenRequest = new PillRegimenRequest(externalId, 5, 20, asList(dosageRequest));

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
