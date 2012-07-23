package org.motechproject.admin.service.impl;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.repository.AllStatusMessages;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service("statusMessageService")
public class StatusMessageServiceImpl implements StatusMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(StatusMessageServiceImpl.class);

    AllStatusMessages allStatusMessages;

    @Autowired
    PlatformSettingsService platformSettingsService;

    @Override
    public List<StatusMessage> getActiveMessages() {
        List<StatusMessage> result = new ArrayList<>();
        for (StatusMessage message : getAllMessages()) {
            if (message.getTimeout().isAfterNow()) {
                result.add(message);
            }
        }

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
            StatusMessage noDbMessage = new StatusMessage("{noDB}", Level.ERROR);
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
    }

    @Override
    public void postMessage(String text, Level level) {
        StatusMessage message = new StatusMessage(text, level);
        postMessage(message);
    }

    @Override
    public void postMessage(String text, Level level, DateTime timeout) {
        StatusMessage message = new StatusMessage(text, level, timeout);
        postMessage(message);
    }

    @Override
    public void info(String text) {
        postMessage(text, Level.INFO);
    }

    @Override
    public void info(String text, DateTime timeout) {
        postMessage(text, Level.INFO, timeout);
    }

    @Override
    public void error(String text) {
        postMessage(text, Level.ERROR);
    }

    @Override
    public void error(String text, DateTime timeout) {
        postMessage(text, Level.ERROR, timeout);
    }

    @Override
    public void debug(String text) {
        postMessage(text, Level.DEBUG);
    }

    @Override
    public void debug(String text, DateTime timeout) {
        postMessage(text, Level.DEBUG, timeout);
    }

    @Override
    public void warn(String text) {
        postMessage(text, Level.WARN);
    }

    @Override
    public void warn(String text, DateTime timeout) {
        postMessage(text, Level.WARN, timeout);
    }

    @Override
    public void ok(String text) {
        postMessage(text, Level.OK);
    }

    @Override
    public void ok(String text, DateTime timeout) {
        postMessage(text, Level.OK, timeout);
    }

    private AllStatusMessages getAllStatusMessages() {
        if (allStatusMessages == null) {
            try {
                CouchDbConnector connector = platformSettingsService.getCouchConnector("motech-admin");
                allStatusMessages = new AllStatusMessages(connector);
            } catch (RuntimeException e) {
                LOG.error("No db connection");
            }
        }
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


}
