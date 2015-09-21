package org.motechproject.admin.it;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.messages.ActionType;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.mds.NotificationRulesDataService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import javax.inject.Inject;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class NotificationRulesDataServiceBundleIT extends BasePaxIT {

    @Inject
    private NotificationRulesDataService dataService;

    @After
    public void tearDown() {
        dataService.deleteAll();
    }

    @Test
    public void shouldPerformCrudOperations() {
        NotificationRule notificationRule = new NotificationRule("recip", ActionType.EMAIL, Level.CRITICAL, "admin");
        NotificationRule notificationRule2 = new NotificationRule("recip2", ActionType.SMS, Level.INFO, "tasks");

        dataService.create(notificationRule);
        dataService.create(notificationRule2);

        List<NotificationRule> notificationRules = dataService.retrieveAll();

        List<String> actual = extract(notificationRules, on(NotificationRule.class).getRecipient());
        assertThat(actual, hasItems("recip", "recip2"));

        List<ActionType> actualActions = extract(notificationRules, on(NotificationRule.class).getActionType());
        assertThat(actualActions, hasItems(ActionType.EMAIL, ActionType.SMS));

        actual = extract(notificationRules, on(NotificationRule.class).getModuleName());
        assertThat(actual, hasItems("admin", "tasks"));

        List<Level> actualLevels = extract(notificationRules, on(NotificationRule.class).getLevel());
        assertThat(actualLevels, hasItems(Level.CRITICAL, Level.INFO));

        dataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                NotificationRule notificationRuletoUpdate = dataService.retrieveAll().get(0);
                notificationRuletoUpdate.setRecipient("recip3");
                dataService.update(notificationRuletoUpdate);
            }
        });

        notificationRules = dataService.retrieveAll();

        actual = extract(notificationRules, on(NotificationRule.class).getRecipient());
        assertThat(actual, hasItems("recip3", "recip2"));

        actualActions = extract(notificationRules, on(NotificationRule.class).getActionType());
        assertThat(actualActions, hasItems(ActionType.EMAIL, ActionType.SMS));

        dataService.delete(notificationRule);

        notificationRules = dataService.retrieveAll();
        assertEquals(asList("recip2"), extract(notificationRules, on(NotificationRule.class).getRecipient()));
        assertEquals(asList(ActionType.SMS), extract(notificationRules, on(NotificationRule.class).getActionType()));
        assertEquals(asList("tasks"), extract(notificationRules, on(NotificationRule.class).getModuleName()));
        assertEquals(asList(Level.INFO), extract(notificationRules, on(NotificationRule.class).getLevel()));
    }
}
