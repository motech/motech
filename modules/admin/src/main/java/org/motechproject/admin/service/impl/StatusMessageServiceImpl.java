package org.motechproject.admin.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.mds.NotificationRulesDataService;
import org.motechproject.admin.mds.StatusMessagesDataService;
import org.motechproject.admin.messages.ActionType;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.notification.EmailNotifier;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.commons.api.Range;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.email.exception.EmailSendException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.osgi.web.service.UIFrameworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link StatusMessageService} interface. Class provides API for everything
 * connected with messages and notifications in admin module.
 */
@Service("statusMessageService")
public class StatusMessageServiceImpl implements StatusMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusMessageServiceImpl.class);

    private StatusMessagesDataService statusMessagesDataService;
    private NotificationRulesDataService notificationRulesDataService;
    private ConfigurationService configurationService;
    private UIFrameworkService uiFrameworkService;
    private EventRelay eventRelay;
    private EmailNotifier emailNotifier;

    @Override
    @Transactional
    public List<StatusMessage> getActiveMessages() {
        Range<DateTime> timeout = new Range<>(DateTime.now(), null);
        List<StatusMessage> result = statusMessagesDataService.findByTimeout(timeout);

        Collections.sort(result, new Comparator<StatusMessage>() {
            @Override
            public int compare(StatusMessage o1, StatusMessage o2) {
                return o2.getDate().compareTo(o1.getDate()); // order by date, descending
            }
        });

        return result;
    }

    @Override
    @Transactional
    public List<StatusMessage> getAllMessages() {
        List<StatusMessage> statusMessages = new ArrayList<>();
        if (getStatusMessagesDataService() == null) {
            StatusMessage noDbMessage = new StatusMessage("{noDB}", "", Level.ERROR);
            statusMessages.add(noDbMessage);
        } else {
            statusMessages = statusMessagesDataService.retrieveAll();
        }

        return statusMessages;
    }

    @Override
    @Transactional
    public void postMessage(StatusMessage message) {
        validateMessage(message);
        if (getStatusMessagesDataService() != null) {
            statusMessagesDataService.create(message);
        }

        if (message.getLevel() == Level.CRITICAL) {
            uiFrameworkService.moduleNeedsAttention("admin", "messages", "");
            uiFrameworkService.moduleNeedsAttention(message.getModuleName(), message.getText());
        }

        sendNotifications(message);
    }

    @Override
    @Transactional
    public void postMessage(String text, String moduleName, Level level) {
        StatusMessage message = new StatusMessage(text, moduleName, level, defaultTimeout());
        postMessage(message);
    }

    @Override
    @Transactional
    public void postMessage(String text, String moduleName, Level level, DateTime timeout) {
        StatusMessage message = new StatusMessage(text, moduleName, level, timeout);
        postMessage(message);
    }

    @Override
    @Transactional
    public void postMessage(String text, String moduleName, String level, DateTime timeout) {
        Level levelAsEnum;
        switch (level) {
            case "admin.log.level.critical":
                levelAsEnum = Level.CRITICAL;
                break;
            case "admin.log.level.debug":
                levelAsEnum = Level.DEBUG;
                break;
            case "admin.log.level.info":
                levelAsEnum = Level.INFO;
                break;
            case "admin.log.level.warn":
                levelAsEnum = Level.WARN;
                break;
            default:
                levelAsEnum = Level.ERROR;
                break;
        }
        if (timeout == null) {
            postMessage(text, moduleName, levelAsEnum);
        } else {
            postMessage(text, moduleName, levelAsEnum, timeout);
        }
    }

    @Override
    @Transactional
    public void info(String text, String moduleName) {
        postMessage(text, moduleName, Level.INFO, defaultTimeout());
    }

    @Override
    @Transactional
    public void info(String text, String moduleName, DateTime timeout) {
        postMessage(text, moduleName, Level.INFO, timeout);
    }

    @Override
    @Transactional
    public void error(String text, String moduleName) {
        postMessage(text, moduleName, Level.ERROR);
    }

    @Override
    @Transactional
    public void error(String text, String moduleName, DateTime timeout) {
        postMessage(text, moduleName, Level.ERROR, timeout);
    }

    @Override
    @Transactional
    public void debug(String text, String moduleName) {
        postMessage(text, moduleName, Level.DEBUG);
    }

    @Override
    @Transactional
    public void debug(String text, String moduleName, DateTime timeout) {
        postMessage(text, moduleName, Level.DEBUG, timeout);
    }

    @Override
    @Transactional
    public void warn(String text, String moduleName) {
        postMessage(text, moduleName, Level.WARN);
    }

    @Override
    @Transactional
    public void warn(String text, String moduleName, DateTime timeout) {
        postMessage(text, moduleName, Level.WARN, timeout);
    }

    @Override
    @Transactional
    public void critical(String text, String moduleName) {
        postMessage(text, moduleName, Level.CRITICAL);
    }

    @Override
    @Transactional
    public void critical(String text, String moduleName, DateTime timeout) {
        postMessage(text, moduleName, Level.CRITICAL, timeout);
    }

    @Override
    @Transactional
    public void removeMessage(StatusMessage message) {
        statusMessagesDataService.delete(message);
    }

    @Override
    @Transactional
    public void saveRule(NotificationRule notificationRule) {
        NotificationRule existing = (notificationRule.getId() == null) ? null :
                notificationRulesDataService.findById(notificationRule.getId());

        if (existing == null) {
            notificationRulesDataService.create(notificationRule);
        } else {
            notificationRulesDataService.update(notificationRule);
        }
    }

    @Override
    @Transactional
    public void removeNotificationRule(String id) {
        Long idAsLong = StringUtils.isNumeric(id) ? Long.parseLong(id) : null;

        if (null != idAsLong) {
            removeNotificationRule(idAsLong);
        }
    }

    @Override
    @Transactional
    public void removeNotificationRule(Long id) {
        NotificationRule notificationRule = notificationRulesDataService.findById(id);
        if (notificationRule != null) {
            notificationRulesDataService.delete(notificationRule);
        }
    }

    @Override
    @Transactional
    public List<NotificationRule> getNotificationRules() {
        return notificationRulesDataService.retrieveAll();
    }

    @Override
    @Transactional
    public void saveNotificationRules(List<NotificationRule> notificationRules) {
        for (final NotificationRule notificationRule : notificationRules) {
            notificationRulesDataService.doInTransaction(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    NotificationRule existing = (notificationRule.getId() == null) ? null :
                            notificationRulesDataService.findById(notificationRule.getId());

                    if (existing == null) {
                        notificationRulesDataService.create(notificationRule);
                    } else {
                        existing.setActionType(notificationRule.getActionType());
                        existing.setRecipient(notificationRule.getRecipient());
                        existing.setLevel(notificationRule.getLevel());
                        existing.setModuleName(notificationRule.getModuleName());
                        notificationRulesDataService.update(existing);
                    }
                }
            });
        }
    }

    private StatusMessagesDataService getStatusMessagesDataService() {
        return statusMessagesDataService;
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
        String timeoutStr = configurationService.getPlatformSettings().getStatusMsgTimeout();
        try {
            Integer timeoutSecs = Integer.parseInt(timeoutStr);
            timeout = DateTime.now().plusSeconds(timeoutSecs);
        } catch (RuntimeException e) {
            LOGGER.error("Invalid timeout setting - " + timeoutStr);
            timeout = DateTime.now().plusMinutes(1);
        }
        return timeout;
    }

    private void sendNotifications(StatusMessage message) {
        List<String> smsRecipients = new ArrayList<>();

        for (NotificationRule notificationRule : notificationRulesDataService.retrieveAll()) {
            if (notificationRule.matches(message)) {
                if (notificationRule.getActionType() == ActionType.SMS) {
                    smsRecipients.add(notificationRule.getRecipient());
                } else if (notificationRule.getActionType() == ActionType.EMAIL) {
                    try {
                        emailNotifier.send(message, notificationRule.getRecipient());
                    } catch (EmailSendException e) {
                        LOGGER.error("Error while sending notification email to {}",
                                notificationRule.getRecipient(), e);
                    }
                }
            }
        }

        if (!smsRecipients.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            params.put("recipients", smsRecipients);
            params.put("message", String.format("Motech %s message: [%s] %s", message.getLevel(),
                    message.getModuleName(), message.getText()));

            MotechEvent smsEvent = new MotechEvent("send_sms", params);
            eventRelay.sendEventMessage(smsEvent);
        }
    }

    @Autowired
    public void setStatusMessagesDataService(StatusMessagesDataService statusMessagesDataService) {
        this.statusMessagesDataService = statusMessagesDataService;
    }

    @Autowired
    public void setNotificationRulesDataService(NotificationRulesDataService notificationRulesDataService) {
        this.notificationRulesDataService = notificationRulesDataService;
    }

    @Autowired
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Autowired
    public void setUiFrameworkService(UIFrameworkService uiFrameworkService) {
        this.uiFrameworkService = uiFrameworkService;
    }

    @Autowired
    public void setEventRelay(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    @Autowired
    public void setEmailNotifier(EmailNotifier emailNotifier) {
        this.emailNotifier = emailNotifier;
    }
}
