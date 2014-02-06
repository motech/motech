package org.motechproject.config.core.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.MotechConfigurationException;

/**
 * This class encapsulates the SQL database configuration, composed of as db url, username and password.
 */
public class SQLDBConfig extends AbstractDBConfig {
    /**
     * @param url
     * @param username
     * @param password
     * @throws org.motechproject.config.core.MotechConfigurationException if given url is invalid.
     */
    public SQLDBConfig(String url, String username, String password) {
        super(url, username, password);
        validate();
    }

    private void validate() {
        if (StringUtils.isBlank(getUrl())) {
            throw new MotechConfigurationException("Motech SQL URL cannot be null or empty.");
        }

        if (!getUrl().matches("jdbc:(\\w+:)+//(\\w+\\.)*\\w+:\\d+/")) {
            throw new MotechConfigurationException("Motech SQL URL is invalid.");
        }
    }
}
