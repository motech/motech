package org.motechproject.security.config;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.validator.PasswordValidator;
import org.motechproject.security.validator.impl.MinLengthValidatorDecorator;
import org.motechproject.security.validator.impl.PasswordValidatorManager;
import org.motechproject.server.config.domain.MotechSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The default implementation of {@link SettingService}. It retrieves
 * security settings directly from the platform {@link ConfigurationService}.
 */
@Service
public class SettingServiceImpl implements SettingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingServiceImpl.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private PasswordValidatorManager passwordValidatorManager;

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

    @Override
    public PasswordValidator getPasswordValidator() {
        MotechSettings motechSettings = configurationService.getPlatformSettings();
        String validatorName = motechSettings.getPasswordValidator();

        PasswordValidator validator = null;
        if (StringUtils.isNotBlank(validatorName)) {
            LOGGER.debug("No password validator configured");
            validator = passwordValidatorManager.getValidator(validatorName);
        }

        if (validator == null) {
            validator = passwordValidatorManager.noneValidator();
        }

        // if min pass length configured, then decorate the validator
        int minPassLength = getMinPasswordLength();
        if (minPassLength > 0) {
            validator = new MinLengthValidatorDecorator(validator, minPassLength);
        }

        return validator;
    }

    @Override
    public int getMinPasswordLength() {
        MotechSettings motechSettings = configurationService.getPlatformSettings();
        Integer minLength = motechSettings.getMinPasswordLength();
        return minLength == null ? 0 : minLength;
    }
}
