package org.motechproject.server.pillreminder.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
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

    @Test
    public void shouldRenewAPillRegimenFromRequest() {
        String externalId = "123";
        String randomUID = "1234567890";
        Date startDate = new Date();
        Date endDate = getDateAfter(startDate, 2);

        MedicineRequest medicineRequest1 = new MedicineRequest("m1", startDate, endDate);
        MedicineRequest medicineRequest2 = new MedicineRequest("m2", getDateAfter(startDate, 1), getDateAfter(startDate, 4));
        List<MedicineRequest> medicineRequests = asList(medicineRequest1, medicineRequest2);

        DosageRequest dosageRequest = new DosageRequest(9, 5, medicineRequests);
        PillRegimenRequest pillRegimenRequest = new PillRegimenRequest(externalId, 5, 20, asList(dosageRequest));
        PillRegimen regimen = mock(PillRegimen.class);
        final Dosage dosage = mock(Dosage.class);
        Set<Dosage> dosages = new HashSet<Dosage>() {{
            add(dosage);
        }};
        when(regimen.getDosages()).thenReturn((Set<Dosage>) dosages);
        when(dosage.getId()).thenReturn(randomUID);
        when(allPillRegimens.findByExternalId(externalId)).thenReturn(regimen);

        service.renew(pillRegimenRequest);

        verify(schedulerService).unscheduleJob(randomUID);
        verify(allPillRegimens).remove(regimen);
        verify(allPillRegimens).add(argThat(new PillRegimenArgumentMatcher()));
        verify(schedulerService, times(1)).scheduleJob(argThat(new CronSchedulableJobArgumentMatcher(startDate, getDateAfter(startDate, 4))));
    }


    @Test
    public void shouldCallAllPillRegimensToFetchMedicines() {
        List<String> expectedMedicines = Arrays.asList("m1", "m2");
        when(allPillRegimens.medicinesFor("pillRegimenId", "dosageId")).thenReturn(expectedMedicines);

        List<String> medicines = service.medicinesFor("pillRegimenId", "dosageId");

        verify(allPillRegimens).medicinesFor("pillRegimenId", "dosageId");
        assertEquals(expectedMedicines, medicines);
    }


    private class PillRegimenArgumentMatcher extends ArgumentMatcher<PillRegimen> {
        @Override
        public boolean matches(Object o) {
            PillRegimen pillRegimen = (PillRegimen) o;
            return pillRegimen.getExternalId().equals("123") && pillRegimen.getDosages().size() == 1;
        }
    }

    private class CronSchedulableJobArgumentMatcher extends ArgumentMatcher<CronSchedulableJob> {
        private Date startDate;
        private Date endDate;

        private CronSchedulableJobArgumentMatcher(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public boolean matches(Object o) {
            CronSchedulableJob schedulableJob = (CronSchedulableJob) o;
            Map<String, Object> parameters = schedulableJob.getMotechEvent().getParameters();
            Boolean allParamsPresent = parameters.containsKey(EventKeys.SCHEDULE_JOB_ID_KEY)
                    && parameters.containsKey(EventKeys.PILLREMINDER_ID_KEY)
                    && parameters.containsKey(EventKeys.DOSAGE_ID_KEY)
                    && parameters.containsKey(EventKeys.EXTERNAL_ID_KEY);

            return allParamsPresent && schedulableJob.getStartTime().equals(startDate) && schedulableJob.getEndTime().equals(endDate);
        }
    }
}
