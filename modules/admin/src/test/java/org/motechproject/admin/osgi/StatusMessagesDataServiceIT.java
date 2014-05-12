package org.motechproject.admin.osgi;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.service.StatusMessagesDataService;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class StatusMessagesDataServiceIT extends BasePaxIT {

    @Inject
    private StatusMessagesDataService dataService;

    @After
    public void tearDown() {
        dataService.deleteAll();
    }

    @Test
    public void shouldPerformCrudOperations() {
        StatusMessage statusMessage1 = new StatusMessage("test", "module", Level.ERROR);
        StatusMessage statusMessage2 = new StatusMessage("test2", "module2", Level.INFO);

        dataService.create(statusMessage1);
        dataService.create(statusMessage2);

        List<StatusMessage> result = dataService.retrieveAll();

        assertEquals(asList("test", "test2"), extract(result, on(StatusMessage.class).getText()));
        assertEquals(asList("module", "module2"), extract(result, on(StatusMessage.class).getModuleName()));
        assertEquals(asList(Level.ERROR, Level.INFO), extract(result, on(StatusMessage.class).getLevel()));

        statusMessage1.setText("test_changed");
        dataService.update(statusMessage1);
        dataService.delete(statusMessage2);

        result = dataService.retrieveAll();

        assertEquals(asList("test_changed"), extract(result, on(StatusMessage.class).getText()));
        assertEquals(asList("module"), extract(result, on(StatusMessage.class).getModuleName()));
        assertEquals(asList(Level.ERROR), extract(result, on(StatusMessage.class).getLevel()));
    }

    @Test
    public void shouldRetrieveMessagesInRange() {
        final DateTime now = DateUtil.now();
        final DateTime max = new DateTime(Long.MAX_VALUE);
        StatusMessage inactiveMsg = new StatusMessage("inactive", "inactiveModule", Level.INFO, now.minusDays(1));
        StatusMessage activeMsq = new StatusMessage("active", "activeModule", Level.ERROR, now.plusDays(1));
        StatusMessage activeMsq2 = new StatusMessage("active2", "activeModule", Level.ERROR, now.plusDays(2));

        dataService.create(activeMsq);
        dataService.create(activeMsq2);
        dataService.create(inactiveMsg);

        List<StatusMessage> result = dataService.findByTimeout(new Range<>(now, max));

        assertEquals(asList("active", "active2"), extract(result, on(StatusMessage.class).getText()));
    }
}
