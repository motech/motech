package org.motechproject.security.config;

import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.MotechSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The default implementation of {@link SettingService}. It retrieves
 * security settings directly from the platform {@link ConfigurationService}.
 */
@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public boolean getEmailRequired() {
        MotechSettings motechSettings = configurationService.getPlatformSettings();
        return motechSettings.getEmailRequired();
    }

    @Override
    public int getSessionTimeout() {
        MotechSettings motechSettings = configurationService.getPlatformSettings();
        Integer sessionTimeout = motechSettings.getSessionTimeout();
        return sessionTimeout == null || sessionTimeout == 0 ? DEFAULT_SESSION_TIMEOUT : sessionTimeout;
    }
}
