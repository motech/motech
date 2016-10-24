package org.motechproject.admin.service;

import org.joda.time.DateTime;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.security.SecurityConstants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Message service used to send status messages and manage notification rules.
 *
 * @see org.motechproject.admin.domain.StatusMessage
 * @see org.motechproject.admin.domain.NotificationRule
 */
public interface StatusMessageService {

    /**
     * Retrieves status messages that have not expired.
     *
     * @return list of active status messages
     */
    List<StatusMessage> getActiveMessages();

    /**
     * Retrieves all status messages, including those that expired.
     *
     * @return list of all status messages
     */
    List<StatusMessage> getAllMessages();

    /**
     * Creates a status message and posts it in the system. If the message matches any
     * notification rules, appropriate notifications will be triggered. The message
     * will be visible in the message UI until it expires.
     *
     * @param message the message to send
     */
    void postMessage(StatusMessage message);

    /**
     * Creates a status message and posts it in the system. If the message matches any
     * notification rules, appropriate notifications will be triggered. The message
     * will be visible in the message UI until it expires. The value of timeout is set
     * to the default.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     * @param level the message level
     */
    void postMessage(String text, String moduleName, Level level);

    /**
     * Creates a status message and posts it in the system. If the message matches any
     * notification rules, appropriate notifications will be triggered. The message
     * will be visible in the message UI until it expires.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     * @param level the message level
     * @param timeout the message expiry date
     */
    void postMessage(String text, String moduleName, Level level, DateTime timeout);

    /**
     * Creates a status message and posts it in the system. If the message matches any
     * notification rules, appropriate notifications will be triggered. The message
     * will be visible in the message UI until it expires.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     * @param level the message level as a string value
     * @param timeout the message expiry date
     */
    void postMessage(String text, String moduleName, String level, DateTime timeout);

    /**
     * Creates a status message and posts it in the system. If the message matches any
     * notification rules, appropriate notifications will be triggered. The message
     * will be visible in the message UI until it expires. The value of timeout is set
     * to the default.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     */
    void info(String text, String moduleName);

    /**
     * Creates a status message with INFO level and posts it in the system. If the message
     * matches any notification rules, appropriate notifications will be triggered.
     * The message will be visible in the message UI until it expires.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     * @param timeout the message expiry date
     */
    void info(String text, String moduleName, DateTime timeout);

    /**
     * Creates a status message with ERROR level and posts it in the system. If the message
     * matches any notification rules, appropriate notifications will be triggered.
     * The message will be visible in the message UI until it expires. The value of timeout
     * is set to the default.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     */
    void error(String text, String moduleName);

    /**
     * Creates a status message with ERROR level and posts it in the system. If the message
     * matches any notification rules, appropriate notifications will be triggered.
     * The message will be visible in the message UI until it expires.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     * @param timeout the message expiry date
     */
    void error(String text, String moduleName, DateTime timeout);

    /**
     * Creates a status message with DEBUG level and posts it in the system. If the message
     * matches any notification rules, appropriate notifications will be triggered.
     * The message will be visible in the message UI until it expires. The value of timeout
     * is set to the default.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     */
    void debug(String text, String moduleName);

    /**
     * Creates a status message with DEBUG level and posts it in the system. If the message
     * matches any notification rules, appropriate notifications will be triggered.
     * The message will be visible in the message UI until it expires.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     * @param timeout the message expiry date
     */
    void debug(String text, String moduleName, DateTime timeout);

    /**
     * Creates a status message with WARN level and posts it in the system. If the message
     * matches any notification rules, appropriate notifications will be triggered.
     * The message will be visible in the message UI until it expires. The value of timeout
     * is set to the default.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     */
    void warn(String text, String moduleName);

    /**
     * Creates a status message with WARN level and posts it in the system. If the message
     * matches any notification rules, appropriate notifications will be triggered.
     * The message will be visible in the message UI until it expires.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     * @param timeout the message expiry date
     */
    void warn(String text, String moduleName, DateTime timeout);

    /**
     * Creates a status message with CRITICAL level and posts it in the system. If the message
     * matches any notification rules, appropriate notifications will be triggered.
     * The message will be visible in the message UI until it expires. The value of timeout
     * is set to the default.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     */
    void critical(String text, String moduleName);

    /**
     * Creates a status message with CRITICAL level and posts it in the system. If the message
     * matches any notification rules, appropriate notifications will be triggered.
     * The message will be visible in the message UI until it expires.
     *
     * @param text the message content
     * @param moduleName the name of the module this message is related with
     * @param timeout the message expiry date
     */
    void critical(String text, String moduleName, DateTime timeout);

    /**
     * Removes the given message.
     *
     * @param message the message to remove
     */
    @PreAuthorize(SecurityConstants.MANAGE_MESSAGES)
    void removeMessage(StatusMessage message);

    /**
     * Creates or updates a notification rule. Rule is updated when it has the id field set.
     *
     *  @param notificationRule the rule to create/update
     */
    @PreAuthorize(SecurityConstants.MANAGE_MESSAGES)
    void saveRule(NotificationRule notificationRule);

    /**
     * Removes notification rule with given id. Method is deprecated because now Motech is using
     * Long type for primary key.
     *
     * @param id the id of notification rule which will be removed
     */
    @PreAuthorize(SecurityConstants.MANAGE_MESSAGES)
    @Deprecated
    void removeNotificationRule(String id);

    /**
     * Removes notification rule with the given id.
     *
     * @param id the id of notification rule which will be removed
     */
    @PreAuthorize(SecurityConstants.MANAGE_MESSAGES)
    void removeNotificationRule(Long id);

    /**
     * Retrieves all notification rules.
     *
     * @return list of all notification rules
     */
    @PreAuthorize(SecurityConstants.MANAGE_MESSAGES)
    List<NotificationRule> getNotificationRules();

    /**
     * Creates or updates notification rules. Rule is updated when it has the id field set.
     *
     * @param notificationRules the list of notification rules to create/update
     */
    @PreAuthorize(SecurityConstants.MANAGE_MESSAGES)
    void saveNotificationRules(List<NotificationRule> notificationRules);
}
