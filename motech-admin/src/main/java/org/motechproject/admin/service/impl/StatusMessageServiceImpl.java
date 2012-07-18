package org.motechproject.admin.service.impl;

import org.joda.time.DateTime;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.repository.AllStatusMessages;
import org.motechproject.admin.service.StatusMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("statusMessageService")
public class StatusMessageServiceImpl implements StatusMessageService {

    @Autowired
    AllStatusMessages allStatusMessages;

    @Override
    public List<StatusMessage> getActiveMessages() {
        List<StatusMessage> result = new ArrayList<>();
        for (StatusMessage message : getAllMessages()) {
            if (message.getTimeout().isAfterNow()) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public List<StatusMessage> getAllMessages() {
        return allStatusMessages.getAll();
    }

    @Override
    public void postMessage(StatusMessage message) {
        validateMessage(message);
        allStatusMessages.add(message);
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
