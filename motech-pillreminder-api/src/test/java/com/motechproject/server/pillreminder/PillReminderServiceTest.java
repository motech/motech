package com.motechproject.server.pillreminder;

import com.motechproject.server.pillreminder.contract.DosageRequest;
import com.motechproject.server.pillreminder.contract.PillRegimenRequest;
import com.motechproject.server.pillreminder.domain.Dosage;
import com.motechproject.server.pillreminder.domain.Medicine;
import com.motechproject.server.pillreminder.domain.PillRegimen;
import com.motechproject.server.pillreminder.domain.Reminder;
import com.motechproject.server.pillreminder.repository.AllPillRegimens;
import com.motechproject.server.pillreminder.service.PillReminderService;
import com.motechproject.server.pillreminder.service.PillReminderServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.*;

import static com.motechproject.server.pillreminder.util.TestUtil.newDate;
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
        Date date1 = newDate(2011, 5, 20);
        Date date2 = newDate(2011, 5, 21);
        String externalId = "123";
        String medicine1Name = "m1";
        String medicine2Name = "m2";
        DosageRequest dosageRequest = new DosageRequest(Arrays.asList(medicine1Name, medicine2Name), Arrays.asList(date1, date2));
        PillRegimenRequest pillRegimenRequest = new PillRegimenRequest(externalId, date1, date2, Arrays.asList(dosageRequest));

        service.createNew(pillRegimenRequest);

        Set<Dosage> expectedDosages = new HashSet<Dosage>();
        Set<Medicine> expectedMedicines = new HashSet<Medicine>();
        expectedMedicines.add(new Medicine(medicine1Name));
        expectedMedicines.add(new Medicine(medicine2Name));

        Set<Reminder> expectedReminders = new HashSet<Reminder>();
        expectedReminders.add(new Reminder(date1));
        expectedReminders.add(new Reminder(date2));

        Dosage expectedDosage = new Dosage(expectedMedicines, expectedReminders);
        expectedDosages.add(expectedDosage);

        PillRegimen expectedPillRegimen = new PillRegimen(externalId, date1, date2, expectedDosages);

        verify(allPillRegimens).add(eq(expectedPillRegimen));
    }

}
