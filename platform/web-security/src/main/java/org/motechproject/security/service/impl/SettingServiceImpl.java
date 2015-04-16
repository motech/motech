package org.motechproject.security.service.impl;

import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.service.SettingService;
import org.motechproject.server.config.domain.MotechSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private ConfigurationService configurationService;

    /**
     * returns true if email is required for creating a user
     */
    @Override
    public boolean getEmailRequired() {
        MotechSettings motechSettings = configurationService.getPlatformSettings();
        return motechSettings.getEmailRequired();
    }
}
