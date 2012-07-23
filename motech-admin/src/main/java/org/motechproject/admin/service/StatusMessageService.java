package org.motechproject.admin.service;

import org.joda.time.DateTime;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;

import java.util.List;

public interface StatusMessageService {

    List<StatusMessage> getActiveMessages();

    List<StatusMessage> getAllMessages();

    void postMessage(StatusMessage message);

    void postMessage(String text, Level level);

    void postMessage(String text, Level level, DateTime timeout);

    void info(String text);

    void info(String text, DateTime timeout);

    void error(String text);

    void error(String text, DateTime timeout);

    void debug(String text);

    void debug(String text, DateTime timeout);

    void warn(String text);

    void warn(String text, DateTime timeout);

    void ok(String text);

    void ok(String text, DateTime timeout);
}
