package org.motechproject.admin.service;

import org.joda.time.DateTime;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;

import java.util.List;

public interface StatusMessageService {

    List<StatusMessage> getActiveMessages();

    List<StatusMessage> getAllMessages();

    void postMessage(StatusMessage message);

    void postMessage(String text, String moduleName, Level level);

    void postMessage(String text, String moduleName, Level level, DateTime timeout);

    void info(String text, String moduleName);

    void info(String text, String moduleName, DateTime timeout);

    void error(String text, String moduleName);

    void error(String text, String moduleName, DateTime timeout);

    void debug(String text, String moduleName);

    void debug(String text, String moduleName, DateTime timeout);

    void warn(String text, String moduleName);

    void warn(String text, String moduleName, DateTime timeout);

    void critical(String text, String moduleName);

    void critical(String text, String moduleName, DateTime timeout);

    void removeMessage(StatusMessage message);

    void saveRule(NotificationRule notificationRule);

    void removeNotificationRule(String id);

    List<NotificationRule> getNotificationRules();

    void saveNotificationRules(List<NotificationRule> notificationRules);
}
