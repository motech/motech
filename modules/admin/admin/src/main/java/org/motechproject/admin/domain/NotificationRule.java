package org.motechproject.admin.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.admin.messages.ActionType;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

/**
 * A notification rule persisted in the database. Represents a rule for sending out a single notification.
 * Contains information about this notification's recipient and the {@link ActionType} representing a method
 * used for notifying the recipient.
 */
@TypeDiscriminator("doc.type === 'NotificationRule'")
public class NotificationRule extends MotechBaseDataObject {

    private static final long serialVersionUID = 347299441797667988L;

    private String recipient;
    private ActionType actionType;

    public NotificationRule() {
    }

    public NotificationRule(String recipient, ActionType actionType) {
        this.recipient = recipient;
        this.actionType = actionType;
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
