package org.motechproject.admin.osgi;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.messages.ActionType;
import org.motechproject.admin.service.NotificationRulesDataService;
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
public class NotificationRulesDataServiceIT extends BasePaxIT {

    @Inject
    private NotificationRulesDataService dataService;

    @After
    public void tearDown() {
        dataService.deleteAll();
    }

    @Test
    public void shouldPerformCrudOperations() {
        NotificationRule notificationRule = new NotificationRule("recip", ActionType.EMAIL);
        NotificationRule notificationRule2 = new NotificationRule("recip2", ActionType.SMS);

        dataService.create(notificationRule);
        dataService.create(notificationRule2);

        List<NotificationRule> notificationRules = dataService.retrieveAll();
        assertEquals(asList("recip", "recip2"), extract(notificationRules, on(NotificationRule.class).getRecipient()));
        assertEquals(asList(ActionType.EMAIL, ActionType.SMS), extract(notificationRules, on(NotificationRule.class).getActionType()));

        notificationRule.setRecipient("recip3");
        dataService.update(notificationRule);

        notificationRules = dataService.retrieveAll();
        assertEquals(asList("recip3", "recip2"), extract(notificationRules, on(NotificationRule.class).getRecipient()));
        assertEquals(asList(ActionType.EMAIL, ActionType.SMS), extract(notificationRules, on(NotificationRule.class).getActionType()));

        dataService.delete(notificationRule);

        notificationRules = dataService.retrieveAll();
        assertEquals(asList("recip2"), extract(notificationRules, on(NotificationRule.class).getRecipient()));
        assertEquals(asList(ActionType.SMS), extract(notificationRules, on(NotificationRule.class).getActionType()));
    }
}
