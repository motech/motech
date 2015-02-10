package org.motechproject.admin.web.dto;

import org.motechproject.admin.domain.NotificationRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object for transferring notification rules between
 * the backend and frontend. Contains a list of IDs for rules that should
 * be removed and a list of notifications rules that should be saved/updated.
 *
 * @see org.motechproject.admin.domain.NotificationRule
 */
public class NotificationRuleDto {

    private List<String> idsToRemove = new ArrayList<>();
    private List<NotificationRule> notificationRules = new ArrayList<>();

    /**
     * @return list of IDs of rules that should be removed
     */
    public List<String> getIdsToRemove() {
        return idsToRemove;
    }

    /**
     * @param idsToRemove list of IDs of rules that should be removed
     */
    public void setIdsToRemove(List<String> idsToRemove) {
        this.idsToRemove = idsToRemove;
    }

    /**
     * @return list notification rules that should be persisted(saved or updated)
     */
    public List<NotificationRule> getNotificationRules() {
        return notificationRules;
    }

    /**
     * @param notificationRules list notification rules that should be persisted(saved or updated)
     */
    public void setNotificationRules(List<NotificationRule> notificationRules) {
        this.notificationRules = notificationRules;
    }
}
