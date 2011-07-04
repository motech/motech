package org.motechproject.server.pillreminder.service;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.pillreminder.util.Util.getDateAfter;

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
        Date endDate = getDateAfter(startDate, 2);
        String externalId = "123";

        MedicineRequest medicineRequest1 = new MedicineRequest("m1", startDate, endDate);
        MedicineRequest medicineRequest2 = new MedicineRequest("m2", getDateAfter(startDate, 1), getDateAfter(startDate, 4));
        List<MedicineRequest> medicineRequests = asList(medicineRequest1, medicineRequest2);

        DosageRequest dosageRequest = new DosageRequest(9, 5, medicineRequests);
        PillRegimenRequest pillRegimenRequest = new PillRegimenRequest(externalId, 5, 20, asList(dosageRequest));

        service.createNew(pillRegimenRequest);
        verify(allPillRegimens).add(argThat(new PillRegimenArgumentMatcher()));
        verify(schedulerService, times(1)).scheduleJob(argThat(new CronSchedulableJobArgumentMatcher(startDate, getDateAfter(startDate, 4))));
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

    private class CronSchedulableJobArgumentMatcher extends BaseMatcher<CronSchedulableJob> {

        private Date startDate;
        private Date endDate;

        private CronSchedulableJobArgumentMatcher(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public boolean matches(Object o) {
            CronSchedulableJob schedulableJob = (CronSchedulableJob) o;
            return schedulableJob.getStartTime().equals(startDate) && schedulableJob.getEndTime().equals(endDate);
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}
