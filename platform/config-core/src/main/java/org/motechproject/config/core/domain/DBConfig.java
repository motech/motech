package org.motechproject.config.core.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.MotechConfigurationException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * <p>DBConfig encapsulates the database configuration, composed of as db url, username and password.</p>
 */
public class DBConfig extends AbstractDBConfig {
    /**
     *
     * @param url
     * @param username
     * @param password
     * @throws org.motechproject.config.core.MotechConfigurationException if given url is invalid.
     */
    public DBConfig(String url, String username, String password) {
        super(url, username, password);
        validate();
    }

    private void validate() {
        if (StringUtils.isBlank(getUrl())) {
            throw new MotechConfigurationException("Motech DB URL cannot be null or empty.");
        }

        try {
            URL urlObject = new URL(getUrl());
            urlObject.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new MotechConfigurationException("Motech DB URL is not a valid http URL.", e);
        }
    }
}
