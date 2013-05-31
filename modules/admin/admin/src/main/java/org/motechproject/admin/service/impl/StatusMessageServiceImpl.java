package org.motechproject.admin.service.impl;

import org.joda.time.DateTime;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.ActionType;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.notification.EmailNotifier;
import org.motechproject.admin.repository.AllNotificationRules;
import org.motechproject.admin.repository.AllStatusMessages;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("statusMessageService")
public class StatusMessageServiceImpl implements StatusMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(StatusMessageServiceImpl.class);

    @Autowired
    private AllStatusMessages allStatusMessages;

    @Autowired
    private AllNotificationRules allNotificationRules;

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired
    private UIFrameworkService uiFrameworkService;

    @Autowired
    private EventRelay eventRelay;

    @Autowired
    private EmailNotifier emailNotifier;

    @Override
    public List<StatusMessage> getActiveMessages() {
        List<StatusMessage> result = allStatusMessages.getActiveMessages();

        Collections.sort(result, new Comparator<StatusMessage>() {
            @Override
            public int compare(StatusMessage o1, StatusMessage o2) {
            return o2.getDate().compareTo(o1.getDate()); // order by date, descending
            }
        });

        return result;
    }

    @Override
    public List<StatusMessage> getAllMessages() {
        List<StatusMessage> statusMessages = new ArrayList<>();
        if (getAllStatusMessages() == null) {
            StatusMessage noDbMessage = new StatusMessage("{noDB}", "", Level.ERROR);
            statusMessages.add(noDbMessage);
        } else {
            statusMessages = allStatusMessages.getAll();
        }

        return statusMessages;
    }

    @Override
    public void postMessage(StatusMessage message) {
        validateMessage(message);
        if (getAllStatusMessages() != null) {
            allStatusMessages.add(message);
        }

        if (message.getLevel() == Level.CRITICAL) {
            uiFrameworkService.moduleNeedsAttention("admin", "messages", "");
            uiFrameworkService.moduleNeedsAttention(message.getModuleName(), message.getText());
            sendNotifications(message);
        }
    }

    @Override
    public void postMessage(String text, String moduleName, Level level) {
        StatusMessage message = new StatusMessage(text, moduleName, level,  defaultTimeout());
        postMessage(message);
    }

    @Override
    public void postMessage(String text, String moduleName, Level level, DateTime timeout) {
        StatusMessage message = new StatusMessage(text, moduleName, level, timeout);
        postMessage(message);
    }

    @Override
    public void info(String text, String moduleName) {
        postMessage(text, moduleName, Level.INFO, defaultTimeout());
    }

    @Override
    public void info(String text, String moduleName, DateTime timeout) {
        postMessage(text, moduleName, Level.INFO, timeout);
    }

    @Override
    public void error(String text, String moduleName) {
        postMessage(text, moduleName, Level.ERROR);
    }

    @Override
    public void error(String text, String moduleName, DateTime timeout) {
        postMessage(text, moduleName, Level.ERROR, timeout);
    }

    @Override
    public void debug(String text, String moduleName) {
        postMessage(text, moduleName, Level.DEBUG);
    }

    @Override
    public void debug(String text, String moduleName, DateTime timeout) {
        postMessage(text, moduleName, Level.DEBUG, timeout);
    }

    @Override
    public void warn(String text, String moduleName) {
        postMessage(text, moduleName, Level.WARN);
    }

    @Override
    public void warn(String text, String moduleName, DateTime timeout) {
        postMessage(text, moduleName, Level.WARN, timeout);
    }

    @Override
    public void critical(String text, String moduleName) {
        postMessage(text, moduleName, Level.CRITICAL);
    }

    @Override
    public void critical(String text, String moduleName, DateTime timeout) {
        postMessage(text, moduleName, Level.CRITICAL, timeout);
    }

    @Override
    public void removeMessage(StatusMessage message) {
        allStatusMessages.remove(message);
    }

    @Override
    public void saveRule(NotificationRule notificationRule) {
        NotificationRule existing = (notificationRule.getId() == null) ? null :
                allNotificationRules.get(notificationRule.getId());

        if (existing == null) {
            allNotificationRules.add(notificationRule);
        } else {
            allNotificationRules.update(notificationRule);
        }
    }

    @Override
    public void removeNotificationRule(String id) {
        NotificationRule notificationRule = allNotificationRules.get(id);
        if (notificationRule != null) {
            allNotificationRules.remove(notificationRule);
        }
    }

    @Override
    public List<NotificationRule> getNotificationRules() {
        return allNotificationRules.getAll();
    }

    @Override
    public void saveNotificationRules(List<NotificationRule> notificationRules) {
        for (NotificationRule notificationRule : notificationRules) {
            NotificationRule existing = (notificationRule.getId() == null) ? null :
                    allNotificationRules.get(notificationRule.getId());

            if (existing == null) {
                allNotificationRules.add(notificationRule);
            } else {
                existing.setActionType(notificationRule.getActionType());
                existing.setRecipient(notificationRule.getRecipient());
                allNotificationRules.update(existing);
            }
        }
    }

    private AllStatusMessages getAllStatusMessages() {
        return allStatusMessages;
    }

    private void validateMessage(StatusMessage message) {
        if (message.getText() == null) {
            throw new IllegalArgumentException("Message text cannot be null");
        } else if (message.getTimeout() == null || message.getTimeout().isBeforeNow()) {
            throw new IllegalArgumentException("Timeout cannot be null or a past date");
        } else if (message.getLevel() == null) {
            throw new IllegalArgumentException("Message level cannot be null");
        }
    }

    private DateTime defaultTimeout() {
        DateTime timeout;
        String timeoutStr = platformSettingsService.getPlatformSettings().getStatusMsgTimeout();
        try {
            Integer timeoutSecs = Integer.parseInt(timeoutStr);
            timeout = DateTime.now().plusSeconds(timeoutSecs);
        } catch (RuntimeException e) {
            LOG.error("Invalid timeout setting - " + timeoutStr);
            timeout = DateTime.now().plusMinutes(1);
        }
        return timeout;
    }

    private void sendNotifications(StatusMessage message) {
        List<String> smsRecipients = new ArrayList<>();

        for (NotificationRule notificationRule : allNotificationRules.getAll()) {
            if (notificationRule.getActionType() == ActionType.SMS) {
                smsRecipients.add(notificationRule.getRecipient());
            } else if (notificationRule.getActionType() == ActionType.EMAIL) {
                emailNotifier.send(message, notificationRule.getRecipient());
            }
        }

        if (!smsRecipients.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            params.put("number",  smsRecipients);
            params.put("message", String.format("Motech Critical: [%s] %s", message.getModuleName(), message.getText()));

            MotechEvent smsEvent = new MotechEvent("SendSMS", params);
            eventRelay.sendEventMessage(smsEvent);
        }
    }
}
