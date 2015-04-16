package org.motechproject.security.service;

public interface SettingService {

    /**
     * Returns whether email is required for creating a user
     */
    boolean getEmailRequired();
}
