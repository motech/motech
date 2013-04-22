package org.motechproject.admin.email;

import org.motechproject.admin.domain.StatusMessage;

public interface EmailSender {

    void sendCriticalNotificationEmail(String address, StatusMessage message);
}
