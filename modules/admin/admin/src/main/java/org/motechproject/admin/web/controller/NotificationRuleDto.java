package org.motechproject.admin.web.controller;

import org.motechproject.admin.domain.NotificationRule;

import java.util.ArrayList;
import java.util.List;

public class NotificationRuleDto {

    private List<String> idsToRemove = new ArrayList<>();
    private List<NotificationRule> notificationRules = new ArrayList<>();

    public List<String> getIdsToRemove() {
        return idsToRemove;
    }

    public void setIdsToRemove(List<String> idsToRemove) {
        this.idsToRemove = idsToRemove;
    }

    public List<NotificationRule> getNotificationRules() {
        return notificationRules;
    }

    public void setNotificationRules(List<NotificationRule> notificationRules) {
        this.notificationRules = notificationRules;
    }
}
