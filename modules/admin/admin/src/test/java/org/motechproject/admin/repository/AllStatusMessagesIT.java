package org.motechproject.admin.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:testApplicationAdmin.xml"})
public class AllStatusMessagesIT {

    @Autowired
    private AllStatusMessages allStatusMessages;

    @After
    public void tearDown() {
        allStatusMessages.removeAll();
    }

    @Test
    public void shouldPerformCrudOperations() {
        StatusMessage statusMessage1 = new StatusMessage("test", "module", Level.ERROR);
        StatusMessage statusMessage2 = new StatusMessage("test2", "module2", Level.INFO);

        allStatusMessages.add(statusMessage1);
        allStatusMessages.add(statusMessage2);

        List<StatusMessage> result = allStatusMessages.getAll();

        assertEquals(asList("test", "test2"), extract(result, on(StatusMessage.class).getText()));
        assertEquals(asList("module", "module2"), extract(result, on(StatusMessage.class).getModuleName()));
        assertEquals(asList(Level.ERROR, Level.INFO), extract(result, on(StatusMessage.class).getLevel()));

        statusMessage1.setText("test_changed");
        allStatusMessages.update(statusMessage1);
        allStatusMessages.remove(statusMessage2);

        result = allStatusMessages.getAll();

        assertEquals(asList("test_changed"), extract(result, on(StatusMessage.class).getText()));
        assertEquals(asList("module"), extract(result, on(StatusMessage.class).getModuleName()));
        assertEquals(asList(Level.ERROR), extract(result, on(StatusMessage.class).getLevel()));
    }

    @Test
    public void shouldRetrieveActiveMessages() {
        final DateTime now = DateUtil.now();
        StatusMessage inactiveMsg = new StatusMessage("inactive", "inactiveModule", Level.INFO, now.minusDays(1));
        StatusMessage activeMsq = new StatusMessage("active", "activeModule", Level.ERROR, now.plusDays(1));
        StatusMessage activeMsq2 = new StatusMessage("active2", "activeModule", Level.ERROR, now.plusDays(2));

        allStatusMessages.add(activeMsq);
        allStatusMessages.add(activeMsq2);
        allStatusMessages.add(inactiveMsg);

        List<StatusMessage> result = allStatusMessages.getActiveMessages();

        assertEquals(asList("active", "active2"), extract(result, on(StatusMessage.class).getText()));
    }
}
