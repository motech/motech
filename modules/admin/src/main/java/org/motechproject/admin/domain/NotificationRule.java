package org.motechproject.admin.domain;

import org.motechproject.admin.messages.ActionType;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIDisplayable;

/**
 * A notification rule persisted in the database. Represents a rule for sending out a single notification.
 * Contains information about this notification's recipient and the {@link ActionType} representing a method
 * used for notifying the recipient.
 */
@Entity
public class NotificationRule {
    private Long id;

    @Field(required = true)
    @UIDisplayable
    private String recipient;

    @Field(required = true, defaultValue = "EMAIL")
    @UIDisplayable
    private ActionType actionType;

    public NotificationRule() {
        this(null, null);
    }

    public NotificationRule(String recipient, ActionType actionType) {
        this.recipient = recipient;
        this.actionType = actionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
}
