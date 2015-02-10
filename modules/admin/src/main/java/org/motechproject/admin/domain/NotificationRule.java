package org.motechproject.admin.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.admin.messages.ActionType;
import org.motechproject.admin.messages.Level;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIDisplayable;

/**
 * A notification rule persisted in the database. Represents a rule for sending out a single notification after
 * a matching {@link org.motechproject.admin.domain.StatusMessage} is registered in the system. The messages is
 * matches against its level and the module to which it is tied to.
 * This class  also contains information about this notification's recipient and the {@link ActionType} representing a method
 * used for notifying the recipient.
 *
 * @see org.motechproject.admin.domain.StatusMessage
 */
@Entity(recordHistory = true)
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

    @Field(required = false)
    @UIDisplayable
    private String moduleName;

    public NotificationRule() {
        this(null, null, Level.CRITICAL, null);
    }

    /**
     * Constructor.
     *
     * @param recipient the recipient of the notification
     * @param actionType the type of action which will be performed
     * @param level the minimal level for which the notification will trigger
     * @param moduleName the module name for which this rule will trigger, leave null or blank for every module
     */
    public NotificationRule(String recipient, ActionType actionType, Level level, String moduleName) {
        this.recipient = recipient;
        this.actionType = actionType;
        this.level = level;
        this.moduleName = moduleName;
    }

    /**
     * @return the database ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the database ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the recipient of the notification
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * @param recipient the recipient of the notification
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * @return the action that should be performed for this notification rule
     */
    public ActionType getActionType() {
        return actionType;
    }

    /**
     * @param actionType the action that should be performed for this notification rule
     */
    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    /**
     * @return the minimal level for which the notification will trigger
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @param level the minimal level for which the notification will trigger
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * @return the module name for which this rule will trigger, null or blank for every module
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * @param moduleName the module name for which this rule will trigger, leave null or blank for every module
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Checks if the message matches this rule.
     *
     * @param message the message which will be checked against this rule
     * @return true if message matches this notification rule, false otherwise
     */
    public boolean matches(StatusMessage message) {
        return (level == null || level.containsLevel(message.getLevel())) &&
                (StringUtils.isBlank(moduleName) || StringUtils.equals(moduleName, message.getModuleName()));
    }
}
