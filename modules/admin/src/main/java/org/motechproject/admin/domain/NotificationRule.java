package org.motechproject.admin.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.admin.messages.ActionType;
import org.motechproject.admin.messages.Level;
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

    @Field(required = true, defaultValue = "CRITICAL")
    @UIDisplayable
    private Level level;

    @Field(required = true)
    @UIDisplayable
    private String moduleName;

    public NotificationRule() {
        this(null, null, Level.CRITICAL, null);
    }

    public NotificationRule(String recipient, ActionType actionType, Level level, String moduleName) {
        this.recipient = recipient;
        this.actionType = actionType;
        this.level = level;
        this.moduleName = moduleName;
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

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public boolean matches(StatusMessage message) {
        return (level == null || level.containsLevel(message.getLevel())) &&
                (StringUtils.isBlank(moduleName) || StringUtils.equals(moduleName, message.getModuleName()));
    }
}
