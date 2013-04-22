package org.motechproject.admin.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.messages.ActionType;
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
public class AllNotificationRulesIT {

    @Autowired
    private AllNotificationRules allNotificationRules;

    @After
    public void tearDown() {
        allNotificationRules.removeAll();
    }

    @Test
    public void shouldPerformCrudOperations() {
        NotificationRule notificationRule = new NotificationRule("recip", ActionType.EMAIL);
        NotificationRule notificationRule2 = new NotificationRule("recip2", ActionType.SMS);

        allNotificationRules.add(notificationRule);
        allNotificationRules.add(notificationRule2);

        List<NotificationRule> notificationRules = allNotificationRules.getAll();
        assertEquals(asList("recip", "recip2"), extract(notificationRules, on(NotificationRule.class).getRecipient()));
        assertEquals(asList(ActionType.EMAIL, ActionType.SMS), extract(notificationRules, on(NotificationRule.class).getActionType()));

        notificationRule.setRecipient("recip3");
        allNotificationRules.update(notificationRule);

        notificationRules = allNotificationRules.getAll();
        assertEquals(asList("recip3", "recip2"), extract(notificationRules, on(NotificationRule.class).getRecipient()));
        assertEquals(asList(ActionType.EMAIL, ActionType.SMS), extract(notificationRules, on(NotificationRule.class).getActionType()));

        allNotificationRules.remove(notificationRule);

        notificationRules = allNotificationRules.getAll();
        assertEquals(asList("recip2"), extract(notificationRules, on(NotificationRule.class).getRecipient()));
        assertEquals(asList(ActionType.SMS), extract(notificationRules, on(NotificationRule.class).getActionType()));
    }
}
